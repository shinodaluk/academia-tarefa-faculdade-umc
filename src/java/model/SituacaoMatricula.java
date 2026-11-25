package model;

import com.google.gson.annotations.JsonAdapter;

/** Situação da matrícula (ENUM situacao do banco). */
@JsonAdapter(Rotulos.AdaptadorJson.class)
public enum SituacaoMatricula implements ComRotulo {
    ATIVA("Ativa"),
    CANCELADA("Cancelada"),
    SUSPENSA("Suspensa");

    private final String rotulo;

    SituacaoMatricula(String rotulo) {
        this.rotulo = rotulo;
    }

    @Override
    public String rotulo() {
        return rotulo;
    }
}
