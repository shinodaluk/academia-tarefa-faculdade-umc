package model;

import java.math.BigDecimal;

public class ServicoAdicional {
    private final String codigo;
    private final String descricao;
    private final BigDecimal valorMensal;

    public ServicoAdicional(String codigo, String descricao, BigDecimal valorMensal) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.valorMensal = valorMensal;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getValorMensal() {
        return valorMensal;
    }
}
