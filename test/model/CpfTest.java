package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CpfTest {

    @Test
    @DisplayName("Aceita só dígitos e guarda com máscara")
    void normalizaComMascara() {
        assertEquals("529.982.247-25", Cpf.de("52998224725").toString());
    }

    @Test
    @DisplayName("Aceita com máscara")
    void aceitaComMascara() {
        assertEquals(Cpf.de("52998224725"), Cpf.de("529.982.247-25"));
    }

    @Test
    @DisplayName("Rejeita quantidade de dígitos diferente de 11")
    void rejeitaTamanhoErrado() {
        assertThrows(IllegalArgumentException.class, () -> Cpf.de("1234567890"));
        assertThrows(IllegalArgumentException.class, () -> Cpf.de("123456789012"));
    }

    @Test
    @DisplayName("Rejeita letras e símbolos fora da máscara")
    void rejeitaCaracteresInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> Cpf.de("529.982.247-2X"));
        assertThrows(IllegalArgumentException.class, () -> Cpf.de("529 982 247 25"));
    }

    @Test
    @DisplayName("Rejeita nulo")
    void rejeitaNulo() {
        assertThrows(NullPointerException.class, () -> Cpf.de(null));
    }

    @Test
    @DisplayName("Confere os dígitos verificadores")
    void digitosVerificadores() {
        assertTrue(Cpf.de("529.982.247-25").digitosVerificadoresValidos());
        assertFalse(Cpf.de("529.982.247-26").digitosVerificadoresValidos());
        assertFalse(Cpf.de("111.111.111-11").digitosVerificadoresValidos());
    }

    @Test
    @DisplayName("Vira texto com máscara no JSON e é lido de volta")
    void json() {
        Gson gson = new Gson();

        assertEquals("\"529.982.247-25\"", gson.toJson(Cpf.de("52998224725")));
        assertEquals(Cpf.de("529.982.247-25"), gson.fromJson("\"52998224725\"", Cpf.class));
    }
}
