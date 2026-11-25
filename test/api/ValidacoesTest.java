package api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import model.Aluno;
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
        Plano plano = Plano.getBuilder().comNome("Mensal").comDuracaoMeses(1).comValorMensal(129.90).constroi();

        assertTrue(Validacoes.plano().validar(plano).isEmpty());
    }

    @Test
    @DisplayName("Plano sem nome, com duração fora de 1–36 e sem valor acumula três erros")
    void planoInvalido() {
        Plano plano = Plano.getBuilder().comDuracaoMeses(40).constroi();

        assertEquals(3, Validacoes.plano().validar(plano).size());
    }

    @Test
    @DisplayName("Plano com valor negativo é inválido")
    void planoValorNegativo() {
        Plano plano = Plano.getBuilder().comNome("X").comDuracaoMeses(1).comValorMensal(-1.0).constroi();

        assertEquals(List.of("valorMensal deve ser maior ou igual a zero"), Validacoes.plano().validar(plano));
    }

    @Test
    @DisplayName("Aluno completo é válido")
    void alunoValido() {
        Aluno aluno = Aluno.getBuilder().comNome("Ana").comCpf("123.456.789-00")
                .comDataNascimento("2000-01-31").comStatus("Ativo").constroi();

        assertTrue(Validacoes.aluno().validar(aluno).isEmpty());
    }

    @Test
    @DisplayName("Aluno sem CPF, sem data de nascimento e com status desconhecido é inválido")
    void alunoInvalido() {
        Aluno aluno = Aluno.getBuilder().comNome("Ana").comStatus("Talvez").constroi();

        assertEquals(List.of(
                "cpf é obrigatório",
                "dataNascimento é obrigatório (formato yyyy-MM-dd)",
                "status deve ser um de [Ativo, Inativo]"), Validacoes.aluno().validar(aluno));
    }

    @Test
    @DisplayName("Instrutor com especialidade fora da lista é inválido")
    void instrutorEspecialidadeInvalida() {
        Instrutor instrutor = Instrutor.getBuilder().comNome("Bia").comCref("123456-G/SP")
                .comEspecialidade("Natação").comStatus("Ativo").constroi();

        assertEquals(1, Validacoes.instrutor().validar(instrutor).size());
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
}
