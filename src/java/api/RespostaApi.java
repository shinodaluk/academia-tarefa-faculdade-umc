package api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Resposta HTTP: status, cabeçalhos e corpo a serializar em JSON (null = sem corpo). */
public record RespostaApi(int status, Map<String, String> cabecalhos, Object corpo) {

    public static RespostaApi ok(Object corpo) {
        return new RespostaApi(200, Map.of(), corpo);
    }

    public static RespostaApi criado(String localizacao, Object corpo) {
        return new RespostaApi(201, Map.of("Location", localizacao), corpo);
    }

    public static RespostaApi semConteudo() {
        return new RespostaApi(204, Map.of(), null);
    }

    public static RespostaApi erro(int status, String mensagem) {
        return erro(status, mensagem, List.of(), Map.of());
    }

    public static RespostaApi erro(int status, String mensagem, List<String> detalhes, Map<String, String> cabecalhos) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("error", mensagem);
        if (!detalhes.isEmpty()) {
            corpo.put("detalhes", detalhes);
        }
        return new RespostaApi(status, cabecalhos, corpo);
    }
}
