package repositorio;

import java.util.Optional;
import model.AvaliacaoFisica;

/** Avaliação física do aluno (1:1): buscada, gravada e removida sempre pelo aluno. */
public interface AvaliacoesFisicas {

    Optional<AvaliacaoFisica> doAluno(int alunoId) throws Exception;

    /** Cria ou substitui a avaliação do aluno; true quando criou. */
    boolean salvar(AvaliacaoFisica avaliacao) throws Exception;

    void excluirDoAluno(int alunoId) throws Exception;
}
