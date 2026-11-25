package factory.instrutor;

import config.Dependencias;
import factory.ICommand;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Instrutor;
import repositorio.Repositorio;
import util.Mensagens;

public class SalvarAction implements ICommand {

    private final Repositorio<Instrutor> instrutores;

    public SalvarAction(Dependencias dependencias) {
        this.instrutores = dependencias.instrutores();
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        boolean novo = id == null || id.isEmpty();

        try {
            Instrutor.InstrutorBuilder builder = Instrutor.getBuilder()
                    .comNome(request.getParameter("nome"))
                    .comCref(request.getParameter("cref"))
                    .comEspecialidade(request.getParameter("especialidade"))
                    .comTelefone(request.getParameter("telefone"))
                    .comEmail(request.getParameter("email"))
                    .comStatus(request.getParameter("status"));

            if (novo) {
                id = String.valueOf(instrutores.criar(builder.constroi()));
            } else {
                instrutores.atualizar(builder.comId(Integer.parseInt(id)).constroi());
            }

            request.setAttribute("redirect", request.getContextPath() + "/instrutor?acao=editar&id=" + id);
            request.getRequestDispatcher("redirect.jsp").forward(request, response);
        } catch (Exception ex) {
            // volta para a mesma tela (criar ou editar), sem perder o contexto da edição
            String erro = URLEncoder.encode(Mensagens.paraUsuario(ex), StandardCharsets.UTF_8);
            String volta = novo ? "acao=criar" : "acao=editar&id=" + URLEncoder.encode(id, StandardCharsets.UTF_8);
            request.setAttribute("redirect", request.getContextPath() + "/instrutor?" + volta + "&erro=" + erro);
            request.getRequestDispatcher("redirect.jsp").forward(request, response);
        }
    }
}
