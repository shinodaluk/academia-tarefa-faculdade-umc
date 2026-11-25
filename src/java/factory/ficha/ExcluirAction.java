package factory.ficha;

import config.Dependencias;
import factory.ICommand;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.FichaTreino;
import repositorio.Repositorio;
import util.Mensagens;

public class ExcluirAction implements ICommand {

    private final Repositorio<FichaTreino> fichas;

    public ExcluirAction(Dependencias dependencias) {
        this.fichas = dependencias.fichas();
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        if (id == null || id.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/ficha?acao=listar");
            return;
        }

        String aviso = "Deletado com sucesso";
        try {
            // exercícios somem junto (FK ON DELETE CASCADE)
            fichas.excluir(Integer.parseInt(id));
        } catch (Exception ex) {
            aviso = Mensagens.paraUsuario(ex);
        }

        request.setAttribute("redirect", request.getContextPath() + "/ficha?acao=listar&aviso="
                + URLEncoder.encode(aviso, StandardCharsets.UTF_8));
        request.getRequestDispatcher("redirect.jsp").forward(request, response);
    }
}
