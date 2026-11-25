package util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TextoSeguroTest {

    @Test
    @DisplayName("Texto comum passa igual, com acentos")
    void textoComum() {
        assertEquals("Deletado com sucesso, não há vínculos", TextoSeguro.paraScript("Deletado com sucesso, não há vínculos"));
    }

    @Test
    @DisplayName("Não deixa fechar a tag script")
    void naoFechaScript() {
        String seguro = TextoSeguro.paraScript("</script><script>alert(1)</script>");

        assertFalse(seguro.contains("<"));
        assertFalse(seguro.contains(">"));
    }

    @Test
    @DisplayName("Escapa aspas, barra invertida e quebra de linha para caber numa string JS/JSON")
    void escapaString() {
        assertEquals("a\\\"b\\\\c\\nd", TextoSeguro.paraScript("a\"b\\c\nd"));
    }

    @Test
    @DisplayName("Nulo vira texto vazio")
    void nulo() {
        assertEquals("", TextoSeguro.paraScript(null));
    }
}
