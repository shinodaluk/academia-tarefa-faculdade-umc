package api;

import com.google.gson.Gson;
import model.Aluno;
import model.AvaliacaoFisica;
import repositorio.AvaliacoesFisicas;
import repositorio.Repositorio;

/** /api/alunos/{id}/avaliacao: recurso único do aluno (1:1), sem coleção nem POST. */
public class AvaliacaoFisicaRecurso implements RecursoApi {

    private final Repositorio<Aluno> alunos;
    private final AvaliacoesFisicas avaliacoes;
    private final Validador<AvaliacaoFisica> validador;
    private final Gson gson;

    public AvaliacaoFisicaRecurso(Repositorio<Aluno> alunos, AvaliacoesFisicas avaliacoes,
            Validador<AvaliacaoFisica> validador, Gson gson) {
        this.alunos = alunos;
        this.avaliacoes = avaliacoes;
        this.validador = validador;
        this.gson = gson;
    }

    @Override
    public RespostaApi tratar(RequisicaoApi requisicao) throws Exception {
        int alunoId = ControladorRest.lerId(requisicao.segmentos().get(0));
        alunos.buscar(alunoId); // aluno inexistente: 404 antes de qualquer gravação

        switch (requisicao.metodo()) {
            case "GET":
                return RespostaApi.ok(existente(alunoId));
            case "PUT":
                return salvar(requisicao, alunoId);
            case "DELETE":
                existente(alunoId);
                avaliacoes.excluirDoAluno(alunoId);
                return RespostaApi.semConteudo();
            default:
                throw ErroApi.metodoNaoPermitido("GET, PUT, DELETE");
        }
    }

    // PUT é idempotente: cria na primeira vez (201) e substitui depois (200)
    private RespostaApi salvar(RequisicaoApi requisicao, int alunoId) throws Exception {
        AvaliacaoFisica avaliacao = CorpoJson.ler(gson, requisicao.corpo(), AvaliacaoFisica.class, json -> {
            json.remove("id");
            json.remove("imc");
            json.addProperty("alunoId", alunoId);
        }, validador);

        boolean criada = avaliacoes.salvar(avaliacao);
        AvaliacaoFisica salva = existente(alunoId);
        return criada
                ? RespostaApi.criado(requisicao.urlRecurso() + "/" + alunoId + "/avaliacao", salva)
                : RespostaApi.ok(salva);
    }

    private AvaliacaoFisica existente(int alunoId) throws Exception {
        return avaliacoes.doAluno(alunoId)
                .orElseThrow(() -> ErroApi.naoEncontrado("Aluno sem avaliação física."));
    }
}
