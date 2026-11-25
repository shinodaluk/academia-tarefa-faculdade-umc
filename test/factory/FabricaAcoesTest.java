package factory;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import apoio.DependenciasFalsas;
import config.Dependencias;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FabricaAcoesTest {

    private static final Dependencias DEPENDENCIAS = new DependenciasFalsas().criar();

    @Test
    @DisplayName("Cria a ação pelo recurso e pelo nome da ação")
    void criaAcao() throws Exception {
        assertInstanceOf(factory.plano.ListarAction.class, FabricaAcoes.criar("plano", "listar", DEPENDENCIAS));
        assertInstanceOf(factory.matricula.CalcularAdicionaisAction.class,
                FabricaAcoes.criar("matricula", "calcularAdicionais", DEPENDENCIAS));
    }

    @Test
    @DisplayName("Ação inexistente é rejeitada")
    void acaoInexistente() {
        assertThrows(AcaoNaoEncontradaException.class, () -> FabricaAcoes.criar("plano", "voar", DEPENDENCIAS));
    }

    @Test
    @DisplayName("Nome de ação com caracteres fora de letras é rejeitado")
    void acaoComCaracteresInvalidos() {
        assertThrows(AcaoNaoEncontradaException.class, () -> FabricaAcoes.criar("plano", "listar.x", DEPENDENCIAS));
        assertThrows(AcaoNaoEncontradaException.class, () -> FabricaAcoes.criar("plano", "../listar", DEPENDENCIAS));
        assertThrows(AcaoNaoEncontradaException.class, () -> FabricaAcoes.criar("plano", "", DEPENDENCIAS));
        assertThrows(AcaoNaoEncontradaException.class, () -> FabricaAcoes.criar("plano", null, DEPENDENCIAS));
    }

    @Test
    @DisplayName("Classe que existe mas não é ICommand é rejeitada")
    void classeQueNaoEhComando() {
        assertThrows(AcaoNaoEncontradaException.class, () -> FabricaAcoes.criar("plano", "naoComando", DEPENDENCIAS));
    }
}
