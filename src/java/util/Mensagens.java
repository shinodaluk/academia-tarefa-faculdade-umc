package util;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Converte exceções em mensagens para a tela, sem expor SQL ou detalhes internos. */
public final class Mensagens {

    public static final String CONFLITO =
            "Já existe um cadastro com esses dados, ou o registro tem vínculos que impedem a operação.";
    public static final String NUMERO_INVALIDO = "Um dos campos numéricos está inválido.";
    public static final String ERRO_GENERICO = "Não foi possível concluir a operação. Tente novamente.";

    private static final Logger LOG = Logger.getLogger(Mensagens.class.getName());

    private Mensagens() {
    }

    public static String paraUsuario(Exception ex) {
        if (ex instanceof SQLIntegrityConstraintViolationException) {
            return CONFLITO;
        }
        if (ex instanceof NumberFormatException) {
            return NUMERO_INVALIDO;
        }
        // IllegalArgumentException = regra de negócio nossa (Cpf, Dinheiro, Datas): mensagem já é legível
        if (ex instanceof IllegalArgumentException) {
            return ex.getMessage();
        }
        LOG.log(Level.SEVERE, "Erro inesperado", ex);
        return ERRO_GENERICO;
    }
}
