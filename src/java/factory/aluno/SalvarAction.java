package factory.aluno;

import config.Dependencias;
import factory.ICommand;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Aluno;
import repositorio.Repositorio;
import util.Mensagens;

public class SalvarAction implements ICommand {

    private final Repositorio<Aluno> alunos;

    public SalvarAction(Dependencias dependencias) {
        this.alunos = dependencias.alunos();
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        boolean novo = id == null || id.isEmpty();

        try {
            Aluno.AlunoBuilder builder = Aluno.getBuilder()
                    .comNome(request.getParameter("nome"))
                    .comCpf(request.getParameter("cpf"))
                    .comDataNascimento(request.getParameter("dataNascimento"))
                    .comTelefone(request.getParameter("telefone"))
                    .comEmail(request.getParameter("email"))
                    .comStatus(request.getParameter("status"));

            if (novo) {
                id = String.valueOf(alunos.criar(builder.constroi()));
            } else {
                alunos.atualizar(builder.comId(Integer.parseInt(id)).constroi());
            }

            request.setAttribute("redirect", request.getContextPath() + "/aluno?acao=editar&id=" + id);
            request.getRequestDispatcher("redirect.jsp").forward(request, response);
        } catch (Exception ex) {
            // volta para a mesma tela (criar ou editar), sem perder o contexto da edição
            String erro = URLEncoder.encode(Mensagens.paraUsuario(ex), StandardCharsets.UTF_8);
            String volta = novo ? "acao=criar" : "acao=editar&id=" + URLEncoder.encode(id, StandardCharsets.UTF_8);
            request.setAttribute("redirect", request.getContextPath() + "/aluno?" + volta + "&erro=" + erro);
            request.getRequestDispatcher("redirect.jsp").forward(request, response);
        }
    }
}
