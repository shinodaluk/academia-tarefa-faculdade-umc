package repositorio;

import java.util.HashMap;
import java.util.Map;

/** Filtros de listagem (query string ou formulário) no formato aceito por Repositorio.listar. */
public final class Filtros {

    private Filtros() {
    }

    /** Pares chave/valor; valor nulo (parâmetro ausente) é ignorado. */
    public static Map<String, String> de(String... chavesEValores) {
        Map<String, String> filtros = new HashMap<>();
        for (int i = 0; i + 1 < chavesEValores.length; i += 2) {
            if (chavesEValores[i + 1] != null) {
                filtros.put(chavesEValores[i], chavesEValores[i + 1]);
            }
        }
        return filtros;
    }

    /** Vazio quando ausente: o SQL dos DAOs usa '' para "sem filtro". */
    public static String valor(Map<String, String> filtros, String chave) {
        String valor = filtros.get(chave);
        return valor == null ? "" : valor;
    }

    /** 0 quando ausente (sem filtro); texto não numérico lança NumberFormatException (400 na API). */
    public static int inteiro(Map<String, String> filtros, String chave) {
        String valor = valor(filtros, chave).trim();
        return valor.isEmpty() ? 0 : Integer.parseInt(valor);
    }
}
