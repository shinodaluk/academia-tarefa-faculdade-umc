package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AdicionaisMatriculaTest {

    private static final ServicoAdicional BIOIMPEDANCIA =
            new ServicoAdicional("BIOIMPEDANCIA", "Bioimpedância", new BigDecimal("20.00"));
    private static final ServicoAdicional CADEIRA_MASSAGEM =
            new ServicoAdicional("CADEIRA_MASSAGEM", "Cadeira de Massagem", new BigDecimal("30.00"));
    private static final ServicoAdicional PRE_TREINO =
            new ServicoAdicional("PRE_TREINO", "Pré-treino", new BigDecimal("12.30"));

    private static final double DELTA = 0.0001;

    private final Servico planoMensal = new ServicoFixo("Mensal", 129.90);

    @Test
    @DisplayName("Bioimpedância soma seu valor ao custo do serviço base")
    void bioimpedanciaSomaValorAoCusto() {
        Servico servico = new Bioimpedancia(planoMensal, BIOIMPEDANCIA);

        assertEquals(149.90, servico.custo(), DELTA);
    }

    @Test
    @DisplayName("Cadeira de massagem soma seu valor ao custo do serviço base")
    void cadeiraMassagemSomaValorAoCusto() {
        Servico servico = new CadeiraMassagem(planoMensal, CADEIRA_MASSAGEM);

        assertEquals(159.90, servico.custo(), DELTA);
    }

    @Test
    @DisplayName("Pré-treino soma seu valor ao custo do serviço base")
    void preTreinoSomaValorAoCusto() {
        Servico servico = new PreTreino(planoMensal, PRE_TREINO);

        assertEquals(142.20, servico.custo(), DELTA);
    }

    @Test
    @DisplayName("Descrição do decorator acrescenta o adicional após a do serviço base")
    void descricaoAcrescentaAdicional() {
        Servico servico = new Bioimpedancia(planoMensal, BIOIMPEDANCIA);

        assertEquals("Mensal, Bioimpedância", servico.getDescricao());
    }

    @Test
    @DisplayName("Decorators encadeados somam todos os adicionais")
    void decoratorsEncadeadosSomamTodos() {
        Servico servico = new PreTreino(
                new CadeiraMassagem(
                        new Bioimpedancia(planoMensal, BIOIMPEDANCIA),
                        CADEIRA_MASSAGEM),
                PRE_TREINO);

        assertEquals(192.20, servico.custo(), DELTA);
    }

    @Test
    @DisplayName("Descrição encadeada segue a ordem em que os decorators foram aplicados")
    void descricaoEncadeadaSegueOrdem() {
        Servico servico = new Bioimpedancia(new PreTreino(planoMensal, PRE_TREINO), BIOIMPEDANCIA);

        assertEquals("Mensal, Pré-treino, Bioimpedância", servico.getDescricao());
    }

    @Test
    @DisplayName("Decorar não altera o serviço base")
    void naoAlteraServicoBase() {
        new Bioimpedancia(planoMensal, BIOIMPEDANCIA);

        assertEquals(129.90, planoMensal.custo(), DELTA);
        assertEquals("Mensal", planoMensal.getDescricao());
    }

    @Test
    @DisplayName("Soma é feita em BigDecimal, sem erro de ponto flutuante")
    void somaSemErroDePontoFlutuante() {
        Servico base = new ServicoFixo("Base", 0.1);
        ServicoAdicional adicional = new ServicoAdicional("PRE_TREINO", "Pré-treino", new BigDecimal("0.2"));

        assertEquals(0.3, new PreTreino(base, adicional).custo());
    }

    @Test
    @DisplayName("Serviço base nulo é rejeitado")
    void rejeitaServicoBaseNulo() {
        assertThrows(NullPointerException.class, () -> new Bioimpedancia(null, BIOIMPEDANCIA));
    }

    @Test
    @DisplayName("Adicional nulo é rejeitado")
    void rejeitaAdicionalNulo() {
        assertThrows(NullPointerException.class, () -> new Bioimpedancia(planoMensal, null));
    }

    @Test
    @DisplayName("Adicional de outro código é rejeitado pelo decorator")
    void rejeitaAdicionalDeOutroCodigo() {
        assertThrows(IllegalArgumentException.class, () -> new Bioimpedancia(planoMensal, PRE_TREINO));
    }

    private static final class ServicoFixo implements Servico {
        private final String descricao;
        private final double custo;

        ServicoFixo(String descricao, double custo) {
            this.descricao = descricao;
            this.custo = custo;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }

        @Override
        public double custo() {
            return custo;
        }
    }
}
