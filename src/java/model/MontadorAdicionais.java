package model;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Escolhe o decorator de cada código de serviço adicional.
 * Adicional novo = nova subclasse de AdicionaisMatricula + uma entrada no mapa.
 */
public final class MontadorAdicionais {

    private static final Map<String, BiFunction<Servico, ServicoAdicional, Servico>> DECORATORS = Map.of(
            "BIOIMPEDANCIA", Bioimpedancia::new,
            "CADEIRA_MASSAGEM", CadeiraMassagem::new,
            "PRE_TREINO", PreTreino::new);

    private MontadorAdicionais() {
    }

    public static boolean reconhece(String codigo) {
        // Map.of lança NullPointerException em containsKey(null)
        return codigo != null && DECORATORS.containsKey(codigo);
    }

    public static List<String> codigos() {
        return DECORATORS.keySet().stream().sorted().toList();
    }

    public static Servico decorar(Servico base, ServicoAdicional adicional) {
        if (!reconhece(adicional.getCodigo())) {
            throw new IllegalArgumentException("Código de adicional não reconhecido: " + adicional.getCodigo());
        }
        return DECORATORS.get(adicional.getCodigo()).apply(base, adicional);
    }
}
