package repositorio.jdbc;

import dao.ServicoAdicionalDao;
import java.util.List;
import model.ServicoAdicional;
import repositorio.CatalogoAdicionais;

/** Adapta o ServicoAdicionalDao à interface CatalogoAdicionais. */
public class CatalogoAdicionaisJdbc implements CatalogoAdicionais {

    private final ServicoAdicionalDao dao;

    public CatalogoAdicionaisJdbc(ServicoAdicionalDao dao) {
        this.dao = dao;
    }

    @Override
    public List<ServicoAdicional> todos() throws Exception {
        return dao.consultarTodos();
    }

    @Override
    public ServicoAdicional porCodigo(String codigo) throws Exception {
        return dao.consultarPorCodigo(codigo);
    }
}
