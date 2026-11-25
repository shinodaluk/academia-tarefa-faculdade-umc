package api;

import java.util.List;
import java.util.Map;

/** Erro de requisição com status HTTP definido; o roteador o converte em resposta. */
public class ErroApi extends RuntimeException {

    private final int status;
    private final List<String> detalhes;
    private final Map<String, String> cabecalhos;

    private ErroApi(int status, String mensagem, List<String> detalhes, Map<String, String> cabecalhos) {
        super(mensagem);
        this.status = status;
        this.detalhes = detalhes;
        this.cabecalhos = cabecalhos;
    }

    public static ErroApi requisicaoInvalida(String mensagem) {
        return new ErroApi(400, mensagem, List.of(), Map.of());
    }

    public static ErroApi dadosInvalidos(List<String> detalhes) {
        return new ErroApi(400, "Dados inválidos.", List.copyOf(detalhes), Map.of());
    }

    public static ErroApi naoEncontrado(String mensagem) {
        return new ErroApi(404, mensagem, List.of(), Map.of());
    }

    public static ErroApi metodoNaoPermitido(String permitidos) {
        return new ErroApi(405, "Método não permitido. Use: " + permitidos + ".", List.of(), Map.of("Allow", permitidos));
    }

    public RespostaApi comoResposta() {
        return RespostaApi.erro(status, getMessage(), detalhes, cabecalhos);
    }
}
