package api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import model.Aluno;
import model.AvaliacaoFisica;
import model.Cpf;
import model.FichaExercicio;
import model.FichaTreino;
import model.Instrutor;
import model.Matricula;
import model.Plano;

/** Validadores de entrada da API, espelhando as restrições do esquema SQL. */
public final class Validacoes {

    private static final List<Integer> FIDELIDADES = List.of(0, 6, 12);
    private static final Pattern PRESSAO = Pattern.compile("[0-9]{2,3}/[0-9]{2,3}");

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
            obrigatorio(erros, aluno.getStatus(), "status");
            return erros;
        };
    }

    public static Validador<Instrutor> instrutor() {
        return instrutor -> {
            List<String> erros = new ArrayList<>();
            texto(erros, instrutor.getNome(), "nome");
            texto(erros, instrutor.getCref(), "cref");
            obrigatorio(erros, instrutor.getEspecialidade(), "especialidade");
            obrigatorio(erros, instrutor.getStatus(), "status");
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
            obrigatorio(erros, matricula.getSituacao(), "situacao");
            return erros;
        };
    }

    public static Validador<FichaTreino> ficha() {
        return ficha -> {
            List<String> erros = new ArrayList<>();
            positivo(erros, ficha.getAlunoId(), "alunoId");
            obrigatorio(erros, ficha.getObjetivo(), "objetivo");
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

    public static Validador<AvaliacaoFisica> avaliacaoFisica() {
        return avaliacao -> {
            List<String> erros = new ArrayList<>();
            presente(erros, avaliacao.getDataAvaliacao(), "dataAvaliacao");
            faixa(erros, avaliacao.getPesoKg(), true, "1", "400", "pesoKg");
            faixa(erros, avaliacao.getAlturaM(), true, "0.5", "2.6", "alturaM");
            faixa(erros, avaliacao.getPercentualGordura(), false, "0", "70", "percentualGordura");
            faixa(erros, avaliacao.getMassaMagraKg(), false, "1", "400", "massaMagraKg");
            faixa(erros, avaliacao.getCinturaCm(), false, "30", "250", "cinturaCm");
            String pressao = avaliacao.getPressaoArterial();
            if (pressao != null && !PRESSAO.matcher(pressao).matches()) {
                erros.add("pressaoArterial deve seguir o formato 120/80");
            }
            Integer frequencia = avaliacao.getFrequenciaCardiaca();
            if (frequencia != null && (frequencia < 20 || frequencia > 250)) {
                erros.add("frequenciaCardiaca deve estar entre 20 e 250");
            }
            return erros;
        };
    }

    // limites cabem nas colunas DECIMAL do esquema
    private static void faixa(List<String> erros, BigDecimal valor, boolean obrigatorio,
            String minimo, String maximo, String campo) {
        if (valor == null && !obrigatorio) {
            return;
        }
        if (valor == null || valor.compareTo(new BigDecimal(minimo)) < 0
                || valor.compareTo(new BigDecimal(maximo)) > 0) {
            erros.add(campo + " deve estar entre " + minimo + " e " + maximo);
        }
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

    // valor fora da lista já é barrado ao converter o texto no enum
    private static void obrigatorio(List<String> erros, Object valor, String campo) {
        if (valor == null) {
            erros.add(campo + " é obrigatório");
        }
    }
}
