package api;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * CRUD REST genérico de um recurso:
 * GET/POST na coleção, GET/PUT/DELETE no item e sub-recursos em /{id}/nome.
 */
public class ControladorRest<T> implements RecursoApi {

    private final Class<T> tipo;
    private final Repositorio<T> repositorio;
    private final Validador<T> validador;
    private final Gson gson;
    private final Map<String, RecursoApi> subrecursos = new HashMap<>();
    private Consumer<JsonObject> normalizador = json -> { };

    public ControladorRest(Class<T> tipo, Repositorio<T> repositorio, Validador<T> validador, Gson gson) {
        this.tipo = tipo;
        this.repositorio = repositorio;
        this.validador = validador;
        this.gson = gson;
    }

    public ControladorRest<T> comSubrecurso(String nome, RecursoApi subrecurso) {
        subrecursos.put(nome, subrecurso);
        return this;
    }

    /** Ajusta o JSON de entrada antes da conversão (ex.: valores padrão). */
    public ControladorRest<T> comNormalizador(Consumer<JsonObject> normalizador) {
        this.normalizador = normalizador;
        return this;
    }

    @Override
    public RespostaApi tratar(RequisicaoApi requisicao) throws Exception {
        List<String> segmentos = requisicao.segmentos();
        if (segmentos.isEmpty()) {
            return tratarColecao(requisicao);
        }
        int id = lerId(segmentos.get(0));
        if (segmentos.size() == 1) {
            return tratarItem(requisicao, id);
        }
        RecursoApi subrecurso = subrecursos.get(segmentos.get(1));
        if (subrecurso == null || segmentos.size() > 2) {
            throw ErroApi.naoEncontrado("Recurso não encontrado.");
        }
        return subrecurso.tratar(requisicao);
    }

    private RespostaApi tratarColecao(RequisicaoApi requisicao) throws Exception {
        switch (requisicao.metodo()) {
            case "GET":
                return RespostaApi.ok(repositorio.listar(requisicao.parametros()));
            case "POST":
                return criar(requisicao);
            default:
                throw ErroApi.metodoNaoPermitido("GET, POST");
        }
    }

    private RespostaApi tratarItem(RequisicaoApi requisicao, int id) throws Exception {
        switch (requisicao.metodo()) {
            case "GET":
                return RespostaApi.ok(repositorio.buscar(id));
            case "PUT":
                return atualizar(requisicao, id);
            case "DELETE":
                repositorio.buscar(id);
                repositorio.excluir(id);
                return RespostaApi.semConteudo();
            default:
                throw ErroApi.metodoNaoPermitido("GET, PUT, DELETE");
        }
    }

    private RespostaApi criar(RequisicaoApi requisicao) throws Exception {
        T entidade = lerCorpo(requisicao, null);
        int id = repositorio.criar(entidade);
        return RespostaApi.criado(requisicao.urlRecurso() + "/" + id, repositorio.buscar(id));
    }

    private RespostaApi atualizar(RequisicaoApi requisicao, int id) throws Exception {
        repositorio.buscar(id);
        repositorio.atualizar(lerCorpo(requisicao, id));
        return RespostaApi.ok(repositorio.buscar(id));
    }

    // o id vem sempre da URL; o do corpo é descartado
    private T lerCorpo(RequisicaoApi requisicao, Integer id) {
        if (requisicao.corpo().isBlank()) {
            throw ErroApi.requisicaoInvalida("O corpo da requisição deve ser um objeto JSON.");
        }
        T entidade;
        try {
            JsonElement elemento = JsonParser.parseString(requisicao.corpo());
            if (!elemento.isJsonObject()) {
                throw ErroApi.requisicaoInvalida("O corpo da requisição deve ser um objeto JSON.");
            }
            JsonObject json = elemento.getAsJsonObject();
            json.remove("id");
            if (id != null) {
                json.addProperty("id", id);
            }
            normalizador.accept(json);
            entidade = gson.fromJson(json, tipo);
        } catch (JsonParseException | NumberFormatException ex) {
            throw ErroApi.requisicaoInvalida("JSON inválido ou com tipo de campo incorreto.");
        } catch (IllegalArgumentException ex) {
            // objeto de valor recusou o campo (ex.: Dinheiro negativo, CPF fora do formato)
            throw ErroApi.dadosInvalidos(List.of(ex.getMessage()));
        }

        List<String> erros = validador.validar(entidade);
        if (!erros.isEmpty()) {
            throw ErroApi.dadosInvalidos(erros);
        }
        return entidade;
    }

    static int lerId(String segmento) {
        try {
            return Integer.parseInt(segmento);
        } catch (NumberFormatException ex) {
            throw ErroApi.requisicaoInvalida("Id inválido: " + segmento);
        }
    }
}
