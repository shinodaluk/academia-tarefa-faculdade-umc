package factory.aluno;

import dao.AlunoDao;
import factory.ICommand;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Aluno;
import util.Mensagens;

public class SalvarAction implements ICommand {

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        AlunoDao alunoDao = new AlunoDao();
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
                id = String.valueOf(alunoDao.cadastrar(builder.constroi()));
            } else {
                alunoDao.atualizar(builder.comId(Integer.parseInt(id)).constroi());
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
