package model;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** Valor monetário em reais: nunca negativo, sempre em centavos exatos. */
@JsonAdapter(Dinheiro.AdaptadorJson.class)
public final class Dinheiro implements Comparable<Dinheiro> {

    public static final Dinheiro ZERO = new Dinheiro(BigDecimal.ZERO);

    private final BigDecimal valor;

    private Dinheiro(BigDecimal valor) {
        Objects.requireNonNull(valor, "O valor é obrigatório.");
        if (valor.signum() < 0) {
            throw new IllegalArgumentException("Valor monetário não pode ser negativo: " + valor.toPlainString());
        }
        // HALF_UP = mesmo arredondamento do ROUND(x, 2) do MySQL em DECIMAL
        this.valor = valor.setScale(2, RoundingMode.HALF_UP);
    }

    public static Dinheiro de(BigDecimal valor) {
        return new Dinheiro(valor);
    }

    public static Dinheiro de(String valor) {
        Objects.requireNonNull(valor, "O valor é obrigatório.");
        try {
            return new Dinheiro(new BigDecimal(valor.trim()));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Valor monetário inválido: " + valor);
        }
    }

    public Dinheiro somar(Dinheiro outro) {
        return new Dinheiro(valor.add(outro.valor));
    }

    public BigDecimal valor() {
        return valor;
    }

    @Override
    public int compareTo(Dinheiro outro) {
        return valor.compareTo(outro.valor);
    }

    @Override
    public boolean equals(Object outro) {
        return outro instanceof Dinheiro && valor.equals(((Dinheiro) outro).valor);
    }

    @Override
    public int hashCode() {
        return valor.hashCode();
    }

    @Override
    public String toString() {
        return valor.toPlainString();
    }

    /** JSON como número (129.90), formato que as telas já leem. */
    public static class AdaptadorJson extends TypeAdapter<Dinheiro> {

        @Override
        public void write(JsonWriter out, Dinheiro dinheiro) throws IOException {
            if (dinheiro == null) {
                out.nullValue();
                return;
            }
            out.value(dinheiro.valor);
        }

        @Override
        public Dinheiro read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return Dinheiro.de(in.nextString());
        }
    }
}
