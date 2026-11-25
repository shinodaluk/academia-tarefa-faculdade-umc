package api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import model.Matricula;
import model.MontadorAdicionais;
import model.Servico;
import model.ServicoAdicional;

/** GET /api/matriculas/{id}/adicionais?codigos=A,B: valor da matrícula com os decorators aplicados. */
public class AdicionaisMatriculaRecurso implements RecursoApi {

    @FunctionalInterface
    public interface FonteMatriculas {
        Matricula buscar(int id) throws Exception;
    }

    @FunctionalInterface
    public interface FonteAdicionais {
        ServicoAdicional porCodigo(String codigo) throws Exception;
    }

    private final FonteMatriculas matriculas;
    private final FonteAdicionais adicionais;

    public AdicionaisMatriculaRecurso(FonteMatriculas matriculas, FonteAdicionais adicionais) {
        this.matriculas = matriculas;
        this.adicionais = adicionais;
    }

    @Override
    public RespostaApi tratar(RequisicaoApi requisicao) throws Exception {
        if (!"GET".equals(requisicao.metodo())) {
            throw ErroApi.metodoNaoPermitido("GET");
        }
        Set<String> codigos = lerCodigos(requisicao.parametros().get("codigos"));
        Matricula matricula = matriculas.buscar(ControladorRest.lerId(requisicao.segmentos().get(0)));

        Servico composto = matricula;
        List<Map<String, Object>> aplicados = new ArrayList<>();
        for (String codigo : codigos) {
            ServicoAdicional adicional = adicionais.porCodigo(codigo);
            composto = MontadorAdicionais.decorar(composto, adicional);
            aplicados.add(Map.of(
                    "codigo", adicional.getCodigo(),
                    "descricao", adicional.getDescricao(),
                    "valorMensal", adicional.getValorMensal()));
        }

        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("matriculaId", matricula.getId());
        corpo.put("descricaoBase", matricula.getDescricao());
        corpo.put("custoBase", dinheiro(matricula.custo()));
        corpo.put("adicionais", aplicados);
        corpo.put("descricao", composto.getDescricao());
        corpo.put("totalMensal", dinheiro(composto.custo()));
        return RespostaApi.ok(corpo);
    }

    private static Set<String> lerCodigos(String parametro) {
        Set<String> codigos = new LinkedHashSet<>();
        if (parametro == null) {
            return codigos;
        }
        for (String parte : parametro.split(",")) {
            String codigo = parte.trim();
            if (codigo.isEmpty()) {
                continue;
            }
            if (!MontadorAdicionais.reconhece(codigo)) {
                throw ErroApi.requisicaoInvalida("Código de serviço adicional não reconhecido: " + codigo);
            }
            codigos.add(codigo);
        }
        return codigos;
    }

    private static BigDecimal dinheiro(double valor) {
        return BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP);
    }
}
