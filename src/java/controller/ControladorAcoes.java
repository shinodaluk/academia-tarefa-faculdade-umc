package controller;

import config.Dependencias;
import factory.AcaoNaoEncontradaException;
import factory.FabricaAcoes;
import factory.ICommand;
import java.io.IOException;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Base dos controllers das telas: escolhe a ação pelo parâmetro "acao" e a executa. */
public abstract class ControladorAcoes extends HttpServlet {

    // ações que gravam só aceitam POST: um link ou prefetch não pode excluir nada
    private static final Set<String> SO_POST = Set.of("salvar", "excluir");

    private final String recurso;
    private transient Dependencias dependencias;

    protected ControladorAcoes(String recurso) {
        this.recurso = recurso;
    }

    @Override
    public void init() {
        dependencias = Dependencias.jdbc();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String acao = request.getParameter("acao");
        if (exigePost(acao)) {
            response.setHeader("Allow", "POST");
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        processar(acao, request, response);
    }

    static boolean exigePost(String acao) {
        // Set.of lança NullPointerException em contains(null)
        return acao != null && SO_POST.contains(acao);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processar(request.getParameter("acao"), request, response);
    }

    private void processar(String acao, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (acao == null) {
            response.sendRedirect(request.getContextPath() + "/" + recurso + "?acao=listar");
            return;
        }
        ICommand comando;
        try {
            comando = FabricaAcoes.criar(recurso, acao, dependencias);
        } catch (AcaoNaoEncontradaException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        try {
            comando.executar(request, response);
        } catch (NumberFormatException ex) {
            // id ou número da URL inválido: erro do cliente, não do servidor
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (ServletException | IOException | RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            // vai para erro.jsp pelo web.xml, sem stack trace na tela
            throw new ServletException(ex);
        }
    }
}
