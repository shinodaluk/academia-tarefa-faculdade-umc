package factory.plano;

import config.Dependencias;
import factory.ICommand;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Dinheiro;
import model.Plano;
import repositorio.Repositorio;
import util.Mensagens;

public class SalvarAction implements ICommand {

    private final Repositorio<Plano> planos;

    public SalvarAction(Dependencias dependencias) {
        this.planos = dependencias.planos();
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        boolean novo = id == null || id.isEmpty();

        try {
            Plano.PlanoBuilder builder = Plano.getBuilder()
                    .comNome(request.getParameter("nome"))
                    .comDuracaoMeses(Integer.parseInt(request.getParameter("duracaoMeses")))
                    .comValorMensal(Dinheiro.de(request.getParameter("valorMensal")))
                    .comDescricao(request.getParameter("descricao"))
                    // checkbox desmarcado não é enviado no POST
                    .comAtivo(request.getParameter("ativo") != null);

            if (novo) {
                id = String.valueOf(planos.criar(builder.constroi()));
            } else {
                planos.atualizar(builder.comId(Integer.parseInt(id)).constroi());
            }

            request.setAttribute("redirect", request.getContextPath() + "/plano?acao=editar&id=" + id);
            request.getRequestDispatcher("redirect.jsp").forward(request, response);
        } catch (Exception ex) {
            // volta para a mesma tela (criar ou editar), sem perder o contexto da edição
            String erro = URLEncoder.encode(Mensagens.paraUsuario(ex), StandardCharsets.UTF_8);
            String volta = novo ? "acao=criar" : "acao=editar&id=" + URLEncoder.encode(id, StandardCharsets.UTF_8);
            request.setAttribute("redirect", request.getContextPath() + "/plano?" + volta + "&erro=" + erro);
            request.getRequestDispatcher("redirect.jsp").forward(request, response);
        }
    }
}
