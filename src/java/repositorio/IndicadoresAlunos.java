package repositorio;

import java.util.Map;

/** Números do painel de alunos (ativos, matrículas, vencidas, receita). */
@FunctionalInterface
public interface IndicadoresAlunos {

    Map<String, Number> calcular() throws Exception;
}
