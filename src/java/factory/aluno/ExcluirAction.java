package factory.aluno;

import dao.AlunoDao;
import factory.ICommand;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Aluno;
import util.Mensagens;

public class ExcluirAction implements ICommand {

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        if (id == null || id.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/aluno?acao=listar");
            return;
        }

        String aviso = "Deletado com sucesso";
        try {
            // matrículas e fichas do aluno somem junto (FK ON DELETE CASCADE)
            new AlunoDao().deletar(Aluno.getBuilder().comId(Integer.parseInt(id)).constroi());
        } catch (Exception ex) {
            aviso = Mensagens.paraUsuario(ex);
        }

        request.setAttribute("redirect", request.getContextPath() + "/aluno?acao=listar&aviso="
                + URLEncoder.encode(aviso, StandardCharsets.UTF_8));
        request.getRequestDispatcher("redirect.jsp").forward(request, response);
    }
}
