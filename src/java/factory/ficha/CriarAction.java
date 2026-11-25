package factory.ficha;

import com.google.gson.Gson;
import config.Dependencias;
import factory.ICommand;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Aluno;
import model.FichaTreino;
import model.Instrutor;
import repositorio.Filtros;
import repositorio.Repositorio;
import util.TextoSeguro;

public class CriarAction implements ICommand {

    private final Repositorio<Aluno> alunos;
    private final Repositorio<Instrutor> instrutores;

    public CriarAction(Dependencias dependencias) {
        this.alunos = dependencias.alunos();
        this.instrutores = dependencias.instrutores();
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        FichaTreino novo = FichaTreino.getBuilder()
                .doObjetivoHipertrofia()
                .constroi();
        Gson gson = new Gson();

        request.setAttribute("fichaJson", gson.toJson(novo));
        request.setAttribute("alunosJson", gson.toJson(alunos.listar(Filtros.de())));
        request.setAttribute("instrutoresJson", gson.toJson(instrutores.listar(Filtros.de())));
        request.setAttribute("aviso", TextoSeguro.paraScript(request.getParameter("erro")));

        request.getRequestDispatcher("ficha/form.jsp").forward(request, response);
    }
}
