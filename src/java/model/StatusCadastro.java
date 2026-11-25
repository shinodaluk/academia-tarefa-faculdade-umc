package model;

import com.google.gson.annotations.JsonAdapter;

/** Situação cadastral de aluno e instrutor (ENUM status do banco). */
@JsonAdapter(Rotulos.AdaptadorJson.class)
public enum StatusCadastro implements ComRotulo {
    ATIVO("Ativo"),
    INATIVO("Inativo");

    private final String rotulo;

    StatusCadastro(String rotulo) {
        this.rotulo = rotulo;
    }

    @Override
    public String rotulo() {
        return rotulo;
    }
}
