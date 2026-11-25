package apoio;

import dao.RegistroNaoEncontradoException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import repositorio.Repositorio;

/** Repositório em memória que registra as chamadas e permite simular falhas. */
public class RepositorioFalso<T> implements Repositorio<T> {

    private final Map<Integer, T> itens = new LinkedHashMap<>();
    private int proximoId = 100;
    public Map<String, String> ultimosFiltros;
    public T ultimoCriado;
    public T ultimoAtualizado;
    public final List<Integer> excluidos = new ArrayList<>();
    public Exception falha;

    public RepositorioFalso<T> com(int id, T item) {
        itens.put(id, item);
        return this;
    }

    @Override
    public List<T> listar(Map<String, String> filtros) throws Exception {
        ultimosFiltros = filtros;
        falharSeConfigurado();
        return new ArrayList<>(itens.values());
    }

    @Override
    public T buscar(int id) throws Exception {
        falharSeConfigurado();
        T item = itens.get(id);
        if (item == null) {
            throw new RegistroNaoEncontradoException("Registro não encontrado");
        }
        return item;
    }

    @Override
    public int criar(T entidade) throws Exception {
        falharSeConfigurado();
        ultimoCriado = entidade;
        int id = proximoId++;
        itens.put(id, entidade);
        return id;
    }

    @Override
    public void atualizar(T entidade) throws Exception {
        falharSeConfigurado();
        ultimoAtualizado = entidade;
    }

    @Override
    public void excluir(int id) throws Exception {
        falharSeConfigurado();
        excluidos.add(id);
        itens.remove(id);
    }

    private void falharSeConfigurado() throws Exception {
        if (falha != null) {
            throw falha;
        }
    }
}
