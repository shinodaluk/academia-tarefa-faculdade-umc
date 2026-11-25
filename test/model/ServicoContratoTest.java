package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Princípio de Substituição de Liskov: o mesmo contrato de Servico vale para a
 * Matricula e para qualquer decorator, sozinho ou empilhado.
 */
class ServicoContratoTest {

    private static final ServicoAdicional BIOIMPEDANCIA =
            new ServicoAdicional("BIOIMPEDANCIA", "Bioimpedância", Dinheiro.de("20.00"));
    private static final ServicoAdicional CADEIRA_MASSAGEM =
            new ServicoAdicional("CADEIRA_MASSAGEM", "Cadeira de Massagem", Dinheiro.de("30.00"));
    private static final ServicoAdicional PRE_TREINO =
            new ServicoAdicional("PRE_TREINO", "Pré-treino", Dinheiro.de("12.30"));

    private static Matricula matricula() {
        return Matricula.getBuilder().comDescricao("Mensal").comCustoBase(Dinheiro.de("129.90")).constroi();
    }

    /** Cada caso embrulha um Servico qualquer; a identidade é a própria Matricula. */
    static Stream<Arguments> servicos() {
        return Stream.of(
                caso("Matricula", s -> s),
                caso("Bioimpedancia", s -> new Bioimpedancia(s, BIOIMPEDANCIA)),
                caso("CadeiraMassagem", s -> new CadeiraMassagem(s, CADEIRA_MASSAGEM)),
                caso("PreTreino", s -> new PreTreino(s, PRE_TREINO)),
                caso("os três empilhados", s -> new PreTreino(
                        new CadeiraMassagem(new Bioimpedancia(s, BIOIMPEDANCIA), CADEIRA_MASSAGEM), PRE_TREINO)));
    }

    private static Arguments caso(String nome, Function<Servico, Servico> montar) {
        return Arguments.of(nome, montar);
    }

    // código cliente que só conhece a interface: é ele que não pode quebrar
    private static String resumo(Servico servico) {
        return servico.getDescricao() + ": " + servico.custo();
    }

    @ParameterizedTest(name = "{0}: custo definido e não negativo")
    @MethodSource("servicos")
    void custoValido(String nome, Function<Servico, Servico> montar) {
        Dinheiro custo = montar.apply(matricula()).custo();

        assertNotNull(custo);
        assertTrue(custo.compareTo(Dinheiro.ZERO) >= 0);
    }

    @ParameterizedTest(name = "{0}: descrição preenchida")
    @MethodSource("servicos")
    void descricaoValida(String nome, Function<Servico, Servico> montar) {
        String descricao = montar.apply(matricula()).getDescricao();

        assertNotNull(descricao);
        assertFalse(descricao.isBlank());
    }

    @ParameterizedTest(name = "{0}: nunca custa menos que o serviço que recebeu")
    @MethodSource("servicos")
    void naoReduzCusto(String nome, Function<Servico, Servico> montar) {
        Servico base = matricula();

        assertTrue(montar.apply(base).custo().compareTo(base.custo()) >= 0);
    }

    @ParameterizedTest(name = "{0}: mantém a descrição do serviço que recebeu")
    @MethodSource("servicos")
    void preservaDescricao(String nome, Function<Servico, Servico> montar) {
        assertTrue(montar.apply(matricula()).getDescricao().startsWith("Mensal"));
    }

    @ParameterizedTest(name = "{0}: chamadas repetidas dão o mesmo resultado")
    @MethodSource("servicos")
    void semEfeitoColateral(String nome, Function<Servico, Servico> montar) {
        Servico servico = montar.apply(matricula());

        assertEquals(resumo(servico), resumo(servico));
    }
}
