package api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.Dinheiro;
import model.Plano;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ControladorRestTest {

    private PlanosEmMemoria planos;
    private RoteadorApi roteador;

    @BeforeEach
    void preparar() {
        planos = new PlanosEmMemoria();
        planos.adicionar("Mensal", 1, "129.90");
        planos.adicionar("Anual", 12, "89.90");

        Validador<Plano> validador = plano -> {
            List<String> erros = new ArrayList<>();
            if (plano.getNome() == null || plano.getNome().isBlank()) {
                erros.add("nome é obrigatório");
            }
            return erros;
        };
        roteador = new RoteadorApi(Map.of(
                "planos", new ControladorRest<>(Plano.class, planos, validador, new Gson())));
    }

    private RespostaApi chamar(String metodo, String caminho, String corpo) {
        return roteador.atender(RequisicaoApi.de(metodo, caminho, Map.of(), corpo, "/app/api"));
    }

    private RespostaApi chamar(String metodo, String caminho) {
        return chamar(metodo, caminho, "");
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> erro(RespostaApi resposta) {
        return (Map<String, Object>) resposta.corpo();
    }

    @Test
    @DisplayName("GET na coleção retorna 200 com todos os itens")
    void listar() {
        RespostaApi resposta = chamar("GET", "/planos");

        assertEquals(200, resposta.status());
        assertEquals(2, ((List<?>) resposta.corpo()).size());
    }

    @Test
    @DisplayName("GET na coleção repassa a query string como filtros")
    void listarRepassaFiltros() {
        roteador.atender(RequisicaoApi.de("GET", "/planos", Map.of("apenasAtivos", "true"), "", "/app/api"));

        assertEquals("true", planos.ultimosFiltros.get("apenasAtivos"));
    }

    @Test
    @DisplayName("GET por id retorna 200 com o item")
    void buscarPorId() {
        RespostaApi resposta = chamar("GET", "/planos/2");

        assertEquals(200, resposta.status());
        assertEquals("Anual", ((Plano) resposta.corpo()).getNome());
    }

    @Test
    @DisplayName("GET por id inexistente retorna 404")
    void buscarInexistente() {
        RespostaApi resposta = chamar("GET", "/planos/99");

        assertEquals(404, resposta.status());
        assertEquals("Plano não encontrado", erro(resposta).get("error"));
    }

    @Test
    @DisplayName("Id não numérico retorna 400")
    void idInvalido() {
        assertEquals(400, chamar("GET", "/planos/abc").status());
    }

    @Test
    @DisplayName("POST cria o item e retorna 201 com Location e o item criado")
    void criar() {
        RespostaApi resposta = chamar("POST", "/planos",
                "{\"nome\":\"Trimestral\",\"duracaoMeses\":3,\"valorMensal\":109.90,\"ativo\":true}");

        assertEquals(201, resposta.status());
        assertEquals("/app/api/planos/3", resposta.cabecalhos().get("Location"));
        Plano criado = (Plano) resposta.corpo();
        assertEquals(3, criado.getId());
        assertEquals("Trimestral", criado.getNome());
        assertTrue(planos.existe(3));
    }

    @Test
    @DisplayName("POST ignora o id enviado no corpo")
    void criarIgnoraIdDoCorpo() {
        RespostaApi resposta = chamar("POST", "/planos", "{\"id\":1,\"nome\":\"Novo\",\"duracaoMeses\":1,\"valorMensal\":50}");

        assertEquals(3, ((Plano) resposta.corpo()).getId());
        assertEquals("Mensal", planos.listar(Map.of()).get(0).getNome());
    }

    @Test
    @DisplayName("POST com JSON malformado retorna 400")
    void criarJsonInvalido() {
        RespostaApi resposta = chamar("POST", "/planos", "{nome: ");

        assertEquals(400, resposta.status());
    }

    @Test
    @DisplayName("POST com tipo de campo errado retorna 400")
    void criarTipoInvalido() {
        RespostaApi resposta = chamar("POST", "/planos", "{\"nome\":\"X\",\"duracaoMeses\":\"muitos\"}");

        assertEquals(400, resposta.status());
    }

    @Test
    @DisplayName("POST com valor que o objeto de valor rejeita retorna 400 com o motivo")
    void criarValorRejeitadoPeloObjetoDeValor() {
        RespostaApi resposta = chamar("POST", "/planos", "{\"nome\":\"X\",\"duracaoMeses\":1,\"valorMensal\":-1}");

        assertEquals(400, resposta.status());
        assertEquals(List.of("Valor monetário não pode ser negativo: -1"), erro(resposta).get("detalhes"));
        assertFalse(planos.existe(3));
    }

    @Test
    @DisplayName("POST sem corpo retorna 400")
    void criarSemCorpo() {
        assertEquals(400, chamar("POST", "/planos").status());
    }

    @Test
    @DisplayName("POST com corpo que não é objeto retorna 400")
    void criarCorpoNaoObjeto() {
        assertEquals(400, chamar("POST", "/planos", "[1,2]").status());
    }

    @Test
    @DisplayName("POST que falha na validação retorna 400 com os detalhes e não grava")
    void criarInvalido() {
        RespostaApi resposta = chamar("POST", "/planos", "{\"duracaoMeses\":1}");

        assertEquals(400, resposta.status());
        assertEquals(List.of("nome é obrigatório"), erro(resposta).get("detalhes"));
        assertFalse(planos.existe(3));
    }

    @Test
    @DisplayName("PUT atualiza o item e retorna 200 com o item atualizado")
    void atualizar() {
        RespostaApi resposta = chamar("PUT", "/planos/1",
                "{\"nome\":\"Mensal Plus\",\"duracaoMeses\":1,\"valorMensal\":139.90,\"ativo\":true}");

        assertEquals(200, resposta.status());
        assertEquals("Mensal Plus", ((Plano) resposta.corpo()).getNome());
        assertEquals(Dinheiro.de("139.90"), planos.listar(Map.of()).get(0).getValorMensal());
    }

    @Test
    @DisplayName("PUT usa o id da URL, não o do corpo")
    void atualizarUsaIdDaUrl() {
        chamar("PUT", "/planos/1", "{\"id\":2,\"nome\":\"Trocado\",\"duracaoMeses\":1,\"valorMensal\":1}");

        assertEquals("Anual", planos.listar(Map.of()).get(1).getNome());
        assertEquals("Trocado", planos.listar(Map.of()).get(0).getNome());
    }

    @Test
    @DisplayName("PUT em id inexistente retorna 404")
    void atualizarInexistente() {
        RespostaApi resposta = chamar("PUT", "/planos/99", "{\"nome\":\"X\",\"duracaoMeses\":1,\"valorMensal\":1}");

        assertEquals(404, resposta.status());
    }

    @Test
    @DisplayName("PUT inválido retorna 400")
    void atualizarInvalido() {
        assertEquals(400, chamar("PUT", "/planos/1", "{\"nome\":\"\"}").status());
    }

    @Test
    @DisplayName("DELETE remove o item e retorna 204 sem corpo")
    void excluir() {
        RespostaApi resposta = chamar("DELETE", "/planos/1");

        assertEquals(204, resposta.status());
        assertNull(resposta.corpo());
        assertFalse(planos.existe(1));
    }

    @Test
    @DisplayName("DELETE em id inexistente retorna 404")
    void excluirInexistente() {
        assertEquals(404, chamar("DELETE", "/planos/99").status());
    }

    @Test
    @DisplayName("DELETE barrado por integridade retorna 409")
    void excluirComConflito() {
        planos.falhaAoExcluir = new SQLIntegrityConstraintViolationException("fk_matricula_plano");

        assertEquals(409, chamar("DELETE", "/planos/1").status());
    }

    @Test
    @DisplayName("Erro inesperado retorna 500 sem expor detalhes internos")
    void erroInterno() {
        planos.falhaAoExcluir = new IllegalStateException("senha do banco: segredo");

        RespostaApi resposta = chamar("DELETE", "/planos/1");

        assertEquals(500, resposta.status());
        assertFalse(erro(resposta).get("error").toString().contains("segredo"));
    }

    @Test
    @DisplayName("Filtro numérico inválido na query string retorna 400, não 500")
    void filtroNumericoInvalido() {
        planos.falhaAoExcluir = new NumberFormatException("For input string: \"abc\"");

        assertEquals(400, chamar("DELETE", "/planos/1").status());
    }

    @Test
    @DisplayName("Método não suportado na coleção retorna 405 com Allow")
    void metodoNaoPermitidoNaColecao() {
        RespostaApi resposta = chamar("DELETE", "/planos");

        assertEquals(405, resposta.status());
        assertEquals("GET, POST", resposta.cabecalhos().get("Allow"));
    }

    @Test
    @DisplayName("Método não suportado no item retorna 405 com Allow")
    void metodoNaoPermitidoNoItem() {
        RespostaApi resposta = chamar("POST", "/planos/1", "{}");

        assertEquals(405, resposta.status());
        assertEquals("GET, PUT, DELETE", resposta.cabecalhos().get("Allow"));
    }

    @Test
    @DisplayName("Recurso desconhecido retorna 404")
    void recursoDesconhecido() {
        assertEquals(404, chamar("GET", "/naoexiste").status());
    }

    @Test
    @DisplayName("Sub-recurso desconhecido retorna 404")
    void subrecursoDesconhecido() {
        assertEquals(404, chamar("GET", "/planos/1/qualquer").status());
    }

    @Test
    @DisplayName("Sub-recurso registrado recebe a requisição")
    void subrecursoRegistrado() {
        ControladorRest<Plano> controlador = new ControladorRest<>(Plano.class, planos, p -> List.of(), new Gson())
                .comSubrecurso("eco", req -> RespostaApi.ok(req.segmentos()));
        RoteadorApi comSub = new RoteadorApi(Map.of("planos", controlador));

        RespostaApi resposta = comSub.atender(RequisicaoApi.de("GET", "/planos/1/eco", Map.of(), "", ""));

        assertEquals(200, resposta.status());
        assertEquals(List.of("1", "eco"), resposta.corpo());
    }

    @Test
    @DisplayName("Normalizador ajusta o JSON antes da conversão")
    void normalizadorAplicado() {
        ControladorRest<Plano> controlador = new ControladorRest<>(Plano.class, planos, p -> List.of(), new Gson())
                .comNormalizador(json -> json.addProperty("descricao", "padrão"));
        RoteadorApi comNormalizador = new RoteadorApi(Map.of("planos", controlador));

        RespostaApi resposta = comNormalizador.atender(RequisicaoApi.de("POST", "/planos",
                Map.of(), "{\"nome\":\"X\",\"duracaoMeses\":1,\"valorMensal\":1}", ""));

        assertEquals("padrão", ((Plano) resposta.corpo()).getDescricao());
    }
}
