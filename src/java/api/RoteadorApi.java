package api;

import dao.RegistroNaoEncontradoException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Encaminha cada requisição ao recurso e traduz exceções em status HTTP. */
public class RoteadorApi {

    private static final Logger LOG = Logger.getLogger(RoteadorApi.class.getName());

    private final Map<String, RecursoApi> recursos;

    public RoteadorApi(Map<String, RecursoApi> recursos) {
        this.recursos = Map.copyOf(recursos);
    }

    public RespostaApi atender(RequisicaoApi requisicao) {
        RecursoApi recurso = recursos.get(requisicao.recurso());
        if (recurso == null) {
            return RespostaApi.erro(404, "Recurso não encontrado.");
        }
        try {
            return recurso.tratar(requisicao);
        } catch (ErroApi ex) {
            return ex.comoResposta();
        } catch (RegistroNaoEncontradoException ex) {
            return RespostaApi.erro(404, ex.getMessage());
        } catch (SQLIntegrityConstraintViolationException ex) {
            return RespostaApi.erro(409,
                    "A operação viola a integridade dos dados (registro duplicado, inexistente ou com vínculos).");
        } catch (Exception ex) {
            // detalhe só no log: a resposta não expõe SQL nem stack trace
            LOG.log(Level.SEVERE, "Erro ao atender " + requisicao.metodo() + " /" + requisicao.recurso(), ex);
            return RespostaApi.erro(500, "Erro interno ao processar a requisição.");
        }
    }
}
