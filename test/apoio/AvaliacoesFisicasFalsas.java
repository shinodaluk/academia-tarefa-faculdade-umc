package apoio;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import model.AvaliacaoFisica;
import repositorio.AvaliacoesFisicas;

/** Avaliações em memória, uma por aluno, como a chave única do banco. */
public class AvaliacoesFisicasFalsas implements AvaliacoesFisicas {

    public final Map<Integer, AvaliacaoFisica> porAluno = new HashMap<>();

    public AvaliacoesFisicasFalsas com(AvaliacaoFisica avaliacao) {
        porAluno.put(avaliacao.getAlunoId(), avaliacao);
        return this;
    }

    @Override
    public Optional<AvaliacaoFisica> doAluno(int alunoId) {
        return Optional.ofNullable(porAluno.get(alunoId));
    }

    @Override
    public boolean salvar(AvaliacaoFisica avaliacao) {
        return porAluno.put(avaliacao.getAlunoId(), avaliacao) == null;
    }

    @Override
    public void excluirDoAluno(int alunoId) {
        porAluno.remove(alunoId);
    }
}
