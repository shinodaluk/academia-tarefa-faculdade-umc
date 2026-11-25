package factory;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FabricaAcoesTest {

    @Test
    @DisplayName("Cria a ação pelo recurso e pelo nome da ação")
    void criaAcao() throws Exception {
        assertInstanceOf(factory.plano.ListarAction.class, FabricaAcoes.criar("plano", "listar"));
        assertInstanceOf(factory.matricula.CalcularAdicionaisAction.class,
                FabricaAcoes.criar("matricula", "calcularAdicionais"));
    }

    @Test
    @DisplayName("Ação inexistente é rejeitada")
    void acaoInexistente() {
        assertThrows(AcaoNaoEncontradaException.class, () -> FabricaAcoes.criar("plano", "voar"));
    }

    @Test
    @DisplayName("Nome de ação com caracteres fora de letras é rejeitado")
    void acaoComCaracteresInvalidos() {
        assertThrows(AcaoNaoEncontradaException.class, () -> FabricaAcoes.criar("plano", "listar.x"));
        assertThrows(AcaoNaoEncontradaException.class, () -> FabricaAcoes.criar("plano", "../listar"));
        assertThrows(AcaoNaoEncontradaException.class, () -> FabricaAcoes.criar("plano", ""));
        assertThrows(AcaoNaoEncontradaException.class, () -> FabricaAcoes.criar("plano", null));
    }

    @Test
    @DisplayName("Classe que existe mas não é ICommand é rejeitada")
    void classeQueNaoEhComando() {
        assertThrows(AcaoNaoEncontradaException.class, () -> FabricaAcoes.criar("plano", "naoComando"));
    }
}
