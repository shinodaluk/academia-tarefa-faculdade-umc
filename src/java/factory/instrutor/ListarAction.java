package factory.instrutor;

import com.google.gson.Gson;
import config.Dependencias;
import factory.ICommand;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Instrutor;
import repositorio.Filtros;
import repositorio.Repositorio;
import util.Mensagens;
import util.TextoSeguro;

public class ListarAction implements ICommand {

    private final Repositorio<Instrutor> instrutores;

    public ListarAction(Dependencias dependencias) {
        this.instrutores = dependencias.instrutores();
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute("aviso", TextoSeguro.paraScript(request.getParameter("aviso")));
        try {
            request.setAttribute("instrutoresJson", new Gson().toJson(instrutores.listar(Filtros.de("especialidade", request.getParameter("especialidade"), "status", request.getParameter("status")))));
        } catch (Exception ex) {
            request.setAttribute("instrutoresJson", "[]");
            request.setAttribute("aviso", TextoSeguro.paraScript(Mensagens.paraUsuario(ex)));
        }

        request.getRequestDispatcher("instrutor/listar.jsp").forward(request, response);
    }
}
