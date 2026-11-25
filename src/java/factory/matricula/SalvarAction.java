package factory.matricula;

import config.Dependencias;
import factory.ICommand;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Matricula;
import repositorio.Repositorio;
import util.Mensagens;

public class SalvarAction implements ICommand {

    private final Repositorio<Matricula> matriculas;

    public SalvarAction(Dependencias dependencias) {
        this.matriculas = dependencias.matriculas();
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        boolean novo = id == null || id.isEmpty();

        try {
            Matricula.MatriculaBuilder builder = Matricula.getBuilder()
                    .comAlunoId(Integer.parseInt(request.getParameter("alunoId")))
                    .comPlanoId(Integer.parseInt(request.getParameter("planoId")))
                    .comDataInicio(request.getParameter("dataInicio"))
                    .comFidelidadeMeses(Integer.parseInt(request.getParameter("fidelidadeMeses")))
                    // checkbox desmarcado não é enviado no POST
                    .comDescontoEstudante(request.getParameter("descontoEstudante") != null)
                    .comDiaVencimento(Integer.parseInt(request.getParameter("diaVencimento")))
                    .comSituacao(request.getParameter("situacao"));

            if (novo) {
                id = String.valueOf(matriculas.criar(builder.constroi()));
            } else {
                matriculas.atualizar(builder.comId(Integer.parseInt(id)).constroi());
            }

            request.setAttribute("redirect", request.getContextPath() + "/matricula?acao=editar&id=" + id);
            request.getRequestDispatcher("redirect.jsp").forward(request, response);
        } catch (Exception ex) {
            // volta para a mesma tela (criar ou editar), sem perder o contexto da edição
            String erro = URLEncoder.encode(Mensagens.paraUsuario(ex), StandardCharsets.UTF_8);
            String volta = novo ? "acao=criar" : "acao=editar&id=" + URLEncoder.encode(id, StandardCharsets.UTF_8);
            request.setAttribute("redirect", request.getContextPath() + "/matricula?" + volta + "&erro=" + erro);
            request.getRequestDispatcher("redirect.jsp").forward(request, response);
        }
    }
}
