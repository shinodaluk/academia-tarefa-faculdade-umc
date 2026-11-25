package controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ControladorAcoesTest {

    @Test
    @DisplayName("Ações que gravam exigem POST")
    void acoesQueGravamExigemPost() {
        assertTrue(ControladorAcoes.exigePost("salvar"));
        assertTrue(ControladorAcoes.exigePost("excluir"));
    }

    @Test
    @DisplayName("Ações de leitura aceitam GET")
    void acoesDeLeituraAceitamGet() {
        assertFalse(ControladorAcoes.exigePost("listar"));
        assertFalse(ControladorAcoes.exigePost("editar"));
    }

    @Test
    @DisplayName("Sem ação não exige POST (redireciona para a listagem)")
    void semAcao() {
        assertFalse(ControladorAcoes.exigePost(null));
    }
}
