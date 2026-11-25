package api;

import api.repositorio.AlunoRepositorioJdbc;
import api.repositorio.FichaTreinoRepositorioJdbc;
import api.repositorio.InstrutorRepositorioJdbc;
import api.repositorio.MatriculaRepositorioJdbc;
import api.repositorio.PlanoRepositorioJdbc;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.AlunoDao;
import dao.FichaTreinoDao;
import dao.InstrutorDao;
import dao.MatriculaDao;
import dao.PlanoDao;
import dao.ServicoAdicionalDao;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Aluno;
import model.FichaTreino;
import model.Instrutor;
import model.Matricula;
import model.Plano;

/** Ponto de entrada da API REST em /api/*: só traduz HTTP de e para o RoteadorApi. */
@WebServlet(name = "ApiServlet", urlPatterns = {"/api/*"})
public class ApiServlet extends HttpServlet {

    private transient Gson gson;
    private transient RoteadorApi roteador;

    @Override
    public void init() {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();

        Repositorio<Matricula> matriculas = new MatriculaRepositorioJdbc(new MatriculaDao());
        ServicoAdicionalDao adicionais = new ServicoAdicionalDao();

        roteador = new RoteadorApi(Map.of(
                "alunos", new ControladorRest<>(Aluno.class,
                        new AlunoRepositorioJdbc(new AlunoDao()), Validacoes.aluno(), gson),
                "planos", new ControladorRest<>(Plano.class,
                        new PlanoRepositorioJdbc(new PlanoDao()), Validacoes.plano(), gson),
                "instrutores", new ControladorRest<>(Instrutor.class,
                        new InstrutorRepositorioJdbc(new InstrutorDao()), Validacoes.instrutor(), gson),
                "matriculas", new ControladorRest<>(Matricula.class, matriculas, Validacoes.matricula(), gson)
                        .comSubrecurso("adicionais",
                                new AdicionaisMatriculaRecurso(matriculas::buscar, adicionais::consultarPorCodigo)),
                "fichas", new ControladorRest<>(FichaTreino.class,
                        new FichaTreinoRepositorioJdbc(new FichaTreinoDao()), Validacoes.ficha(), gson)
                        .comNormalizador(new NormalizacaoFicha())));
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String corpo;
        try {
            // corpo lido antes dos parâmetros para não ser consumido como formulário
            corpo = request.getReader().lines().collect(Collectors.joining("\n"));
        } catch (UncheckedIOException ex) {
            escrever(response, RespostaApi.erro(400, "O corpo da requisição deve estar em UTF-8."));
            return;
        }
        Map<String, String> parametros = new HashMap<>();
        request.getParameterMap().forEach((nome, valores) -> parametros.put(nome, valores[0]));

        escrever(response, roteador.atender(RequisicaoApi.de(
                request.getMethod(),
                request.getPathInfo(),
                parametros,
                corpo,
                request.getContextPath() + request.getServletPath())));
    }

    private void escrever(HttpServletResponse response, RespostaApi resposta) throws IOException {
        response.setStatus(resposta.status());
        resposta.cabecalhos().forEach(response::setHeader);
        if (resposta.corpo() != null) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(resposta.corpo()));
        }
    }
}
