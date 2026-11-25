package api;

/** Atende as requisições de um recurso da API. */
@FunctionalInterface
public interface RecursoApi {

    RespostaApi tratar(RequisicaoApi requisicao) throws Exception;
}
