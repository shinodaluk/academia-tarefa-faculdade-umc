package factory.instrutor;

import com.google.gson.Gson;
import config.Dependencias;
import factory.ICommand;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Instrutor;
import repositorio.Repositorio;
import util.TextoSeguro;

public class EditarAction implements ICommand {

    private final Repositorio<Instrutor> instrutores;

    public EditarAction(Dependencias dependencias) {
        this.instrutores = dependencias.instrutores();
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        if (id == null || id.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/instrutor?acao=listar");
            return;
        }

        Gson gson = new Gson();
        request.setAttribute("instrutorJson", gson.toJson(instrutores.buscar(Integer.parseInt(id))));
        request.setAttribute("aviso", TextoSeguro.paraScript(request.getParameter("erro")));

        request.getRequestDispatcher("instrutor/form.jsp").forward(request, response);
    }
}
