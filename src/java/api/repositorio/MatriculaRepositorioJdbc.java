package api.repositorio;

import api.Repositorio;
import dao.MatriculaDao;
import java.util.List;
import java.util.Map;
import model.Matricula;

/** Adapta o MatriculaDao à interface usada pela API. Filtros: ?situacao= */
public class MatriculaRepositorioJdbc implements Repositorio<Matricula> {

    private final MatriculaDao dao;

    public MatriculaRepositorioJdbc(MatriculaDao dao) {
        this.dao = dao;
    }

    @Override
    public List<Matricula> listar(Map<String, String> filtros) throws Exception {
        return dao.consultarTodos(filtros.getOrDefault("situacao", ""));
    }

    @Override
    public Matricula buscar(int id) throws Exception {
        return dao.consultarById(comId(id));
    }

    @Override
    public int criar(Matricula matricula) throws Exception {
        return dao.cadastrar(matricula);
    }

    @Override
    public void atualizar(Matricula matricula) throws Exception {
        dao.atualizar(matricula);
    }

    @Override
    public void excluir(int id) throws Exception {
        dao.deletar(comId(id));
    }

    private static Matricula comId(int id) {
        return Matricula.getBuilder().comId(id).constroi();
    }
}
