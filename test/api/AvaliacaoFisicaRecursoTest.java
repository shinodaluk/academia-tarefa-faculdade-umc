package api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import apoio.AvaliacoesFisicasFalsas;
import apoio.RepositorioFalso;
import com.google.gson.Gson;
import java.math.BigDecimal;
import java.util.Map;
import model.Aluno;
import model.AvaliacaoFisica;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AvaliacaoFisicaRecursoTest {

    private static final String CORPO = "{\"dataAvaliacao\":\"2026-02-10\",\"pesoKg\":78.4,\"alturaM\":1.76}";

    private AvaliacoesFisicasFalsas avaliacoes;
    private RoteadorApi roteador;

    @BeforeEach
    void preparar() {
        RepositorioFalso<Aluno> alunos = new RepositorioFalso<Aluno>()
                .com(1, Aluno.getBuilder().comId(1).comNome("Ana").constroi())
                .com(2, Aluno.getBuilder().comId(2).comNome("Bruno").constroi());
        avaliacoes = new AvaliacoesFisicasFalsas().com(AvaliacaoFisica.getBuilder()
                .comAlunoId(1).comPesoKg(new BigDecimal("61.20")).comAlturaM(new BigDecimal("1.63")).constroi());

        Gson gson = new Gson();
        AvaliacaoFisicaRecurso recurso = new AvaliacaoFisicaRecurso(
                alunos, avaliacoes, Validacoes.avaliacaoFisica(), gson);
        roteador = new RoteadorApi(Map.of("alunos",
                new ControladorRest<>(Aluno.class, alunos, Validacoes.aluno(), gson).comSubrecurso("avaliacao", recurso)));
    }

    private RespostaApi chamar(String metodo, String caminho, String corpo) {
        return roteador.atender(RequisicaoApi.de(metodo, caminho, Map.of(), corpo, "/api"));
    }

    @Test
    @DisplayName("GET retorna a avaliação do aluno")
    void buscaAvaliacao() {
        RespostaApi resposta = chamar("GET", "/alunos/1/avaliacao", "");

        assertEquals(200, resposta.status());
        assertEquals(1, ((AvaliacaoFisica) resposta.corpo()).getAlunoId());
    }

    @Test
    @DisplayName("GET de aluno sem avaliação responde 404")
    void alunoSemAvaliacao() {
        assertEquals(404, chamar("GET", "/alunos/2/avaliacao", "").status());
    }

    @Test
    @DisplayName("Aluno inexistente responde 404")
    void alunoInexistente() {
        assertEquals(404, chamar("GET", "/alunos/9/avaliacao", "").status());
        assertEquals(404, chamar("PUT", "/alunos/9/avaliacao", CORPO).status());
    }

    @Test
    @DisplayName("PUT em aluno sem avaliação cria: 201 com Location")
    void putCria() {
        RespostaApi resposta = chamar("PUT", "/alunos/2/avaliacao", CORPO);

        assertEquals(201, resposta.status());
        assertEquals("/api/alunos/2/avaliacao", resposta.cabecalhos().get("Location"));
        assertEquals(new BigDecimal("25.3"), avaliacoes.porAluno.get(2).getImc());
    }

    @Test
    @DisplayName("PUT em aluno com avaliação substitui: 200, continua uma só (1:1)")
    void putSubstitui() {
        RespostaApi resposta = chamar("PUT", "/alunos/1/avaliacao", CORPO);

        assertEquals(200, resposta.status());
        assertEquals(new BigDecimal("78.4"), avaliacoes.porAluno.get(1).getPesoKg());
        assertEquals(1, avaliacoes.porAluno.size());
    }

    @Test
    @DisplayName("PUT usa o aluno da URL, não o do corpo")
    void alunoDaUrl() {
        chamar("PUT", "/alunos/2/avaliacao", CORPO.replace("{", "{\"alunoId\":1,"));

        assertEquals(2, avaliacoes.porAluno.get(2).getAlunoId());
        assertEquals(new BigDecimal("61.20"), avaliacoes.porAluno.get(1).getPesoKg());
    }

    @Test
    @DisplayName("PUT inválido responde 400 sem gravar")
    void putInvalido() {
        RespostaApi resposta = chamar("PUT", "/alunos/2/avaliacao", "{\"pesoKg\":-1}");

        assertEquals(400, resposta.status());
        assertFalse(avaliacoes.porAluno.containsKey(2));
    }

    @Test
    @DisplayName("DELETE remove a avaliação (204); sem avaliação, 404")
    void delete() {
        assertEquals(204, chamar("DELETE", "/alunos/1/avaliacao", "").status());
        assertTrue(avaliacoes.porAluno.isEmpty());
        assertEquals(404, chamar("DELETE", "/alunos/1/avaliacao", "").status());
    }

    @Test
    @DisplayName("POST não é permitido: 405 com Allow")
    void post() {
        RespostaApi resposta = chamar("POST", "/alunos/1/avaliacao", CORPO);

        assertEquals(405, resposta.status());
        assertEquals("GET, PUT, DELETE", resposta.cabecalhos().get("Allow"));
    }
}
