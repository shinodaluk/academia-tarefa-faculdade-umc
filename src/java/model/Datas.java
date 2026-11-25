package model;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Date;

/** Datas no formato ISO (yyyy-MM-dd) usado pelos formulários, pela API e pelo banco. */
public final class Datas {

    private Datas() {
    }

    /** LocalDate.parse é estrito: 2024-02-30 é erro, não 1º de março. */
    public static Date deIso(String texto) {
        if (texto == null) {
            return null;
        }
        try {
            return Date.from(LocalDate.parse(texto.trim()).atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Data inválida (use yyyy-MM-dd): " + texto);
        }
    }
}
