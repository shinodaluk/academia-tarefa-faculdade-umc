package repositorio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FiltrosTest {

    @Test
    @DisplayName("Filtro numérico ausente ou vazio vale 0 (sem filtro)")
    void inteiroAusente() {
        assertEquals(0, Filtros.inteiro(Map.of(), "alunoId"));
        assertEquals(0, Filtros.inteiro(Map.of("alunoId", " "), "alunoId"));
        assertEquals(7, Filtros.inteiro(Map.of("alunoId", "7"), "alunoId"));
    }

    @Test
    @DisplayName("Filtro numérico inválido é erro, não é ignorado")
    void inteiroInvalido() {
        assertThrows(NumberFormatException.class, () -> Filtros.inteiro(Map.of("alunoId", "abc"), "alunoId"));
    }
}
