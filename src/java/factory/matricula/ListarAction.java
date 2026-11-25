package factory.matricula;

import com.google.gson.Gson;
import config.Dependencias;
import factory.ICommand;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Matricula;
import repositorio.Filtros;
import repositorio.Repositorio;
import util.Mensagens;
import util.TextoSeguro;

public class ListarAction implements ICommand {

    private final Repositorio<Matricula> matriculas;

    public ListarAction(Dependencias dependencias) {
        this.matriculas = dependencias.matriculas();
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute("aviso", TextoSeguro.paraScript(request.getParameter("aviso")));
        try {
            request.setAttribute("matriculasJson", new Gson().toJson(matriculas.listar(Filtros.de("situacao", request.getParameter("situacao")))));
        } catch (Exception ex) {
            request.setAttribute("matriculasJson", "[]");
            request.setAttribute("aviso", TextoSeguro.paraScript(Mensagens.paraUsuario(ex)));
        }

        request.getRequestDispatcher("matricula/listar.jsp").forward(request, response);
    }
}
