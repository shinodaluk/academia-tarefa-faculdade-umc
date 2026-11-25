package api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import model.Aluno;
import model.AvaliacaoFisica;
import model.Dinheiro;
import model.FichaExercicio;
import model.FichaTreino;
import model.Instrutor;
import model.Matricula;
import model.Plano;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ValidacoesTest {

    @Test
    @DisplayName("Plano completo é válido")
    void planoValido() {
        Plano plano = Plano.getBuilder().comNome("Mensal").comDuracaoMeses(1).comValorMensal(Dinheiro.de("129.90")).constroi();

        assertTrue(Validacoes.plano().validar(plano).isEmpty());
    }

    @Test
    @DisplayName("Plano sem nome, com duração fora de 1–36 e sem valor acumula três erros")
    void planoInvalido() {
        Plano plano = Plano.getBuilder().comDuracaoMeses(40).constroi();

        assertEquals(3, Validacoes.plano().validar(plano).size());
    }

    @Test
    @DisplayName("Aluno completo é válido")
    void alunoValido() {
        Aluno aluno = Aluno.getBuilder().comNome("Ana").comCpf("529.982.247-25")
                .comDataNascimento("2000-01-31").comStatus("Ativo").constroi();

        assertTrue(Validacoes.aluno().validar(aluno).isEmpty());
    }

    @Test
    @DisplayName("Aluno sem CPF, sem data de nascimento e sem status é inválido")
    void alunoInvalido() {
        Aluno aluno = Aluno.getBuilder().comNome("Ana").constroi();

        assertEquals(List.of(
                "cpf é obrigatório",
                "dataNascimento é obrigatório (formato yyyy-MM-dd)",
                "status é obrigatório"), Validacoes.aluno().validar(aluno));
    }

    @Test
    @DisplayName("Aluno com CPF de dígitos verificadores errados é inválido")
    void alunoCpfComDigitoErrado() {
        Aluno aluno = Aluno.getBuilder().comNome("Ana").comCpf("529.982.247-26")
                .comDataNascimento("2000-01-31").comStatus("Ativo").constroi();

        assertEquals(List.of("cpf com dígitos verificadores inválidos"), Validacoes.aluno().validar(aluno));
    }

    @Test
    @DisplayName("Instrutor sem especialidade é inválido")
    void instrutorSemEspecialidade() {
        Instrutor instrutor = Instrutor.getBuilder().comNome("Bia").comCref("123456-G/SP")
                .comStatus("Ativo").constroi();

        assertEquals(List.of("especialidade é obrigatório"), Validacoes.instrutor().validar(instrutor));
    }

    @Test
    @DisplayName("Matrícula completa é válida")
    void matriculaValida() {
        assertTrue(Validacoes.matricula().validar(matricula(6, 10)).isEmpty());
    }

    @Test
    @DisplayName("Matrícula com fidelidade fora de 0/6/12 e vencimento fora de 1–28 é inválida")
    void matriculaInvalida() {
        assertEquals(2, Validacoes.matricula().validar(matricula(7, 30)).size());
    }

    @Test
    @DisplayName("Ficha com exercício sem nome é inválida")
    void fichaComExercicioInvalido() {
        FichaTreino ficha = FichaTreino.getBuilder().comAlunoId(1).comObjetivo("Hipertrofia")
                .comDataCriacao("2026-11-24")
                .comExercicios(List.of(FichaExercicio.getBuilder().comSeries(3).comRepeticoes("12").constroi()))
                .constroi();

        assertEquals(List.of("exercicios[0].nome é obrigatório"), Validacoes.ficha().validar(ficha));
    }

    @Test
    @DisplayName("Ficha com exercício nulo na lista é inválida, sem NullPointerException")
    void fichaComExercicioNulo() {
        List<FichaExercicio> exercicios = new ArrayList<>();
        exercicios.add(null);
        FichaTreino ficha = FichaTreino.getBuilder().comAlunoId(1).comObjetivo("Hipertrofia")
                .comDataCriacao("2026-11-24").comExercicios(exercicios).constroi();

        assertEquals(List.of("exercicios[0] deve ser um objeto"), Validacoes.ficha().validar(ficha));
    }

    @Test
    @DisplayName("Ficha completa é válida")
    void fichaValida() {
        FichaTreino ficha = FichaTreino.getBuilder().comAlunoId(1).comObjetivo("Emagrecimento")
                .comDataCriacao("2026-11-24")
                .comExercicios(List.of(FichaExercicio.getBuilder().comNome("Agachamento")
                        .comSeries(4).comRepeticoes("10").constroi()))
                .constroi();

        assertTrue(Validacoes.ficha().validar(ficha).isEmpty());
    }

    private static Matricula matricula(int fidelidade, int diaVencimento) {
        return Matricula.getBuilder().comAlunoId(1).comPlanoId(1).comDataInicio("2026-11-01")
                .comFidelidadeMeses(fidelidade).comDiaVencimento(diaVencimento).comSituacao("Ativa")
                .constroi();
    }

    @Test
    @DisplayName("Avaliação física com data, peso e altura é válida")
    void avaliacaoValida() {
        AvaliacaoFisica avaliacao = AvaliacaoFisica.getBuilder().comDataAvaliacao("2026-02-10")
                .comPesoKg(new BigDecimal("78.4")).comAlturaM(new BigDecimal("1.76"))
                .comPressaoArterial("120/80").comFrequenciaCardiaca(64).constroi();

        assertTrue(Validacoes.avaliacaoFisica().validar(avaliacao).isEmpty());
    }

    @Test
    @DisplayName("Avaliação sem data, peso e altura e com medidas fora da faixa acumula erros")
    void avaliacaoInvalida() {
        AvaliacaoFisica avaliacao = AvaliacaoFisica.getBuilder()
                .comPercentualGordura(new BigDecimal("90")).comPressaoArterial("12x8")
                .comFrequenciaCardiaca(5).constroi();

        assertEquals(List.of(
                "dataAvaliacao é obrigatório (formato yyyy-MM-dd)",
                "pesoKg deve estar entre 1 e 400",
                "alturaM deve estar entre 0.5 e 2.6",
                "percentualGordura deve estar entre 0 e 70",
                "pressaoArterial deve seguir o formato 120/80",
                "frequenciaCardiaca deve estar entre 20 e 250"),
                Validacoes.avaliacaoFisica().validar(avaliacao));
    }
}
