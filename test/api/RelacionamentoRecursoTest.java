package api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import apoio.RepositorioFalso;
import com.google.gson.Gson;
import java.util.List;
import java.util.Map;
import model.Aluno;
import model.FichaTreino;
import model.Matricula;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RelacionamentoRecursoTest {

    private RoteadorApi roteador;

    @BeforeEach
    void preparar() {
        Aluno ana = Aluno.getBuilder().comId(1).comNome("Ana").constroi();
        ana.vincular(new Aluno.Relacionamentos() {
            @Override
            public List<Matricula> matriculasDoAluno(int alunoId) {
                return List.of(Matricula.getBuilder().comId(10).comAlunoId(alunoId).constroi());
            }

            @Override
            public List<FichaTreino> fichasDoAluno(int alunoId) {
                return List.of();
            }

            @Override
            public java.util.Optional<model.AvaliacaoFisica> avaliacaoDoAluno(int alunoId) {
                return java.util.Optional.empty();
            }
        });
        RepositorioFalso<Aluno> alunos = new RepositorioFalso<Aluno>().com(1, ana);

        roteador = new RoteadorApi(Map.of("alunos",
                new ControladorRest<>(Aluno.class, alunos, Validacoes.aluno(), new Gson())
                        .comSubrecurso("matriculas", new RelacionamentoRecurso<>(alunos, Aluno::getMatriculas))
                        .comSubrecurso("fichas", new RelacionamentoRecurso<>(alunos, Aluno::getFichas))));
    }

    private RespostaApi chamar(String metodo, String caminho) {
        return roteador.atender(RequisicaoApi.de(metodo, caminho, Map.of(), "", "/api"));
    }

    @Test
    @DisplayName("GET /alunos/{id}/matriculas devolve as matrículas do aluno")
    void listaRelacionados() {
        RespostaApi resposta = chamar("GET", "/alunos/1/matriculas");

        assertEquals(200, resposta.status());
        List<?> matriculas = (List<?>) resposta.corpo();
        assertEquals(10, ((Matricula) matriculas.get(0)).getId());
    }

    @Test
    @DisplayName("Sem relacionados: 200 com lista vazia")
    void listaVazia() {
        RespostaApi resposta = chamar("GET", "/alunos/1/fichas");

        assertEquals(200, resposta.status());
        assertEquals(List.of(), resposta.corpo());
    }

    @Test
    @DisplayName("Dono inexistente: 404")
    void donoInexistente() {
        assertEquals(404, chamar("GET", "/alunos/9/matriculas").status());
    }

    @Test
    @DisplayName("Sub-recurso é só leitura: 405 com Allow GET")
    void somenteLeitura() {
        RespostaApi resposta = chamar("POST", "/alunos/1/matriculas");

        assertEquals(405, resposta.status());
        assertEquals("GET", resposta.cabecalhos().get("Allow"));
    }
}
