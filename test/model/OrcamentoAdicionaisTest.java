package model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrcamentoAdicionaisTest {

    private final Matricula matricula = Matricula.getBuilder()
            .comId(7).comDescricao("Mensal").comCustoBase(Dinheiro.de("129.90")).constroi();
    private final ServicoAdicional bioimpedancia =
            new ServicoAdicional("BIOIMPEDANCIA", "Bioimpedância", Dinheiro.de("20.00"));
    private final ServicoAdicional preTreino =
            new ServicoAdicional("PRE_TREINO", "Pré-treino", Dinheiro.de("12.30"));

    @Test
    @DisplayName("Aplica os decorators e monta o orçamento")
    void calcula() {
        OrcamentoAdicionais orcamento = OrcamentoAdicionais.calcular(matricula, List.of(bioimpedancia, preTreino));

        assertEquals(7, orcamento.matriculaId());
        assertEquals("Mensal", orcamento.descricaoBase());
        assertEquals(Dinheiro.de("129.90"), orcamento.custoBase());
        assertEquals(List.of(bioimpedancia, preTreino), orcamento.adicionais());
        assertEquals("Mensal, Bioimpedância, Pré-treino", orcamento.descricao());
        assertEquals(Dinheiro.de("162.20"), orcamento.totalMensal());
    }

    @Test
    @DisplayName("Sem adicionais, o total é o custo base")
    void semAdicionais() {
        OrcamentoAdicionais orcamento = OrcamentoAdicionais.calcular(matricula, List.of());

        assertEquals(Dinheiro.de("129.90"), orcamento.totalMensal());
        assertEquals("Mensal", orcamento.descricao());
    }
}
