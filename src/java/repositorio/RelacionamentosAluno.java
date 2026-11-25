package repositorio;

import java.util.List;
import java.util.Optional;
import model.Aluno;
import model.AvaliacaoFisica;
import model.FichaTreino;
import model.Matricula;

/** Fonte dos relacionamentos do aluno, montada sobre os outros repositórios. */
public class RelacionamentosAluno implements Aluno.Relacionamentos {

    private final Repositorio<Matricula> matriculas;
    private final Repositorio<FichaTreino> fichas;
    private final AvaliacoesFisicas avaliacoes;

    public RelacionamentosAluno(Repositorio<Matricula> matriculas, Repositorio<FichaTreino> fichas,
            AvaliacoesFisicas avaliacoes) {
        this.matriculas = matriculas;
        this.fichas = fichas;
        this.avaliacoes = avaliacoes;
    }

    public void vincular(Aluno aluno) {
        aluno.vincular(this);
    }

    @Override
    public List<Matricula> matriculasDoAluno(int alunoId) throws Exception {
        return matriculas.listar(Filtros.de("alunoId", String.valueOf(alunoId)));
    }

    @Override
    public List<FichaTreino> fichasDoAluno(int alunoId) throws Exception {
        return fichas.listar(Filtros.de("alunoId", String.valueOf(alunoId)));
    }

    @Override
    public Optional<AvaliacaoFisica> avaliacaoDoAluno(int alunoId) throws Exception {
        return avaliacoes.doAluno(alunoId);
    }
}
