package factory.ficha;

import com.google.gson.Gson;
import config.Dependencias;
import factory.ICommand;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.FichaTreino;
import repositorio.Filtros;
import repositorio.Repositorio;
import util.Mensagens;
import util.TextoSeguro;

public class ListarAction implements ICommand {

    private final Repositorio<FichaTreino> fichas;

    public ListarAction(Dependencias dependencias) {
        this.fichas = dependencias.fichas();
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute("aviso", TextoSeguro.paraScript(request.getParameter("aviso")));
        try {
            request.setAttribute("fichasJson", new Gson().toJson(fichas.listar(Filtros.de("objetivo", request.getParameter("objetivo")))));
        } catch (Exception ex) {
            request.setAttribute("fichasJson", "[]");
            request.setAttribute("aviso", TextoSeguro.paraScript(Mensagens.paraUsuario(ex)));
        }

        request.getRequestDispatcher("ficha/listar.jsp").forward(request, response);
    }
}
