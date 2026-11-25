package api.repositorio;

import api.Repositorio;
import dao.InstrutorDao;
import java.util.List;
import java.util.Map;
import model.Instrutor;

/** Adapta o InstrutorDao à interface usada pela API. Filtros: ?especialidade=&status= */
public class InstrutorRepositorioJdbc implements Repositorio<Instrutor> {

    private final InstrutorDao dao;

    public InstrutorRepositorioJdbc(InstrutorDao dao) {
        this.dao = dao;
    }

    @Override
    public List<Instrutor> listar(Map<String, String> filtros) throws Exception {
        return dao.consultarTodos(filtros.getOrDefault("especialidade", ""), filtros.getOrDefault("status", ""));
    }

    @Override
    public Instrutor buscar(int id) throws Exception {
        return dao.consultarById(comId(id));
    }

    @Override
    public int criar(Instrutor instrutor) throws Exception {
        return dao.cadastrar(instrutor);
    }

    @Override
    public void atualizar(Instrutor instrutor) throws Exception {
        dao.atualizar(instrutor);
    }

    @Override
    public void excluir(int id) throws Exception {
        dao.deletar(comId(id));
    }

    private static Instrutor comId(int id) {
        return Instrutor.getBuilder().comId(id).constroi();
    }
}
