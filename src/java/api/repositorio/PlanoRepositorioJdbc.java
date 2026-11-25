package api.repositorio;

import api.Repositorio;
import dao.PlanoDao;
import java.util.List;
import java.util.Map;
import model.Plano;

/** Adapta o PlanoDao à interface usada pela API. Filtros: ?apenasAtivos=true */
public class PlanoRepositorioJdbc implements Repositorio<Plano> {

    private final PlanoDao dao;

    public PlanoRepositorioJdbc(PlanoDao dao) {
        this.dao = dao;
    }

    @Override
    public List<Plano> listar(Map<String, String> filtros) throws Exception {
        return dao.consultarTodos("true".equalsIgnoreCase(filtros.get("apenasAtivos")));
    }

    @Override
    public Plano buscar(int id) throws Exception {
        return dao.consultarById(comId(id));
    }

    @Override
    public int criar(Plano plano) throws Exception {
        return dao.cadastrar(plano);
    }

    @Override
    public void atualizar(Plano plano) throws Exception {
        dao.atualizar(plano);
    }

    @Override
    public void excluir(int id) throws Exception {
        dao.deletar(comId(id));
    }

    private static Plano comId(int id) {
        return Plano.getBuilder().comId(id).constroi();
    }
}
