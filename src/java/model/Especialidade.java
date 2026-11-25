package model;

import com.google.gson.annotations.JsonAdapter;

/** Especialidade do instrutor (ENUM especialidade do banco). */
@JsonAdapter(Rotulos.AdaptadorJson.class)
public enum Especialidade implements ComRotulo {
    MUSCULACAO("Musculação"),
    FUNCIONAL("Funcional"),
    CONDICIONAMENTO("Condicionamento"),
    PILATES("Pilates");

    private final String rotulo;

    Especialidade(String rotulo) {
        this.rotulo = rotulo;
    }

    @Override
    public String rotulo() {
        return rotulo;
    }
}
