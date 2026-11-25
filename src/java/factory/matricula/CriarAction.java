package factory.matricula;

import com.google.gson.Gson;
import config.Dependencias;
import factory.ICommand;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Aluno;
import model.Matricula;
import model.Plano;
import repositorio.Filtros;
import repositorio.Repositorio;
import util.TextoSeguro;

public class CriarAction implements ICommand {

    private final Repositorio<Aluno> alunos;
    private final Repositorio<Plano> planos;

    public CriarAction(Dependencias dependencias) {
        this.alunos = dependencias.alunos();
        this.planos = dependencias.planos();
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Matricula novo = Matricula.getBuilder()
                .comFidelidadeMeses(0)
                .comDiaVencimento(5)
                .doSituacaoAtiva()
                .constroi();
        Gson gson = new Gson();

        request.setAttribute("matriculaJson", gson.toJson(novo));
        request.setAttribute("alunosJson", gson.toJson(alunos.listar(Filtros.de())));
        request.setAttribute("planosJson", gson.toJson(planos.listar(Filtros.de())));
        request.setAttribute("aviso", TextoSeguro.paraScript(request.getParameter("erro")));

        request.getRequestDispatcher("matricula/form.jsp").forward(request, response);
    }
}
