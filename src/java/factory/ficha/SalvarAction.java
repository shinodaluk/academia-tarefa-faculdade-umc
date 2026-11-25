package factory.ficha;

import config.Dependencias;
import factory.ICommand;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.FichaExercicio;
import model.FichaTreino;
import repositorio.Repositorio;
import util.Mensagens;

public class SalvarAction implements ICommand {

    private final Repositorio<FichaTreino> fichas;

    public SalvarAction(Dependencias dependencias) {
        this.fichas = dependencias.fichas();
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        boolean novo = id == null || id.isEmpty();

        try {
            FichaTreino.FichaTreinoBuilder builder = FichaTreino.getBuilder()
                    .comAlunoId(Integer.parseInt(request.getParameter("alunoId")))
                    .comInstrutorId(vazio(request.getParameter("instrutorId")) ? null : Integer.valueOf(request.getParameter("instrutorId")))
                    .comObjetivo(request.getParameter("objetivo"))
                    .comDataCriacao(vazio(request.getParameter("dataCriacao")) ? LocalDate.now().toString() : request.getParameter("dataCriacao"))
                    .comExercicios(lerExercicios(request));

            if (novo) {
                id = String.valueOf(fichas.criar(builder.constroi()));
            } else {
                fichas.atualizar(builder.comId(Integer.parseInt(id)).constroi());
            }

            request.setAttribute("redirect", request.getContextPath() + "/ficha?acao=editar&id=" + id);
            request.getRequestDispatcher("redirect.jsp").forward(request, response);
        } catch (Exception ex) {
            // volta para a mesma tela (criar ou editar), sem perder o contexto da edição
            String erro = URLEncoder.encode(Mensagens.paraUsuario(ex), StandardCharsets.UTF_8);
            String volta = novo ? "acao=criar" : "acao=editar&id=" + URLEncoder.encode(id, StandardCharsets.UTF_8);
            request.setAttribute("redirect", request.getContextPath() + "/ficha?" + volta + "&erro=" + erro);
            request.getRequestDispatcher("redirect.jsp").forward(request, response);
        }
    }

    // o form manda exercicios[0].nome, exercicios[1].nome... com índices contíguos
    private List<FichaExercicio> lerExercicios(HttpServletRequest request) {
        List<FichaExercicio> exercicios = new ArrayList<>();
        for (int i = 0; request.getParameter("exercicios[" + i + "].nome") != null; i++) {
            String prefixo = "exercicios[" + i + "].";
            String carga = request.getParameter(prefixo + "cargaKg");
            exercicios.add(FichaExercicio.getBuilder()
                    .comOrdem(i + 1)
                    .comNome(request.getParameter(prefixo + "nome"))
                    .comSeries(Integer.parseInt(request.getParameter(prefixo + "series")))
                    .comRepeticoes(request.getParameter(prefixo + "repeticoes"))
                    .comCargaKg(vazio(carga) ? null : carga)
                    .constroi());
        }
        return exercicios;
    }

    private static boolean vazio(String texto) {
        return texto == null || texto.isEmpty();
    }
}
