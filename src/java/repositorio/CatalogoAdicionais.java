package repositorio;

import java.util.List;
import model.ServicoAdicional;

/** Serviços adicionais cadastrados (preço e descrição de cada decorator). */
public interface CatalogoAdicionais {

    List<ServicoAdicional> todos() throws Exception;

    /** Lança {@link dao.RegistroNaoEncontradoException} se o código não estiver cadastrado. */
    ServicoAdicional porCodigo(String codigo) throws Exception;
}
