package factory;

/** O parâmetro acao não corresponde a nenhum ICommand do recurso. */
public class AcaoNaoEncontradaException extends Exception {

    public AcaoNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
