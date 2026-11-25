package api;

import dao.RegistroNaoEncontradoException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.Plano;

/** Repositório fake: guarda planos em memória para testar a API sem banco. */
class PlanosEmMemoria implements Repositorio<Plano> {

    private final Map<Integer, Plano> planos = new LinkedHashMap<>();
    private int proximoId = 1;
    Exception falhaAoExcluir;
    Map<String, String> ultimosFiltros;

    Plano adicionar(String nome, int duracaoMeses, double valorMensal) {
        Plano plano = Plano.getBuilder()
                .comId(proximoId++)
                .comNome(nome)
                .comDuracaoMeses(duracaoMeses)
                .comValorMensal(valorMensal)
                .doAtivo()
                .constroi();
        planos.put(plano.getId(), plano);
        return plano;
    }

    boolean existe(int id) {
        return planos.containsKey(id);
    }

    @Override
    public List<Plano> listar(Map<String, String> filtros) {
        ultimosFiltros = filtros;
        return new ArrayList<>(planos.values());
    }

    @Override
    public Plano buscar(int id) throws RegistroNaoEncontradoException {
        Plano plano = planos.get(id);
        if (plano == null) {
            throw new RegistroNaoEncontradoException("Plano não encontrado");
        }
        return plano;
    }

    @Override
    public int criar(Plano plano) {
        int id = proximoId++;
        planos.put(id, copiarComId(plano, id));
        return id;
    }

    @Override
    public void atualizar(Plano plano) {
        planos.put(plano.getId(), plano);
    }

    @Override
    public void excluir(int id) throws Exception {
        if (falhaAoExcluir != null) {
            throw falhaAoExcluir;
        }
        planos.remove(id);
    }

    private static Plano copiarComId(Plano plano, int id) {
        return Plano.getBuilder()
                .comId(id)
                .comNome(plano.getNome())
                .comDuracaoMeses(plano.getDuracaoMeses())
                .comValorMensal(plano.getValorMensal())
                .comDescricao(plano.getDescricao())
                .comAtivo(plano.isAtivo())
                .constroi();
    }
}
