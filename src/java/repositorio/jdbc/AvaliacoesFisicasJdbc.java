package repositorio.jdbc;

import dao.AvaliacaoFisicaDao;
import java.util.Optional;
import model.AvaliacaoFisica;
import repositorio.AvaliacoesFisicas;

/** Adapta o AvaliacaoFisicaDao à interface AvaliacoesFisicas. */
public class AvaliacoesFisicasJdbc implements AvaliacoesFisicas {

    private final AvaliacaoFisicaDao dao;

    public AvaliacoesFisicasJdbc(AvaliacaoFisicaDao dao) {
        this.dao = dao;
    }

    @Override
    public Optional<AvaliacaoFisica> doAluno(int alunoId) throws Exception {
        return dao.consultarPorAluno(alunoId);
    }

    @Override
    public boolean salvar(AvaliacaoFisica avaliacao) throws Exception {
        boolean nova = dao.consultarPorAluno(avaliacao.getAlunoId()).isEmpty();
        dao.salvar(avaliacao);
        return nova;
    }

    @Override
    public void excluirDoAluno(int alunoId) throws Exception {
        dao.excluirPorAluno(alunoId);
    }
}
