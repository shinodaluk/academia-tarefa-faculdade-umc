package factory.plano;

import config.Dependencias;
import factory.ICommand;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLIntegrityConstraintViolationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Plano;
import repositorio.Repositorio;
import util.Mensagens;

public class ExcluirAction implements ICommand {

    private final Repositorio<Plano> planos;

    public ExcluirAction(Dependencias dependencias) {
        this.planos = dependencias.planos();
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        if (id == null || id.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/plano?acao=listar");
            return;
        }

        String aviso = "Deletado com sucesso";
        try {
            planos.excluir(Integer.parseInt(id));
        } catch (SQLIntegrityConstraintViolationException ex) {
            // FK de matricula é ON DELETE RESTRICT; a tela já barra, isso cobre o POST direto
            aviso = "O plano tem matrículas vinculadas. Desative-o em vez de excluir.";
        } catch (Exception ex) {
            aviso = Mensagens.paraUsuario(ex);
        }

        request.setAttribute("redirect", request.getContextPath() + "/plano?acao=listar&aviso="
                + URLEncoder.encode(aviso, StandardCharsets.UTF_8));
        request.getRequestDispatcher("redirect.jsp").forward(request, response);
    }
}
