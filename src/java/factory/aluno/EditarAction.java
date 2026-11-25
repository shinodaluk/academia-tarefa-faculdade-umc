package factory.aluno;

import com.google.gson.Gson;
import dao.AlunoDao;
import factory.ICommand;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Aluno;
import util.TextoSeguro;

public class EditarAction implements ICommand {

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        if (id == null || id.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/aluno?acao=listar");
            return;
        }

        Aluno aluno = new AlunoDao().consultarById(Aluno.getBuilder().comId(Integer.parseInt(id)).constroi());
        request.setAttribute("alunoJson", new Gson().toJson(aluno));
        request.setAttribute("aviso", TextoSeguro.paraScript(request.getParameter("erro")));

        request.getRequestDispatcher("aluno/form.jsp").forward(request, response);
    }
}
