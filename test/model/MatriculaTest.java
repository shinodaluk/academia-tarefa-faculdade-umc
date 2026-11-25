package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MatriculaTest {

    @Test
    @DisplayName("Matrícula expõe descrição e custo do plano carregado")
    void expoeDescricaoECusto() {
        Matricula matricula = Matricula.getBuilder()
                .comDescricao("Mensal")
                .comCustoBase(Dinheiro.de("129.90"))
                .constroi();

        assertEquals("Mensal", matricula.getDescricao());
        assertEquals(Dinheiro.de("129.90"), matricula.custo());
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
                .comCustoBase(Dinheiro.de("129.90"))
                .constroi();
        ServicoAdicional preTreino = new ServicoAdicional("PRE_TREINO", "Pré-treino", Dinheiro.de("12.30"));

        Servico servico = new PreTreino(matricula, preTreino);

        assertEquals(Dinheiro.de("142.20"), servico.custo());
        assertEquals("Mensal, Pré-treino", servico.getDescricao());
    }
}
