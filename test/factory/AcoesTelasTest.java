package factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import apoio.DependenciasFalsas;
import apoio.HttpFalso;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import model.Dinheiro;
import model.Matricula;
import model.Plano;
import model.ServicoAdicional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.Mensagens;

/** Actions das telas com dependências falsas: sem banco nem Tomcat. */
class AcoesTelasTest {

    private DependenciasFalsas falsas;
    private HttpFalso.Requisicao requisicao;
    private HttpFalso.Resposta resposta;

    @BeforeEach
    void preparar() {
        falsas = new DependenciasFalsas();
        requisicao = new HttpFalso.Requisicao();
        resposta = new HttpFalso.Resposta();
    }

    private void executar(String recurso, String acao) throws Exception {
        FabricaAcoes.criar(recurso, acao, falsas.criar()).executar(requisicao.proxy(), resposta.proxy());
    }

    private static Plano plano(String nome) {
        return Plano.getBuilder().comNome(nome).comDuracaoMeses(1).comValorMensal(Dinheiro.de("99.90")).constroi();
    }

    @Test
    @DisplayName("Listar planos usa o repositório injetado e encaminha para a tela")
    void listarPlanos() throws Exception {
        falsas.planos.com(1, plano("Mensal"));

        executar("plano", "listar");

        assertEquals("plano/listar.jsp", requisicao.encaminhadoPara);
        assertTrue(requisicao.atributo("planosJson").contains("\"nome\":\"Mensal\""));
    }

    @Test
    @DisplayName("Filtro da tela de planos chega ao repositório")
    void listarPlanosSomenteAtivos() throws Exception {
        requisicao.com("somenteAtivos", "1");

        executar("plano", "listar");

        assertEquals("true", falsas.planos.ultimosFiltros.get("apenasAtivos"));
    }

    @Test
    @DisplayName("Falha ao listar mostra lista vazia e mensagem sem detalhes internos")
    void listarComFalha() throws Exception {
        falsas.planos.falha = new SQLException("Communications link failure");

        executar("plano", "listar");

        assertEquals("[]", requisicao.atributo("planosJson"));
        assertEquals(Mensagens.ERRO_GENERICO, requisicao.atributo("aviso"));
    }

    @Test
    @DisplayName("Salvar plano novo cria e redireciona para a edição do id gerado")
    void salvarPlanoNovo() throws Exception {
        requisicao.com("nome", "Trimestral").com("duracaoMeses", "3").com("valorMensal", "109.90").com("ativo", "on");

        executar("plano", "salvar");

        assertEquals("Trimestral", falsas.planos.ultimoCriado.getNome());
        assertEquals(Dinheiro.de("109.90"), falsas.planos.ultimoCriado.getValorMensal());
        assertEquals("/app/plano?acao=editar&id=100", requisicao.atributo("redirect"));
    }

    @Test
    @DisplayName("Salvar plano existente atualiza com o id do formulário")
    void salvarPlanoExistente() throws Exception {
        requisicao.com("id", "7").com("nome", "Anual").com("duracaoMeses", "12").com("valorMensal", "89.90");

        executar("plano", "salvar");

        assertEquals(7, falsas.planos.ultimoAtualizado.getId());
        assertFalse(falsas.planos.ultimoAtualizado.isAtivo());
    }

    @Test
    @DisplayName("Salvar com conflito volta ao formulário com mensagem amigável")
    void salvarComConflito() throws Exception {
        falsas.planos.falha = new SQLIntegrityConstraintViolationException("Duplicate entry 'Mensal'");
        requisicao.com("nome", "Mensal").com("duracaoMeses", "1").com("valorMensal", "1");

        executar("plano", "salvar");

        assertEquals("/app/plano?acao=criar&erro=" + URLEncoder.encode(Mensagens.CONFLITO, StandardCharsets.UTF_8),
                requisicao.atributo("redirect"));
    }

    @Test
    @DisplayName("Excluir plano com matrículas avisa em vez de quebrar")
    void excluirPlanoComVinculo() throws Exception {
        falsas.planos.falha = new SQLIntegrityConstraintViolationException("fk_matricula_plano");
        requisicao.com("id", "1");

        executar("plano", "excluir");

        assertTrue(requisicao.atributo("redirect").contains("aviso=O+plano+tem+matr"));
    }

    @Test
    @DisplayName("Listar alunos traz os indicadores da fonte injetada")
    void listarAlunosComIndicadores() throws Exception {
        falsas.indicadores.put("alunosAtivos", 42);

        executar("aluno", "listar");

        assertTrue(requisicao.atributo("indicadoresJson").contains("\"alunosAtivos\":42"));
    }

    @Test
    @DisplayName("Excluir aluno usa o id do formulário")
    void excluirAluno() throws Exception {
        requisicao.com("id", "5");

        executar("aluno", "excluir");

        assertEquals(5, falsas.alunos.excluidos.get(0));
    }

    @Test
    @DisplayName("Salvar ficha lê os exercícios do formulário na ordem")
    void salvarFicha() throws Exception {
        requisicao.com("alunoId", "1").com("objetivo", "Hipertrofia").com("dataCriacao", "2026-11-24")
                .com("exercicios[0].nome", "Supino").com("exercicios[0].series", "4").com("exercicios[0].repeticoes", "10")
                .com("exercicios[1].nome", "Remada").com("exercicios[1].series", "3").com("exercicios[1].repeticoes", "12");

        executar("ficha", "salvar");

        assertEquals(2, falsas.fichas.ultimoCriado.getExercicios().size());
        assertEquals("Remada", falsas.fichas.ultimoCriado.getExercicios().get(1).getNome());
        assertEquals(2, falsas.fichas.ultimoCriado.getExercicios().get(1).getOrdem());
    }

    @Test
    @DisplayName("Calcular adicionais pela tela responde o orçamento em JSON")
    void calcularAdicionais() throws Exception {
        falsas.matriculas.com(1, Matricula.getBuilder().comId(1).comDescricao("Mensal")
                .comCustoBase(Dinheiro.de("129.90")).constroi());
        falsas.adicionais.add(new ServicoAdicional("PRE_TREINO", "Pré-treino", Dinheiro.de("12.30")));
        requisicao.metodo = "POST";
        requisicao.corpo = "{\"id\":1,\"adicionais\":[\"PRE_TREINO\"]}";

        executar("matricula", "calcularAdicionais");

        assertEquals(200, resposta.status);
        assertTrue(resposta.corpo.toString().contains("\"totalMensal\":142.20"), resposta.corpo.toString());
    }

    @Test
    @DisplayName("Calcular adicionais de matrícula inexistente responde 404")
    void calcularAdicionaisMatriculaInexistente() throws Exception {
        requisicao.metodo = "POST";
        requisicao.corpo = "{\"id\":9,\"adicionais\":[]}";

        executar("matricula", "calcularAdicionais");

        assertEquals(404, resposta.status);
    }

    @Test
    @DisplayName("Tela de adicionais lista só os serviços que têm decorator")
    void telaAdicionais() throws Exception {
        falsas.adicionais.add(new ServicoAdicional("PRE_TREINO", "Pré-treino", Dinheiro.de("12.30")));
        falsas.adicionais.add(new ServicoAdicional("SAUNA", "Sauna", Dinheiro.de("5")));

        executar("matricula", "adicionais");

        assertTrue(requisicao.atributo("servicosJson").contains("PRE_TREINO"));
        assertFalse(requisicao.atributo("servicosJson").contains("SAUNA"));
    }
}
