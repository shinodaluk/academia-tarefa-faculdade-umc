package api;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Requisição HTTP já desmontada, independente da Servlet API. */
public record RequisicaoApi(
        String metodo,
        String recurso,
        List<String> segmentos,
        Map<String, String> parametros,
        String corpo,
        String urlRecurso) {

    /** Monta a partir do pathInfo do servlet, ex.: "/matriculas/7/adicionais". */
    public static RequisicaoApi de(String metodo, String pathInfo, Map<String, String> parametros,
            String corpo, String urlBase) {
        List<String> partes = new ArrayList<>();
        for (String parte : (pathInfo == null ? "" : pathInfo).split("/")) {
            if (!parte.isEmpty()) {
                partes.add(parte);
            }
        }
        String recurso = partes.isEmpty() ? "" : partes.remove(0);
        return new RequisicaoApi(
                metodo.toUpperCase(Locale.ROOT),
                recurso,
                List.copyOf(partes),
                Map.copyOf(parametros),
                corpo == null ? "" : corpo,
                urlBase + "/" + recurso);
    }
}
