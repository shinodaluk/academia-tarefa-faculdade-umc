package repositorio;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Decorator de Repositorio: toda entidade lida sai com os relacionamentos vinculados
 * (lazy loading). Gravações passam direto para o repositório decorado.
 */
public class RepositorioComRelacionamentos<T> implements Repositorio<T> {

    private final Repositorio<T> repositorio;
    private final Consumer<T> vinculador;

    public RepositorioComRelacionamentos(Repositorio<T> repositorio, Consumer<T> vinculador) {
        this.repositorio = repositorio;
        this.vinculador = vinculador;
    }

    @Override
    public List<T> listar(Map<String, String> filtros) throws Exception {
        List<T> entidades = repositorio.listar(filtros);
        entidades.forEach(vinculador);
        return entidades;
    }

    @Override
    public T buscar(int id) throws Exception {
        T entidade = repositorio.buscar(id);
        vinculador.accept(entidade);
        return entidade;
    }

    @Override
    public int criar(T entidade) throws Exception {
        return repositorio.criar(entidade);
    }

    @Override
    public void atualizar(T entidade) throws Exception {
        repositorio.atualizar(entidade);
    }

    @Override
    public void excluir(int id) throws Exception {
        repositorio.excluir(id);
    }
}
