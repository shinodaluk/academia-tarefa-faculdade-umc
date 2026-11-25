package model;

public class ServicoAdicional {
    private final String codigo;
    private final String descricao;
    private final Dinheiro valorMensal;

    public ServicoAdicional(String codigo, String descricao, Dinheiro valorMensal) {
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

    public Dinheiro getValorMensal() {
        return valorMensal;
    }
}
