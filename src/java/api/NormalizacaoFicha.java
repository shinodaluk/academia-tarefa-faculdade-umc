package api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.time.LocalDate;
import java.util.function.Consumer;

/** Valores padrão da ficha, iguais aos do formulário: data de hoje e ordem pela posição. */
public class NormalizacaoFicha implements Consumer<JsonObject> {

    @Override
    public void accept(JsonObject ficha) {
        if (!ficha.has("dataCriacao") || ficha.get("dataCriacao").isJsonNull()) {
            ficha.addProperty("dataCriacao", LocalDate.now().toString());
        }
        if (!ficha.has("exercicios") || ficha.get("exercicios").isJsonNull()) {
            ficha.add("exercicios", new JsonArray());
        }
        JsonElement exercicios = ficha.get("exercicios");
        if (!exercicios.isJsonArray()) {
            return;
        }
        int ordem = 1;
        for (JsonElement exercicio : exercicios.getAsJsonArray()) {
            if (exercicio.isJsonObject()) {
                exercicio.getAsJsonObject().addProperty("ordem", ordem);
            }
            ordem++;
        }
    }
}
