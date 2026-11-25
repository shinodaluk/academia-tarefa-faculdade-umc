package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AdicionaisMatriculaTest {

    private static final ServicoAdicional BIOIMPEDANCIA =
            new ServicoAdicional("BIOIMPEDANCIA", "Bioimpedância", Dinheiro.de("20.00"));
    private static final ServicoAdicional CADEIRA_MASSAGEM =
            new ServicoAdicional("CADEIRA_MASSAGEM", "Cadeira de Massagem", Dinheiro.de("30.00"));
    private static final ServicoAdicional PRE_TREINO =
            new ServicoAdicional("PRE_TREINO", "Pré-treino", Dinheiro.de("12.30"));

    private final Servico planoMensal = new ServicoFixo("Mensal", Dinheiro.de("129.90"));

    @Test
    @DisplayName("Bioimpedância soma seu valor ao custo do serviço base")
    void bioimpedanciaSomaValorAoCusto() {
        Servico servico = new Bioimpedancia(planoMensal, BIOIMPEDANCIA);

        assertEquals(Dinheiro.de("149.90"), servico.custo());
    }

    @Test
    @DisplayName("Cadeira de massagem soma seu valor ao custo do serviço base")
    void cadeiraMassagemSomaValorAoCusto() {
        Servico servico = new CadeiraMassagem(planoMensal, CADEIRA_MASSAGEM);

        assertEquals(Dinheiro.de("159.90"), servico.custo());
    }

    @Test
    @DisplayName("Pré-treino soma seu valor ao custo do serviço base")
    void preTreinoSomaValorAoCusto() {
        Servico servico = new PreTreino(planoMensal, PRE_TREINO);

        assertEquals(Dinheiro.de("142.20"), servico.custo());
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

        assertEquals(Dinheiro.de("192.20"), servico.custo());
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

        assertEquals(Dinheiro.de("129.90"), planoMensal.custo());
        assertEquals("Mensal", planoMensal.getDescricao());
    }

    @Test
    @DisplayName("Soma é exata, sem erro de ponto flutuante")
    void somaSemErroDePontoFlutuante() {
        Servico base = new ServicoFixo("Base", Dinheiro.de("0.1"));
        ServicoAdicional adicional = new ServicoAdicional("PRE_TREINO", "Pré-treino", Dinheiro.de("0.2"));

        assertEquals(Dinheiro.de("0.30"), new PreTreino(base, adicional).custo());
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
        private final Dinheiro custo;

        ServicoFixo(String descricao, Dinheiro custo) {
            this.descricao = descricao;
            this.custo = custo;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }

        @Override
        public Dinheiro custo() {
            return custo;
        }
    }
}
