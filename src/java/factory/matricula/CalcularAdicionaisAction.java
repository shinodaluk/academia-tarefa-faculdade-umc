package factory.matricula;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dao.MatriculaDao;
import dao.ServicoAdicionalDao;
import factory.ICommand;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Matricula;
import model.MontadorAdicionais;
import model.Servico;
import model.ServicoAdicional;

public class CalcularAdicionaisAction implements ICommand {
    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        Gson gson = new Gson();

        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            responderErro(response, gson, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    "Este cálculo aceita apenas requisições POST.");
            return;
        }

        JsonObject entrada;
        try {
            JsonElement elemento = JsonParser.parseReader(request.getReader());
            if (!elemento.isJsonObject()) {
                responderErro(response, gson, HttpServletResponse.SC_BAD_REQUEST,
                        "O corpo da requisição deve ser um objeto JSON.");
                return;
            }
            entrada = elemento.getAsJsonObject();
        } catch (IOException | RuntimeException ex) {
            responderErro(response, gson, HttpServletResponse.SC_BAD_REQUEST,
                    "O corpo da requisição contém JSON inválido.");
            return;
        }

        Integer matriculaId = lerId(entrada.get("id"));
        if (matriculaId == null || matriculaId <= 0) {
            responderErro(response, gson, HttpServletResponse.SC_BAD_REQUEST,
                    "Informe um id de matrícula válido.");
            return;
        }

        JsonElement listaAdicionais = entrada.get("adicionais");
        if (listaAdicionais == null || !listaAdicionais.isJsonArray()) {
            responderErro(response, gson, HttpServletResponse.SC_BAD_REQUEST,
                    "Informe adicionais como uma lista de códigos.");
            return;
        }

        List<String> codigos = validarCodigos(listaAdicionais.getAsJsonArray(), response, gson);
        if (codigos == null) {
            return;
        }

        Matricula matricula;
        try {
            matricula = new MatriculaDao().consultarById(
                    Matricula.getBuilder().comId(matriculaId).constroi());
        } catch (Exception ex) {
            if ("Matrícula não encontrada".equals(ex.getMessage())) {
                responderErro(response, gson, HttpServletResponse.SC_NOT_FOUND,
                        "A matrícula informada não foi encontrada.");
            } else {
                responderErro(response, gson, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Não foi possível consultar a matrícula.");
            }
            return;
        }

        Servico composto = matricula;
        List<Map<String, Object>> adicionaisSelecionados = new ArrayList<>();
        ServicoAdicionalDao adicionaisDao = new ServicoAdicionalDao();
        try {
            for (String codigo : codigos) {
                ServicoAdicional adicional = adicionaisDao.consultarPorCodigo(codigo);
                composto = MontadorAdicionais.decorar(composto, adicional);

                adicionaisSelecionados.add(Map.of(
                        "codigo", adicional.getCodigo(),
                        "descricao", adicional.getDescricao(),
                        "valorMensal", adicional.getValorMensal()));
            }
        } catch (Exception ex) {
            responderErro(response, gson, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Não foi possível calcular os serviços adicionais.");
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(gson.toJson(Map.of(
                "matriculaId", matricula.getId(),
                "descricaoBase", matricula.getDescricao(),
                "custoBase", BigDecimal.valueOf(matricula.custo()).setScale(2),
                "adicionais", adicionaisSelecionados,
                "descricao", composto.getDescricao(),
                "totalMensal", BigDecimal.valueOf(composto.custo()).setScale(2))));
    }

    private Integer lerId(JsonElement valor) {
        if (valor == null || !valor.isJsonPrimitive()
                || !(valor.getAsJsonPrimitive().isNumber() || valor.getAsJsonPrimitive().isString())) {
            return null;
        }

        try {
            return Integer.valueOf(valor.getAsString());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private List<String> validarCodigos(JsonArray lista, HttpServletResponse response, Gson gson)
            throws IOException {
        Set<String> codigos = new LinkedHashSet<>();
        for (JsonElement elemento : lista) {
            if (!elemento.isJsonPrimitive() || !elemento.getAsJsonPrimitive().isString()) {
                responderErro(response, gson, HttpServletResponse.SC_BAD_REQUEST,
                        "Cada adicional deve ser informado pelo código.");
                return null;
            }

            String codigo = elemento.getAsString();
            if (!MontadorAdicionais.reconhece(codigo)) {
                responderErro(response, gson, HttpServletResponse.SC_BAD_REQUEST,
                        "Código de serviço adicional não reconhecido: " + codigo);
                return null;
            }
            codigos.add(codigo);
        }
        return new ArrayList<>(codigos);
    }

    private void responderErro(HttpServletResponse response, Gson gson, int status, String mensagem)
            throws IOException {
        response.setStatus(status);
        response.getWriter().write(gson.toJson(Map.of("error", mensagem)));
    }
}
