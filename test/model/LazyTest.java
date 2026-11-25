package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LazyTest {

    @Test
    @DisplayName("Não carrega nada antes do primeiro acesso")
    void naoCarregaAntes() {
        AtomicInteger cargas = new AtomicInteger();
        Lazy<String> lazy = Lazy.de(() -> "valor" + cargas.incrementAndGet());

        assertEquals(0, cargas.get());
        assertFalse(lazy.carregado());
    }

    @Test
    @DisplayName("Carrega no primeiro acesso e reaproveita nos seguintes")
    void carregaUmaVez() {
        AtomicInteger cargas = new AtomicInteger();
        Lazy<String> lazy = Lazy.de(() -> "valor" + cargas.incrementAndGet());

        String primeiro = lazy.obter();
        String segundo = lazy.obter();

        assertEquals("valor1", primeiro);
        assertSame(primeiro, segundo);
        assertEquals(1, cargas.get());
        assertTrue(lazy.carregado());
    }

    @Test
    @DisplayName("Valor pronto não consulta nada")
    void pronto() {
        Lazy<String> lazy = Lazy.pronto("fixo");

        assertTrue(lazy.carregado());
        assertEquals("fixo", lazy.obter());
    }

    @Test
    @DisplayName("Falha do banco vira IllegalStateException com a causa; tenta de novo no próximo acesso")
    void falhaNaCarga() {
        AtomicInteger tentativas = new AtomicInteger();
        Lazy<String> lazy = Lazy.de(() -> {
            if (tentativas.incrementAndGet() == 1) {
                throw new SQLException("conexão recusada");
            }
            return "ok";
        });

        IllegalStateException erro = assertThrows(IllegalStateException.class, lazy::obter);
        assertTrue(erro.getCause() instanceof SQLException);
        assertEquals("ok", lazy.obter());
    }
}
