package api;

import java.util.List;

/** Regras de entrada de um recurso; lista vazia = válido. */
@FunctionalInterface
public interface Validador<T> {

    List<String> validar(T entidade);
}
