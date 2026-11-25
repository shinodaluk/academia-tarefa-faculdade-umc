package model;

import com.google.gson.annotations.JsonAdapter;

/** Objetivo da ficha de treino (ENUM objetivo do banco). */
@JsonAdapter(Rotulos.AdaptadorJson.class)
public enum ObjetivoTreino implements ComRotulo {
    HIPERTROFIA("Hipertrofia"),
    EMAGRECIMENTO("Emagrecimento"),
    CONDICIONAMENTO("Condicionamento");

    private final String rotulo;

    ObjetivoTreino(String rotulo) {
        this.rotulo = rotulo;
    }

    @Override
    public String rotulo() {
        return rotulo;
    }
}
