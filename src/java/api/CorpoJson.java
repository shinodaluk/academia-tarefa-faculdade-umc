package api;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.util.List;
import java.util.function.Consumer;

/** Converte o corpo JSON em entidade e valida; qualquer problema vira 400. */
final class CorpoJson {

    private CorpoJson() {
    }

    /** O ajuste roda sobre o JSON antes da conversão (ex.: impor o id da URL). */
    static <T> T ler(Gson gson, String corpo, Class<T> tipo, Consumer<JsonObject> ajuste, Validador<T> validador) {
        if (corpo.isBlank()) {
            throw ErroApi.requisicaoInvalida("O corpo da requisição deve ser um objeto JSON.");
        }
        T entidade;
        try {
            JsonElement elemento = JsonParser.parseString(corpo);
            if (!elemento.isJsonObject()) {
                throw ErroApi.requisicaoInvalida("O corpo da requisição deve ser um objeto JSON.");
            }
            JsonObject json = elemento.getAsJsonObject();
            ajuste.accept(json);
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
}
