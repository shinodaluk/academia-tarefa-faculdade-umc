package api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NormalizacaoFichaTest {

    private static JsonObject json(String texto) {
        return JsonParser.parseString(texto).getAsJsonObject();
    }

    @Test
    @DisplayName("Ordem dos exercícios segue a posição na lista")
    void ordemPelaPosicao() {
        JsonObject ficha = json("{\"dataCriacao\":\"2026-01-01\",\"exercicios\":[{\"nome\":\"A\",\"ordem\":9},{\"nome\":\"B\"}]}");

        new NormalizacaoFicha().accept(ficha);

        assertEquals(1, ficha.getAsJsonArray("exercicios").get(0).getAsJsonObject().get("ordem").getAsInt());
        assertEquals(2, ficha.getAsJsonArray("exercicios").get(1).getAsJsonObject().get("ordem").getAsInt());
    }

    @Test
    @DisplayName("Data de criação ausente vira a data de hoje")
    void dataPadraoHoje() {
        JsonObject ficha = json("{\"exercicios\":[]}");

        new NormalizacaoFicha().accept(ficha);

        assertEquals(LocalDate.now().toString(), ficha.get("dataCriacao").getAsString());
    }

    @Test
    @DisplayName("Data de criação informada é mantida")
    void dataInformadaMantida() {
        JsonObject ficha = json("{\"dataCriacao\":\"2026-01-01\"}");

        new NormalizacaoFicha().accept(ficha);

        assertEquals("2026-01-01", ficha.get("dataCriacao").getAsString());
    }

    @Test
    @DisplayName("Lista de exercícios ausente vira lista vazia")
    void exerciciosPadraoVazio() {
        JsonObject ficha = json("{\"dataCriacao\":\"2026-01-01\"}");

        new NormalizacaoFicha().accept(ficha);

        assertEquals(0, ficha.getAsJsonArray("exercicios").size());
    }
}
