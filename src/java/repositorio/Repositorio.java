package repositorio;

import java.util.List;
import java.util.Map;

/** Persistência usada pela API e pelas telas; a implementação JDBC fica em repositorio.jdbc. */
public interface Repositorio<T> {

    List<T> listar(Map<String, String> filtros) throws Exception;

    /** Lança {@link dao.RegistroNaoEncontradoException} se o id não existir. */
    T buscar(int id) throws Exception;

    int criar(T entidade) throws Exception;

    void atualizar(T entidade) throws Exception;

    void excluir(int id) throws Exception;
}
