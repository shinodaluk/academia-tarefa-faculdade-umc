package factory.plano;

import com.google.gson.Gson;
import config.Dependencias;
import factory.ICommand;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Plano;
import repositorio.Filtros;
import repositorio.Repositorio;
import util.Mensagens;
import util.TextoSeguro;

public class ListarAction implements ICommand {

    private final Repositorio<Plano> planos;

    public ListarAction(Dependencias dependencias) {
        this.planos = dependencias.planos();
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute("aviso", TextoSeguro.paraScript(request.getParameter("aviso")));
        try {
            request.setAttribute("planosJson", new Gson().toJson(planos.listar(Filtros.de("apenasAtivos", String.valueOf("1".equals(request.getParameter("somenteAtivos")))))));
        } catch (Exception ex) {
            request.setAttribute("planosJson", "[]");
            request.setAttribute("aviso", TextoSeguro.paraScript(Mensagens.paraUsuario(ex)));
        }

        request.getRequestDispatcher("plano/listar.jsp").forward(request, response);
    }
}
