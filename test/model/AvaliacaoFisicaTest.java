package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AvaliacaoFisicaTest {

    @Test
    @DisplayName("IMC = peso / altura², com uma casa decimal")
    void calculaImc() {
        AvaliacaoFisica avaliacao = AvaliacaoFisica.getBuilder()
                .comPesoKg(new BigDecimal("78.40"))
                .comAlturaM(new BigDecimal("1.76"))
                .constroi();

        assertEquals(new BigDecimal("25.3"), avaliacao.getImc());
    }

    @Test
    @DisplayName("Sem peso ou altura não há IMC")
    void semImc() {
        AvaliacaoFisica semAltura = AvaliacaoFisica.getBuilder().comPesoKg(new BigDecimal("70")).constroi();

        assertNull(semAltura.getImc());
    }

    @Test
    @DisplayName("Builder guarda os campos da avaliação")
    void builder() {
        AvaliacaoFisica avaliacao = AvaliacaoFisica.getBuilder()
                .comId(3)
                .comAlunoId(7)
                .comDataAvaliacao("2026-02-10")
                .comPercentualGordura(new BigDecimal("18.5"))
                .comPressaoArterial("120/80")
                .comFrequenciaCardiaca(64)
                .constroi();

        assertEquals(3, avaliacao.getId());
        assertEquals(7, avaliacao.getAlunoId());
        assertEquals(Datas.deIso("2026-02-10"), avaliacao.getDataAvaliacao());
        assertEquals(new BigDecimal("18.5"), avaliacao.getPercentualGordura());
        assertEquals("120/80", avaliacao.getPressaoArterial());
        assertEquals(64, avaliacao.getFrequenciaCardiaca());
    }
}
