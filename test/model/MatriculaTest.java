package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MatriculaTest {

    @Test
    @DisplayName("Matrícula expõe descrição e custo do plano carregado")
    void expoeDescricaoECusto() {
        Matricula matricula = Matricula.getBuilder()
                .comDescricao("Mensal")
                .comCustoBase(129.90)
                .constroi();

        assertEquals("Mensal", matricula.getDescricao());
        assertEquals(129.90, matricula.custo(), 0.0001);
    }

    @Test
    @DisplayName("Custo sem plano carregado lança IllegalStateException")
    void custoSemPlanoCarregado() {
        Matricula matricula = Matricula.getBuilder().comId(1).constroi();

        assertThrows(IllegalStateException.class, matricula::custo);
    }

    @Test
    @DisplayName("Descrição sem plano carregado lança IllegalStateException")
    void descricaoSemPlanoCarregado() {
        Matricula matricula = Matricula.getBuilder().comId(1).constroi();

        assertThrows(IllegalStateException.class, matricula::getDescricao);
    }

    @Test
    @DisplayName("Matrícula funciona como componente concreto do decorator")
    void matriculaComoComponenteDoDecorator() {
        Matricula matricula = Matricula.getBuilder()
                .comDescricao("Mensal")
                .comCustoBase(129.90)
                .constroi();
        ServicoAdicional preTreino = new ServicoAdicional("PRE_TREINO", "Pré-treino", new BigDecimal("12.30"));

        Servico servico = new PreTreino(matricula, preTreino);

        assertEquals(142.20, servico.custo(), 0.0001);
        assertEquals("Mensal, Pré-treino", servico.getDescricao());
    }
}
