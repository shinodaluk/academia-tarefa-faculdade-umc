package api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dao.RegistroNaoEncontradoException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import model.Matricula;
import model.ServicoAdicional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AdicionaisMatriculaRecursoTest {

    private static final Map<String, ServicoAdicional> CATALOGO = Map.of(
            "BIOIMPEDANCIA", new ServicoAdicional("BIOIMPEDANCIA", "Bioimpedância", new BigDecimal("20.00")),
            "PRE_TREINO", new ServicoAdicional("PRE_TREINO", "Pré-treino", new BigDecimal("12.30")));

    private RoteadorApi roteador;

    @BeforeEach
    void preparar() {
        AdicionaisMatriculaRecurso recurso = new AdicionaisMatriculaRecurso(
                id -> {
                    if (id != 1) {
                        throw new RegistroNaoEncontradoException("Matrícula não encontrada");
                    }
                    return Matricula.getBuilder().comId(1).comDescricao("Mensal").comCustoBase(129.90).constroi();
                },
                CATALOGO::get);
        roteador = new RoteadorApi(Map.of("matriculas", new SubrecursoDireto(recurso)));
    }

    private RespostaApi chamar(String metodo, String caminho, Map<String, String> parametros) {
        return roteador.atender(RequisicaoApi.de(metodo, caminho, parametros, "", ""));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> corpo(RespostaApi resposta) {
        return (Map<String, Object>) resposta.corpo();
    }

    @Test
    @DisplayName("GET com códigos aplica os decorators e retorna o total")
    void calculaTotal() {
        RespostaApi resposta = chamar("GET", "/matriculas/1/adicionais",
                Map.of("codigos", "BIOIMPEDANCIA,PRE_TREINO"));

        assertEquals(200, resposta.status());
        assertEquals(new BigDecimal("162.20"), corpo(resposta).get("totalMensal"));
        assertEquals("Mensal, Bioimpedância, Pré-treino", corpo(resposta).get("descricao"));
        assertEquals(2, ((List<?>) corpo(resposta).get("adicionais")).size());
    }

    @Test
    @DisplayName("GET sem códigos retorna só o custo base")
    void semCodigos() {
        RespostaApi resposta = chamar("GET", "/matriculas/1/adicionais", Map.of());

        assertEquals(new BigDecimal("129.90"), corpo(resposta).get("totalMensal"));
    }

    @Test
    @DisplayName("Código repetido é aplicado uma vez só")
    void codigoRepetido() {
        RespostaApi resposta = chamar("GET", "/matriculas/1/adicionais",
                Map.of("codigos", "PRE_TREINO, PRE_TREINO"));

        assertEquals(new BigDecimal("142.20"), corpo(resposta).get("totalMensal"));
    }

    @Test
    @DisplayName("Código desconhecido retorna 400")
    void codigoDesconhecido() {
        assertEquals(400, chamar("GET", "/matriculas/1/adicionais", Map.of("codigos", "SAUNA")).status());
    }

    @Test
    @DisplayName("Matrícula inexistente retorna 404")
    void matriculaInexistente() {
        assertEquals(404, chamar("GET", "/matriculas/9/adicionais", Map.of()).status());
    }

    @Test
    @DisplayName("Método diferente de GET retorna 405 com Allow")
    void metodoNaoPermitido() {
        RespostaApi resposta = chamar("POST", "/matriculas/1/adicionais", Map.of());

        assertEquals(405, resposta.status());
        assertEquals("GET", resposta.cabecalhos().get("Allow"));
    }

    /** Encaminha /matriculas/{id}/... direto ao sub-recurso, isolando-o do CRUD. */
    private record SubrecursoDireto(RecursoApi alvo) implements RecursoApi {
        @Override
        public RespostaApi tratar(RequisicaoApi requisicao) throws Exception {
            return alvo.tratar(requisicao);
        }
    }
}
