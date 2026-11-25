package apoio;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Request/response em memória (Proxy da Servlet API) para testar as Actions sem Tomcat. */
public final class HttpFalso {

    private HttpFalso() {
    }

    public static final class Requisicao {
        public final Map<String, String> parametros = new HashMap<>();
        public final Map<String, Object> atributos = new HashMap<>();
        public String metodo = "GET";
        public String corpo = "";
        public String encaminhadoPara;

        public Requisicao com(String nome, String valor) {
            parametros.put(nome, valor);
            return this;
        }

        public String atributo(String nome) {
            return (String) atributos.get(nome);
        }

        public HttpServletRequest proxy() {
            return (HttpServletRequest) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[] {HttpServletRequest.class}, (alvo, metodoChamado, args) -> {
                        switch (metodoChamado.getName()) {
                            case "getParameter": return parametros.get((String) args[0]);
                            case "getAttribute": return atributos.get((String) args[0]);
                            case "setAttribute": atributos.put((String) args[0], args[1]); return null;
                            case "getContextPath": return "/app";
                            case "getMethod": return metodo;
                            case "getReader": return new BufferedReader(new StringReader(corpo));
                            case "getRequestDispatcher": return despachante((String) args[0]);
                            default: return padrao(metodoChamado);
                        }
                    });
        }

        private RequestDispatcher despachante(String caminho) {
            return (RequestDispatcher) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[] {RequestDispatcher.class}, (alvo, metodoChamado, args) -> {
                        if (metodoChamado.getName().equals("forward")) {
                            encaminhadoPara = caminho;
                        }
                        return null;
                    });
        }
    }

    public static final class Resposta {
        public int status = 200;
        public Integer erroEnviado;
        public String redirecionadoPara;
        public final Map<String, String> cabecalhos = new HashMap<>();
        public final StringWriter corpo = new StringWriter();

        public HttpServletResponse proxy() {
            PrintWriter escritor = new PrintWriter(corpo, true);
            return (HttpServletResponse) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[] {HttpServletResponse.class}, (alvo, metodoChamado, args) -> {
                        switch (metodoChamado.getName()) {
                            case "setStatus": status = (Integer) args[0]; return null;
                            case "getStatus": return status;
                            case "sendError": erroEnviado = (Integer) args[0]; status = erroEnviado; return null;
                            case "sendRedirect": redirecionadoPara = (String) args[0]; status = 302; return null;
                            case "setHeader": cabecalhos.put((String) args[0], (String) args[1]); return null;
                            case "getWriter": return escritor;
                            default: return padrao(metodoChamado);
                        }
                    });
        }
    }

    // métodos não simulados devolvem o valor neutro do tipo, sem NullPointerException em primitivos
    private static Object padrao(Method metodo) {
        Class<?> tipo = metodo.getReturnType();
        if (tipo == boolean.class) {
            return false;
        }
        if (tipo == int.class || tipo == long.class || tipo == short.class || tipo == byte.class) {
            return 0;
        }
        return null;
    }
}
