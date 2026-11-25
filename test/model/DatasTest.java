package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.SimpleDateFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DatasTest {

    @Test
    @DisplayName("Converte data ISO válida")
    void dataValida() {
        assertEquals("2024-02-29", new SimpleDateFormat("yyyy-MM-dd").format(Datas.deIso("2024-02-29")));
    }

    @Test
    @DisplayName("Rejeita dia inexistente em vez de rolar para o mês seguinte")
    void rejeitaDiaInexistente() {
        assertThrows(IllegalArgumentException.class, () -> Datas.deIso("2024-02-30"));
    }

    @Test
    @DisplayName("Rejeita mês inexistente e texto fora do formato")
    void rejeitaFormatoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> Datas.deIso("2024-13-01"));
        assertThrows(IllegalArgumentException.class, () -> Datas.deIso("10/05/1999"));
        assertThrows(IllegalArgumentException.class, () -> Datas.deIso(""));
    }

    @Test
    @DisplayName("Nulo continua nulo (campo opcional)")
    void nulo() {
        assertNull(Datas.deIso(null));
    }
}
