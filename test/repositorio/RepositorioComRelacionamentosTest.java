package repositorio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import apoio.AvaliacoesFisicasFalsas;
import apoio.RepositorioFalso;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.Aluno;
import model.AvaliacaoFisica;
import model.FichaTreino;
import model.Matricula;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RepositorioComRelacionamentosTest {

    @Test
    @DisplayName("buscar e listar vinculam cada entidade devolvida")
    void vinculaAoLer() throws Exception {
        RepositorioFalso<Aluno> base = new RepositorioFalso<Aluno>()
                .com(1, Aluno.getBuilder().comId(1).constroi())
                .com(2, Aluno.getBuilder().comId(2).constroi());
        List<Integer> vinculados = new ArrayList<>();
        Repositorio<Aluno> repositorio = new RepositorioComRelacionamentos<>(base, aluno -> vinculados.add(aluno.getId()));

        repositorio.buscar(2);
        repositorio.listar(Map.of());

        assertEquals(List.of(2, 1, 2), vinculados);
    }

    @Test
    @DisplayName("Gravações passam direto para o repositório decorado")
    void delegaGravacoes() throws Exception {
        RepositorioFalso<Aluno> base = new RepositorioFalso<>();
        Repositorio<Aluno> repositorio = new RepositorioComRelacionamentos<>(base, aluno -> { });
        Aluno aluno = Aluno.getBuilder().comNome("Ana").constroi();

        int id = repositorio.criar(aluno);
        repositorio.atualizar(aluno);
        repositorio.excluir(id);

        assertSame(aluno, base.ultimoCriado);
        assertSame(aluno, base.ultimoAtualizado);
        assertEquals(List.of(id), base.excluidos);
    }

    @Test
    @DisplayName("RelacionamentosAluno filtra matrículas e fichas pelo aluno")
    void relacionamentosAluno() throws Exception {
        RepositorioFalso<Matricula> matriculas = new RepositorioFalso<Matricula>()
                .com(10, Matricula.getBuilder().comId(10).comAlunoId(7).constroi());
        RepositorioFalso<FichaTreino> fichas = new RepositorioFalso<>();
        AvaliacoesFisicasFalsas avaliacoes = new AvaliacoesFisicasFalsas().com(AvaliacaoFisica.getBuilder()
                .comAlunoId(7).comPesoKg(BigDecimal.TEN).constroi());
        Repositorio<Aluno> alunos = new RepositorioComRelacionamentos<>(
                new RepositorioFalso<Aluno>().com(7, Aluno.getBuilder().comId(7).constroi()),
                new RelacionamentosAluno(matriculas, fichas, avaliacoes)::vincular);

        Aluno aluno = alunos.buscar(7);

        assertEquals(10, aluno.getMatriculas().get(0).getId());
        assertEquals(Map.of("alunoId", "7"), matriculas.ultimosFiltros);
        assertTrue(aluno.getFichas().isEmpty());
        assertEquals(Map.of("alunoId", "7"), fichas.ultimosFiltros);
        assertEquals(BigDecimal.TEN, aluno.getAvaliacaoFisica().get().getPesoKg());
    }
}
