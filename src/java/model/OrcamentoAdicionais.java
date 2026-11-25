package model;

import java.util.List;

/** Valor mensal da matrícula com os adicionais aplicados (resposta da tela e da API). */
public record OrcamentoAdicionais(
        int matriculaId,
        String descricaoBase,
        Dinheiro custoBase,
        List<ServicoAdicional> adicionais,
        String descricao,
        Dinheiro totalMensal) {

    public static OrcamentoAdicionais calcular(Matricula matricula, List<ServicoAdicional> adicionais) {
        Servico composto = matricula;
        for (ServicoAdicional adicional : adicionais) {
            composto = MontadorAdicionais.decorar(composto, adicional);
        }
        return new OrcamentoAdicionais(matricula.getId(), matricula.getDescricao(), matricula.custo(),
                List.copyOf(adicionais), composto.getDescricao(), composto.custo());
    }
}
