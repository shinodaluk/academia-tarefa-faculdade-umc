package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RelacionamentosEntidadesTest {

    /** Fonte falsa que registra cada consulta feita. */
    private static class FonteAluno implements Aluno.Relacionamentos {
        final List<String> consultas = new ArrayList<>();

        @Override
        public List<Matricula> matriculasDoAluno(int alunoId) {
            consultas.add("matriculas:" + alunoId);
            return List.of(Matricula.getBuilder().comId(10).comAlunoId(alunoId).constroi());
        }

        @Override
        public List<FichaTreino> fichasDoAluno(int alunoId) {
            consultas.add("fichas:" + alunoId);
            return List.of(FichaTreino.getBuilder().comId(20).comAlunoId(alunoId).constroi());
        }

        @Override
        public Optional<AvaliacaoFisica> avaliacaoDoAluno(int alunoId) {
            consultas.add("avaliacao:" + alunoId);
            return Optional.of(AvaliacaoFisica.getBuilder().comAlunoId(alunoId)
                    .comPesoKg(new BigDecimal("70")).comAlturaM(new BigDecimal("1.75")).constroi());
        }
    }

    @Test
    @DisplayName("Aluno sem fonte vinculada tem relacionamentos vazios")
    void alunoNovo() {
        Aluno aluno = Aluno.getBuilder().comId(1).constroi();

        assertTrue(aluno.getMatriculas().isEmpty());
        assertTrue(aluno.getFichas().isEmpty());
        assertTrue(aluno.getAvaliacaoFisica().isEmpty());
    }

    @Test
    @DisplayName("Vincular não consulta; cada relacionamento é buscado só quando acessado")
    void cargaSobDemanda() {
        FonteAluno fonte = new FonteAluno();
        Aluno aluno = Aluno.getBuilder().comId(7).constroi();

        aluno.vincular(fonte);
        assertTrue(fonte.consultas.isEmpty());

        assertEquals(10, aluno.getMatriculas().get(0).getId());
        assertEquals(List.of("matriculas:7"), fonte.consultas);
    }

    @Test
    @DisplayName("Acessos repetidos usam o valor já carregado")
    void semConsultaRepetida() {
        FonteAluno fonte = new FonteAluno();
        Aluno aluno = Aluno.getBuilder().comId(7).constroi();
        aluno.vincular(fonte);

        aluno.getFichas();
        aluno.getFichas();
        aluno.getAvaliacaoFisica();
        aluno.getAvaliacaoFisica();

        assertEquals(List.of("fichas:7", "avaliacao:7"), fonte.consultas);
        assertEquals(new BigDecimal("22.9"), aluno.getAvaliacaoFisica().get().getImc());
    }

    @Test
    @DisplayName("Plano carrega as matrículas dele sob demanda")
    void planoMatriculas() {
        List<Integer> consultados = new ArrayList<>();
        Plano plano = Plano.getBuilder().comId(3).constroi();

        plano.vincular(planoId -> {
            consultados.add(planoId);
            return List.of(Matricula.getBuilder().comPlanoId(planoId).constroi());
        });

        assertTrue(consultados.isEmpty());
        assertEquals(3, plano.getMatriculas().get(0).getPlanoId());
        plano.getMatriculas();
        assertEquals(List.of(3), consultados);
    }

    @Test
    @DisplayName("Instrutor carrega as fichas dele sob demanda")
    void instrutorFichas() {
        List<Integer> consultados = new ArrayList<>();
        Instrutor instrutor = Instrutor.getBuilder().comId(4).constroi();

        instrutor.vincular(instrutorId -> {
            consultados.add(instrutorId);
            return List.of(FichaTreino.getBuilder().comInstrutorId(instrutorId).constroi());
        });

        assertTrue(consultados.isEmpty());
        assertEquals(4, instrutor.getFichas().get(0).getInstrutorId());
        assertEquals(List.of(4), consultados);
    }

    @Test
    @DisplayName("Relacionamentos ficam fora do JSON: serializar não dispara consulta")
    void foraDoJson() {
        FonteAluno fonte = new FonteAluno();
        Aluno aluno = Aluno.getBuilder().comId(7).comNome("Ana").constroi();
        aluno.vincular(fonte);

        String json = new com.google.gson.Gson().toJson(aluno);

        assertTrue(fonte.consultas.isEmpty());
        assertTrue(!json.contains("matriculas"));
    }
}
