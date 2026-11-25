package api;

import config.Dependencias;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        // resposta é application/json, não HTML: o escape de ' < > do Gson só atrapalharia a leitura
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").disableHtmlEscaping().create();
        Dependencias dependencias = Dependencias.jdbc();

        roteador = new RoteadorApi(Map.of(
                "alunos", new ControladorRest<>(Aluno.class, dependencias.alunos(), Validacoes.aluno(), gson)
                        .comSubrecurso("matriculas", new RelacionamentoRecurso<>(dependencias.alunos(), Aluno::getMatriculas))
                        .comSubrecurso("fichas", new RelacionamentoRecurso<>(dependencias.alunos(), Aluno::getFichas))
                        .comSubrecurso("avaliacao", new AvaliacaoFisicaRecurso(dependencias.alunos(),
                                dependencias.avaliacoes(), Validacoes.avaliacaoFisica(), gson)),
                "planos", new ControladorRest<>(Plano.class, dependencias.planos(), Validacoes.plano(), gson)
                        .comSubrecurso("matriculas", new RelacionamentoRecurso<>(dependencias.planos(), Plano::getMatriculas)),
                "instrutores", new ControladorRest<>(Instrutor.class,
                        dependencias.instrutores(), Validacoes.instrutor(), gson)
                        .comSubrecurso("fichas", new RelacionamentoRecurso<>(
                                dependencias.instrutores(), Instrutor::getFichas)),
                "matriculas", new ControladorRest<>(Matricula.class,
                        dependencias.matriculas(), Validacoes.matricula(), gson)
                        .comSubrecurso("adicionais", new AdicionaisMatriculaRecurso(
                                dependencias.matriculas()::buscar, dependencias.adicionais()::porCodigo)),
                "fichas", new ControladorRest<>(FichaTreino.class, dependencias.fichas(), Validacoes.ficha(), gson)
                        .comNormalizador(new NormalizacaoFicha())
                        .comSubrecurso("exercicios", new RelacionamentoRecurso<>(
                                dependencias.fichas(), FichaTreino::getExercicios))));
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
