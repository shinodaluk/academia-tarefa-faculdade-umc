package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Regra de preço da matrícula: só cálculo, não acessa o banco. */
class MatriculaDaoTest {

    @ParameterizedTest(name = "{0} com fidelidade {1} e estudante={2} = {3}")
    @CsvSource({
        "100.00, 0,  false, 100.00",
        "100.00, 6,  false, 95.00",
        "100.00, 12, false, 90.00",
        "100.00, 0,  true,  85.00",
        "100.00, 12, true,  75.00",
        "99.90,  6,  true,  79.92",
        "129.90, 12, false, 116.91",
        "0.05,   6,  false, 0.05"
    })
    void calculaValorFinal(String base, int fidelidade, boolean estudante, String esperado) {
        assertEquals(new BigDecimal(esperado),
                new MatriculaDao().calcularValorFinal(new BigDecimal(base), fidelidade, estudante));
    }
}
