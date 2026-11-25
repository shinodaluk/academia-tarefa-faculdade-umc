package dao;

/** Consulta por id que não encontrou registro. */
public class RegistroNaoEncontradoException extends Exception {

    public RegistroNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
