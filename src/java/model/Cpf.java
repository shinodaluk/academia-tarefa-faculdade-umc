package model;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * CPF com formato garantido (11 dígitos, guardado com máscara).
 * Os dígitos verificadores são conferidos à parte (na entrada da API), para não impedir a leitura
 * de registros antigos gravados sem essa checagem.
 */
@JsonAdapter(Cpf.AdaptadorJson.class)
public final class Cpf {

    private static final Pattern FORMATO = Pattern.compile("\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");

    private final String digitos;

    private Cpf(String digitos) {
        this.digitos = digitos;
    }

    public static Cpf de(String texto) {
        Objects.requireNonNull(texto, "O CPF é obrigatório.");
        String limpo = texto.trim();
        if (!FORMATO.matcher(limpo).matches()) {
            throw new IllegalArgumentException("CPF deve ter 11 dígitos, com ou sem máscara: " + texto);
        }
        return new Cpf(limpo.replaceAll("\\D", ""));
    }

    public boolean digitosVerificadoresValidos() {
        if (digitos.chars().distinct().count() == 1) {
            return false;
        }
        return digitoVerificador(9) == digito(9) && digitoVerificador(10) == digito(10);
    }

    private int digitoVerificador(int posicao) {
        int soma = 0;
        for (int i = 0; i < posicao; i++) {
            soma += digito(i) * (posicao + 1 - i);
        }
        return soma * 10 % 11 % 10;
    }

    private int digito(int posicao) {
        return digitos.charAt(posicao) - '0';
    }

    @Override
    public boolean equals(Object outro) {
        return outro instanceof Cpf && digitos.equals(((Cpf) outro).digitos);
    }

    @Override
    public int hashCode() {
        return digitos.hashCode();
    }

    /** Formato do banco e das telas: 000.000.000-00. */
    @Override
    public String toString() {
        return digitos.substring(0, 3) + "." + digitos.substring(3, 6) + "."
                + digitos.substring(6, 9) + "-" + digitos.substring(9);
    }

    public static class AdaptadorJson extends TypeAdapter<Cpf> {

        @Override
        public void write(JsonWriter out, Cpf cpf) throws IOException {
            if (cpf == null) {
                out.nullValue();
                return;
            }
            out.value(cpf.toString());
        }

        @Override
        public Cpf read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return Cpf.de(in.nextString());
        }
    }
}
