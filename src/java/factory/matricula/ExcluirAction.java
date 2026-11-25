package factory.matricula;

import config.Dependencias;
import factory.ICommand;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Matricula;
import repositorio.Repositorio;
import util.Mensagens;

public class ExcluirAction implements ICommand {

    private final Repositorio<Matricula> matriculas;

    public ExcluirAction(Dependencias dependencias) {
        this.matriculas = dependencias.matriculas();
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        if (id == null || id.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/matricula?acao=listar");
            return;
        }

        String aviso = "Deletado com sucesso";
        try {
            // mensalidades somem junto (FK ON DELETE CASCADE)
            matriculas.excluir(Integer.parseInt(id));
        } catch (Exception ex) {
            aviso = Mensagens.paraUsuario(ex);
        }

        request.setAttribute("redirect", request.getContextPath() + "/matricula?acao=listar&aviso="
                + URLEncoder.encode(aviso, StandardCharsets.UTF_8));
        request.getRequestDispatcher("redirect.jsp").forward(request, response);
    }
}
