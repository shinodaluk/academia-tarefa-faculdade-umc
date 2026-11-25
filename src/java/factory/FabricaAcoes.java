package factory;

import java.util.regex.Pattern;

/**
 * Factory das ações das telas: "plano" + "listar" vira factory.plano.ListarAction.
 * Ação nova = classe nova no pacote, sem alterar a fábrica nem o controller.
 */
public final class FabricaAcoes {

    // só letras: impede montar nomes de classe de outros pacotes
    private static final Pattern NOME_VALIDO = Pattern.compile("[a-zA-Z]+");

    private FabricaAcoes() {
    }

    public static ICommand criar(String recurso, String acao) throws AcaoNaoEncontradaException {
        if (acao == null || !NOME_VALIDO.matcher(acao).matches()) {
            throw new AcaoNaoEncontradaException("Ação inválida.");
        }
        String nomeDaClasse = "factory." + recurso + "."
                + Character.toUpperCase(acao.charAt(0)) + acao.substring(1) + "Action";
        try {
            Class<?> classe = Class.forName(nomeDaClasse);
            if (!ICommand.class.isAssignableFrom(classe)) {
                throw new AcaoNaoEncontradaException("Ação não encontrada: " + acao);
            }
            return (ICommand) classe.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException ex) {
            throw new AcaoNaoEncontradaException("Ação não encontrada: " + acao);
        }
    }
}
