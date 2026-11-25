package factory.instrutor;

import config.Dependencias;
import factory.ICommand;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Instrutor;
import repositorio.Repositorio;
import util.Mensagens;

public class ExcluirAction implements ICommand {

    private final Repositorio<Instrutor> instrutores;

    public ExcluirAction(Dependencias dependencias) {
        this.instrutores = dependencias.instrutores();
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        if (id == null || id.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/instrutor?acao=listar");
            return;
        }

        String aviso = "Deletado com sucesso";
        try {
            // FK de ficha_treino é ON DELETE SET NULL: fichas ficam sem responsável
            instrutores.excluir(Integer.parseInt(id));
        } catch (Exception ex) {
            aviso = Mensagens.paraUsuario(ex);
        }

        request.setAttribute("redirect", request.getContextPath() + "/instrutor?acao=listar&aviso="
                + URLEncoder.encode(aviso, StandardCharsets.UTF_8));
        request.getRequestDispatcher("redirect.jsp").forward(request, response);
    }
}
