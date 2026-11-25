package util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MensagensTest {

    @Test
    @DisplayName("Violação de integridade vira mensagem sem SQL")
    void integridade() {
        String mensagem = Mensagens.paraUsuario(
                new SQLIntegrityConstraintViolationException("Duplicate entry '1' for key 'aluno.cpf'"));

        assertFalse(mensagem.contains("Duplicate"));
        assertEquals(Mensagens.CONFLITO, mensagem);
    }

    @Test
    @DisplayName("Regra de negócio (IllegalArgumentException) mostra a própria mensagem")
    void regraDeNegocio() {
        assertEquals("CPF deve ter 11 dígitos", Mensagens.paraUsuario(new IllegalArgumentException("CPF deve ter 11 dígitos")));
    }

    @Test
    @DisplayName("Número mal digitado tem mensagem própria")
    void numeroInvalido() {
        assertEquals(Mensagens.NUMERO_INVALIDO, Mensagens.paraUsuario(new NumberFormatException("For input string: \"x\"")));
    }

    @Test
    @DisplayName("Erro inesperado não expõe detalhes")
    void inesperado() {
        assertEquals(Mensagens.ERRO_GENERICO, Mensagens.paraUsuario(new SQLException("Communications link failure")));
    }
}
