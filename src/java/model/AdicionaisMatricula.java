package model;

import java.math.BigDecimal;
import java.util.Objects;

/** Decorator abstrato: acrescenta um serviço adicional ao serviço decorado. */
public abstract class AdicionaisMatricula implements Servico {

    protected final Servico servicodecorator;
    private final ServicoAdicional adicional;

    protected AdicionaisMatricula(Servico servicodecorator, ServicoAdicional adicional, String codigoEsperado) {
        this.servicodecorator = Objects.requireNonNull(servicodecorator, "O serviço base é obrigatório.");
        this.adicional = Objects.requireNonNull(adicional, "O serviço adicional é obrigatório.");
        if (!codigoEsperado.equals(adicional.getCodigo())) {
            throw new IllegalArgumentException(
                    "Adicional '" + adicional.getCodigo() + "' não corresponde a " + codigoEsperado + ".");
        }
    }

    protected final String descricaoAdicional() {
        return adicional.getDescricao();
    }

    protected final double custoComAdicional() {
        return BigDecimal.valueOf(servicodecorator.custo())
                .add(adicional.getValorMensal())
                .doubleValue();
    }
}
