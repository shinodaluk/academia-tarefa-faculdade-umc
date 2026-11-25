package model;

import java.util.Set;

/** Escolhe o decorator de cada código de serviço adicional. */
public final class MontadorAdicionais {

    private static final Set<String> CODIGOS = Set.of("BIOIMPEDANCIA", "CADEIRA_MASSAGEM", "PRE_TREINO");

    private MontadorAdicionais() {
    }

    public static boolean reconhece(String codigo) {
        return CODIGOS.contains(codigo);
    }

    public static Servico decorar(Servico base, ServicoAdicional adicional) {
        switch (adicional.getCodigo()) {
            case "BIOIMPEDANCIA":
                return new Bioimpedancia(base, adicional);
            case "CADEIRA_MASSAGEM":
                return new CadeiraMassagem(base, adicional);
            case "PRE_TREINO":
                return new PreTreino(base, adicional);
            default:
                throw new IllegalArgumentException("Código de adicional não reconhecido: " + adicional.getCodigo());
        }
    }
}
