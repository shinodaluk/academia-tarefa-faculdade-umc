package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MontadorAdicionaisTest {

    private final Servico base = Matricula.getBuilder().comDescricao("Mensal").comCustoBase(Dinheiro.de("100")).constroi();

    private static ServicoAdicional adicional(String codigo) {
        return new ServicoAdicional(codigo, codigo, Dinheiro.de("1"));
    }

    @Test
    @DisplayName("Cada código gera o decorator correspondente")
    void codigoGeraDecorator() {
        assertInstanceOf(Bioimpedancia.class, MontadorAdicionais.decorar(base, adicional("BIOIMPEDANCIA")));
        assertInstanceOf(CadeiraMassagem.class, MontadorAdicionais.decorar(base, adicional("CADEIRA_MASSAGEM")));
        assertInstanceOf(PreTreino.class, MontadorAdicionais.decorar(base, adicional("PRE_TREINO")));
    }

    @Test
    @DisplayName("Código desconhecido é rejeitado")
    void codigoDesconhecido() {
        assertThrows(IllegalArgumentException.class, () -> MontadorAdicionais.decorar(base, adicional("SAUNA")));
    }

    @Test
    @DisplayName("Informa quais códigos são reconhecidos")
    void reconhece() {
        assertTrue(MontadorAdicionais.reconhece("PRE_TREINO"));
        assertFalse(MontadorAdicionais.reconhece("SAUNA"));
        assertFalse(MontadorAdicionais.reconhece(null));
    }

    @Test
    @DisplayName("Lista os códigos reconhecidos em ordem")
    void codigos() {
        assertEquals(List.of("BIOIMPEDANCIA", "CADEIRA_MASSAGEM", "PRE_TREINO"), MontadorAdicionais.codigos());
    }
}
