package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DinheiroTest {

    private final Gson gson = new Gson();

    @Test
    @DisplayName("Valores iguais com escalas diferentes são iguais")
    void igualdadeIgnoraEscala() {
        assertEquals(Dinheiro.de("129.9"), Dinheiro.de("129.90"));
        assertEquals(Dinheiro.de("129.9").hashCode(), Dinheiro.de("129.90").hashCode());
    }

    @Test
    @DisplayName("Texto sempre com duas casas decimais")
    void textoComDuasCasas() {
        assertEquals("129.90", Dinheiro.de("129.9").toString());
        assertEquals("0.00", Dinheiro.ZERO.toString());
    }

    @Test
    @DisplayName("Soma é exata, sem erro de ponto flutuante")
    void somaExata() {
        assertEquals(Dinheiro.de("0.30"), Dinheiro.de("0.1").somar(Dinheiro.de("0.2")));
    }

    @Test
    @DisplayName("Arredonda para centavos com HALF_UP")
    void arredondaCentavos() {
        assertEquals(Dinheiro.de("10.01"), Dinheiro.de("10.005"));
        assertEquals(Dinheiro.de("10.00"), Dinheiro.de("10.004"));
    }

    @Test
    @DisplayName("Valor negativo é rejeitado")
    void rejeitaNegativo() {
        assertThrows(IllegalArgumentException.class, () -> Dinheiro.de("-0.01"));
    }

    @Test
    @DisplayName("Texto que não é número é rejeitado")
    void rejeitaTextoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> Dinheiro.de("abc"));
    }

    @Test
    @DisplayName("Nulo é rejeitado")
    void rejeitaNulo() {
        assertThrows(NullPointerException.class, () -> Dinheiro.de((BigDecimal) null));
    }

    @Test
    @DisplayName("Compara pelo valor")
    void comparaPorValor() {
        assertTrue(Dinheiro.de("10").compareTo(Dinheiro.de("9.99")) > 0);
    }

    @Test
    @DisplayName("Vira número no JSON, com duas casas")
    void serializaComoNumero() {
        assertEquals("{\"valor\":129.90}", gson.toJson(new Envelope(Dinheiro.de("129.9"))));
    }

    @Test
    @DisplayName("Lê número ou texto do JSON")
    void desserializa() {
        assertEquals(Dinheiro.de("12.30"), gson.fromJson("{\"valor\":12.3}", Envelope.class).valor);
        assertEquals(Dinheiro.de("12.30"), gson.fromJson("{\"valor\":\"12.3\"}", Envelope.class).valor);
        assertNull(gson.fromJson("{\"valor\":null}", Envelope.class).valor);
    }

    private static final class Envelope {
        private final Dinheiro valor;

        Envelope(Dinheiro valor) {
            this.valor = valor;
        }
    }
}
