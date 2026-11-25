package api;

import java.util.ArrayList;
import java.util.List;
import model.Aluno;
import model.Cpf;
import model.FichaExercicio;
import model.FichaTreino;
import model.Instrutor;
import model.Matricula;
import model.Plano;

/** Validadores de entrada da API, espelhando as restrições do esquema SQL. */
public final class Validacoes {

    private static final List<String> STATUS = List.of("Ativo", "Inativo");
    private static final List<String> ESPECIALIDADES = List.of("Musculação", "Funcional", "Condicionamento", "Pilates");
    private static final List<String> SITUACOES = List.of("Ativa", "Cancelada", "Suspensa");
    private static final List<String> OBJETIVOS = List.of("Hipertrofia", "Emagrecimento", "Condicionamento");
    private static final List<Integer> FIDELIDADES = List.of(0, 6, 12);

    private Validacoes() {
    }

    public static Validador<Plano> plano() {
        return plano -> {
            List<String> erros = new ArrayList<>();
            texto(erros, plano.getNome(), "nome");
            if (plano.getDuracaoMeses() < 1 || plano.getDuracaoMeses() > 36) {
                erros.add("duracaoMeses deve estar entre 1 e 36");
            }
            // negativo já é barrado pelo próprio Dinheiro
            if (plano.getValorMensal() == null) {
                erros.add("valorMensal é obrigatório");
            }
            return erros;
        };
    }

    public static Validador<Aluno> aluno() {
        return aluno -> {
            List<String> erros = new ArrayList<>();
            texto(erros, aluno.getNome(), "nome");
            cpf(erros, aluno.getCpf());
            presente(erros, aluno.getDataNascimento(), "dataNascimento");
            umDe(erros, aluno.getStatus(), STATUS, "status");
            return erros;
        };
    }

    public static Validador<Instrutor> instrutor() {
        return instrutor -> {
            List<String> erros = new ArrayList<>();
            texto(erros, instrutor.getNome(), "nome");
            texto(erros, instrutor.getCref(), "cref");
            umDe(erros, instrutor.getEspecialidade(), ESPECIALIDADES, "especialidade");
            umDe(erros, instrutor.getStatus(), STATUS, "status");
            return erros;
        };
    }

    public static Validador<Matricula> matricula() {
        return matricula -> {
            List<String> erros = new ArrayList<>();
            positivo(erros, matricula.getAlunoId(), "alunoId");
            positivo(erros, matricula.getPlanoId(), "planoId");
            presente(erros, matricula.getDataInicio(), "dataInicio");
            if (!FIDELIDADES.contains(matricula.getFidelidadeMeses())) {
                erros.add("fidelidadeMeses deve ser 0, 6 ou 12");
            }
            if (matricula.getDiaVencimento() < 1 || matricula.getDiaVencimento() > 28) {
                erros.add("diaVencimento deve estar entre 1 e 28");
            }
            umDe(erros, matricula.getSituacao(), SITUACOES, "situacao");
            return erros;
        };
    }

    public static Validador<FichaTreino> ficha() {
        return ficha -> {
            List<String> erros = new ArrayList<>();
            positivo(erros, ficha.getAlunoId(), "alunoId");
            umDe(erros, ficha.getObjetivo(), OBJETIVOS, "objetivo");
            presente(erros, ficha.getDataCriacao(), "dataCriacao");
            if (ficha.getExercicios() == null) {
                erros.add("exercicios deve ser uma lista");
                return erros;
            }
            for (int i = 0; i < ficha.getExercicios().size(); i++) {
                FichaExercicio exercicio = ficha.getExercicios().get(i);
                if (exercicio == null) {
                    erros.add("exercicios[" + i + "] deve ser um objeto");
                    continue;
                }
                String prefixo = "exercicios[" + i + "].";
                texto(erros, exercicio.getNome(), prefixo + "nome");
                positivo(erros, exercicio.getSeries(), prefixo + "series");
                texto(erros, exercicio.getRepeticoes(), prefixo + "repeticoes");
            }
            return erros;
        };
    }

    // o formato já é garantido pelo Cpf; aqui só os dígitos verificadores
    private static void cpf(List<String> erros, Cpf cpf) {
        if (cpf == null) {
            erros.add("cpf é obrigatório");
            return;
        }
        if (!cpf.digitosVerificadoresValidos()) {
            erros.add("cpf com dígitos verificadores inválidos");
        }
    }

    private static void texto(List<String> erros, String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            erros.add(campo + " é obrigatório");
        }
    }

    private static void presente(List<String> erros, Object valor, String campo) {
        if (valor == null) {
            erros.add(campo + " é obrigatório (formato yyyy-MM-dd)");
        }
    }

    private static void positivo(List<String> erros, int valor, String campo) {
        if (valor <= 0) {
            erros.add(campo + " deve ser maior que zero");
        }
    }

    private static void umDe(List<String> erros, String valor, List<String> permitidos, String campo) {
        if (valor == null || !permitidos.contains(valor)) {
            erros.add(campo + " deve ser um de " + permitidos);
        }
    }
}
