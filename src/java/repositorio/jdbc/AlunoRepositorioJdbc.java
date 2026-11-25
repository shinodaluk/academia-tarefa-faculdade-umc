package repositorio.jdbc;

import repositorio.Filtros;
import repositorio.Repositorio;
import dao.AlunoDao;
import java.util.List;
import java.util.Map;
import model.Aluno;

/** Adapta o AlunoDao à interface Repositorio (API e telas). Filtros: ?busca=&status= */
public class AlunoRepositorioJdbc implements Repositorio<Aluno> {

    private final AlunoDao dao;

    public AlunoRepositorioJdbc(AlunoDao dao) {
        this.dao = dao;
    }

    @Override
    public List<Aluno> listar(Map<String, String> filtros) throws Exception {
        return dao.consultarTodos(Filtros.valor(filtros, "busca"), Filtros.valor(filtros, "status"));
    }

    @Override
    public Aluno buscar(int id) throws Exception {
        return dao.consultarById(comId(id));
    }

    @Override
    public int criar(Aluno aluno) throws Exception {
        return dao.cadastrar(aluno);
    }

    @Override
    public void atualizar(Aluno aluno) throws Exception {
        dao.atualizar(aluno);
    }

    @Override
    public void excluir(int id) throws Exception {
        dao.deletar(comId(id));
    }

    private static Aluno comId(int id) {
        return Aluno.getBuilder().comId(id).constroi();
    }
}
