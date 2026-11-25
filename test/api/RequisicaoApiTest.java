package api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequisicaoApiTest {

    @Test
    @DisplayName("Caminho só com o recurso não tem segmentos")
    void caminhoDaColecao() {
        RequisicaoApi req = RequisicaoApi.de("GET", "/planos", Map.of(), "", "/app/api");

        assertEquals("planos", req.recurso());
        assertTrue(req.segmentos().isEmpty());
        assertEquals("/app/api/planos", req.urlRecurso());
    }

    @Test
    @DisplayName("Caminho com id e sub-recurso separa os segmentos")
    void caminhoComIdESubrecurso() {
        RequisicaoApi req = RequisicaoApi.de("GET", "/matriculas/7/adicionais", Map.of(), "", "/app/api");

        assertEquals("matriculas", req.recurso());
        assertEquals(List.of("7", "adicionais"), req.segmentos());
    }

    @Test
    @DisplayName("Barra final é ignorada")
    void barraFinalIgnorada() {
        RequisicaoApi req = RequisicaoApi.de("GET", "/planos/3/", Map.of(), "", "/app/api");

        assertEquals(List.of("3"), req.segmentos());
    }

    @Test
    @DisplayName("Caminho vazio resulta em recurso vazio")
    void caminhoVazio() {
        RequisicaoApi req = RequisicaoApi.de("GET", null, Map.of(), "", "/app/api");

        assertEquals("", req.recurso());
        assertTrue(req.segmentos().isEmpty());
    }

    @Test
    @DisplayName("Método HTTP é normalizado para maiúsculas")
    void metodoMaiusculo() {
        assertEquals("DELETE", RequisicaoApi.de("delete", "/planos/1", Map.of(), "", "").metodo());
    }
}
