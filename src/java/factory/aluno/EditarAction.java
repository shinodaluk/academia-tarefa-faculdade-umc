package factory.aluno;

import com.google.gson.Gson;
import config.Dependencias;
import factory.ICommand;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Aluno;
import repositorio.Repositorio;
import util.TextoSeguro;

public class EditarAction implements ICommand {

    private final Repositorio<Aluno> alunos;

    public EditarAction(Dependencias dependencias) {
        this.alunos = dependencias.alunos();
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        if (id == null || id.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/aluno?acao=listar");
            return;
        }

        Gson gson = new Gson();
        request.setAttribute("alunoJson", gson.toJson(alunos.buscar(Integer.parseInt(id))));
        request.setAttribute("aviso", TextoSeguro.paraScript(request.getParameter("erro")));

        request.getRequestDispatcher("aluno/form.jsp").forward(request, response);
    }
}
