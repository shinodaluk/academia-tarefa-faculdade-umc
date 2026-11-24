/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory.plano;

import model.Plano;
import dao.PlanoDao;
import factory.ICommand;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLIntegrityConstraintViolationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Shino
 */
public class ExcluirAction implements ICommand {
    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");

        if(id == null || id.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/plano?acao=listar");
            return;
        }

        PlanoDao planoDao = new PlanoDao();
        Plano plano = Plano.getBuilder().comId(Integer.parseInt(id)).constroi();
        String aviso = "Deletado com sucesso";
        try {
            planoDao.deletar(plano);
        } catch (SQLIntegrityConstraintViolationException ex) {
            // FK de matricula é ON DELETE RESTRICT; a tela já barra, isso cobre o POST direto
            aviso = "O plano tem matrículas vinculadas. Desative-o em vez de excluir.";
        }

        request.setAttribute("redirect", request.getContextPath() + "/plano?acao=listar&aviso=" + URLEncoder.encode(aviso, StandardCharsets.UTF_8));
        request.getRequestDispatcher("redirect.jsp").forward(request, response);
    }
}
