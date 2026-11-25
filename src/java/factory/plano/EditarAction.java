package factory.plano;

import com.google.gson.Gson;
import config.Dependencias;
import factory.ICommand;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Plano;
import repositorio.Repositorio;
import util.TextoSeguro;

public class EditarAction implements ICommand {

    private final Repositorio<Plano> planos;

    public EditarAction(Dependencias dependencias) {
        this.planos = dependencias.planos();
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        if (id == null || id.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/plano?acao=listar");
            return;
        }

        Gson gson = new Gson();
        request.setAttribute("planoJson", gson.toJson(planos.buscar(Integer.parseInt(id))));
        request.setAttribute("aviso", TextoSeguro.paraScript(request.getParameter("erro")));

        request.getRequestDispatcher("plano/form.jsp").forward(request, response);
    }
}
