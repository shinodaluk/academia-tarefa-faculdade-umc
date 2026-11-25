package model;

/** Lazy loading: o valor só é buscado no primeiro obter() e depois fica guardado. */
public final class Lazy<T> {

    /** Consulta que produz o valor; pode lançar a exceção do banco. */
    @FunctionalInterface
    public interface Carregador<T> {
        T carregar() throws Exception;
    }

    private final Carregador<T> carregador;
    private T valor;
    private boolean carregado;

    private Lazy(Carregador<T> carregador, T valor, boolean carregado) {
        this.carregador = carregador;
        this.valor = valor;
        this.carregado = carregado;
    }

    public static <T> Lazy<T> de(Carregador<T> carregador) {
        return new Lazy<>(carregador, null, false);
    }

    public static <T> Lazy<T> pronto(T valor) {
        return new Lazy<>(null, valor, true);
    }

    public synchronized T obter() {
        if (!carregado) {
            try {
                valor = carregador.carregar();
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                // getter da entidade não declara exceção; a falha não fica em cache
                throw new IllegalStateException("Falha ao carregar relacionamento.", ex);
            }
            carregado = true;
        }
        return valor;
    }

    public synchronized boolean carregado() {
        return carregado;
    }
}
