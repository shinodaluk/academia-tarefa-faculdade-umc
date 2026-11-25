package model;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

/** Conversão estrita entre rótulo (texto) e enum ComRotulo, usada pelos builders, DAOs e Gson. */
public final class Rotulos {

    private Rotulos() {
    }

    public static <E extends Enum<E> & ComRotulo> E de(Class<E> tipo, String rotulo) {
        if (rotulo == null) {
            return null;
        }
        for (E valor : tipo.getEnumConstants()) {
            if (valor.rotulo().equals(rotulo)) {
                return valor;
            }
        }
        throw new IllegalArgumentException("Valor '" + rotulo + "' inválido; use um de " + rotulos(tipo) + ".");
    }

    public static String rotulos(Class<? extends ComRotulo> tipo) {
        return Arrays.stream(tipo.getEnumConstants()).map(ComRotulo::rotulo)
                .collect(Collectors.joining(", ", "[", "]"));
    }

    /** O adaptador padrão do Gson transformaria rótulo desconhecido em null sem avisar. */
    public static class AdaptadorJson implements TypeAdapterFactory {

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> tipo) {
            Class raw = tipo.getRawType();
            return (TypeAdapter<T>) new TypeAdapter<Object>() {
                @Override
                public void write(JsonWriter out, Object valor) throws IOException {
                    if (valor == null) {
                        out.nullValue();
                        return;
                    }
                    out.value(((ComRotulo) valor).rotulo());
                }

                @Override
                public Object read(JsonReader in) throws IOException {
                    if (in.peek() == JsonToken.NULL) {
                        in.nextNull();
                        return null;
                    }
                    return de(raw, in.nextString());
                }
            };
        }
    }
}
