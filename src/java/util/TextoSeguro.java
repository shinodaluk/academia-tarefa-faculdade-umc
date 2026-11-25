package util;

import com.google.gson.Gson;

/** Texto livre (ex.: parâmetro da URL) pronto para entrar entre aspas dentro de um script na JSP. */
public final class TextoSeguro {

    // Gson escapa < > & = ' como <..., então o texto não fecha a tag <script>
    private static final Gson GSON = new Gson();

    private TextoSeguro() {
    }

    public static String paraScript(String texto) {
        if (texto == null) {
            return "";
        }
        String json = GSON.toJson(texto);
        return json.substring(1, json.length() - 1);
    }
}
