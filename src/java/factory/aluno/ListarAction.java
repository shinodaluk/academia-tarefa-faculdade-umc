package factory.aluno;

import com.google.gson.Gson;
import config.Dependencias;
import factory.ICommand;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Aluno;
import repositorio.Filtros;
import repositorio.IndicadoresAlunos;
import repositorio.Repositorio;
import util.Mensagens;
import util.TextoSeguro;

public class ListarAction implements ICommand {

    private final Repositorio<Aluno> alunos;
    private final IndicadoresAlunos indicadores;

    public ListarAction(Dependencias dependencias) {
        this.alunos = dependencias.alunos();
        this.indicadores = dependencias.indicadoresAlunos();
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Gson gson = new Gson();
        request.setAttribute("aviso", TextoSeguro.paraScript(request.getParameter("aviso")));

        try {
            request.setAttribute("alunosJson", gson.toJson(alunos.listar(
                    Filtros.de("busca", request.getParameter("busca"), "status", request.getParameter("status")))));
            request.setAttribute("indicadoresJson", gson.toJson(indicadores.calcular()));
        } catch (Exception ex) {
            request.setAttribute("alunosJson", "[]");
            request.setAttribute("indicadoresJson", "{}");
            request.setAttribute("aviso", TextoSeguro.paraScript(Mensagens.paraUsuario(ex)));
        }

        request.getRequestDispatcher("aluno/listar.jsp").forward(request, response);
    }
}
