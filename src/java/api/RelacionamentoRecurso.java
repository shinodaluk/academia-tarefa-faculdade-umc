package api;

import java.util.function.Function;
import repositorio.Repositorio;

/**
 * GET /api/{recurso}/{id}/{relacionamento}: busca o dono e navega pelo getter da entidade
 * (ex.: Aluno::getMatriculas), que carrega o relacionamento sob demanda.
 */
public class RelacionamentoRecurso<T> implements RecursoApi {

    private final Repositorio<T> donos;
    private final Function<T, ?> navegacao;

    public RelacionamentoRecurso(Repositorio<T> donos, Function<T, ?> navegacao) {
        this.donos = donos;
        this.navegacao = navegacao;
    }

    @Override
    public RespostaApi tratar(RequisicaoApi requisicao) throws Exception {
        if (!"GET".equals(requisicao.metodo())) {
            throw ErroApi.metodoNaoPermitido("GET");
        }
        T dono = donos.buscar(ControladorRest.lerId(requisicao.segmentos().get(0)));
        return RespostaApi.ok(navegacao.apply(dono));
    }
}
