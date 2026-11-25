package repositorio.jdbc;

import repositorio.Filtros;
import repositorio.Repositorio;
import dao.FichaTreinoDao;
import java.util.List;
import java.util.Map;
import model.FichaTreino;

/** Adapta o FichaTreinoDao à interface Repositorio (API e telas). Filtros: ?objetivo=&alunoId=&instrutorId= */
public class FichaTreinoRepositorioJdbc implements Repositorio<FichaTreino> {

    private final FichaTreinoDao dao;

    public FichaTreinoRepositorioJdbc(FichaTreinoDao dao) {
        this.dao = dao;
    }

    @Override
    public List<FichaTreino> listar(Map<String, String> filtros) throws Exception {
        return dao.consultarTodos(Filtros.valor(filtros, "objetivo"),
                Filtros.inteiro(filtros, "alunoId"), Filtros.inteiro(filtros, "instrutorId"));
    }

    @Override
    public FichaTreino buscar(int id) throws Exception {
        return dao.consultarById(comId(id));
    }

    @Override
    public int criar(FichaTreino ficha) throws Exception {
        return dao.cadastrar(ficha);
    }

    @Override
    public void atualizar(FichaTreino ficha) throws Exception {
        dao.atualizar(ficha);
    }

    @Override
    public void excluir(int id) throws Exception {
        dao.deletar(comId(id));
    }

    private static FichaTreino comId(int id) {
        return FichaTreino.getBuilder().comId(id).constroi();
    }
}
