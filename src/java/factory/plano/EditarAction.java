/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory.plano;

import com.google.gson.Gson;
import dao.PlanoDao;
import factory.ICommand;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Plano;
import org.apache.commons.text.StringEscapeUtils;

/**
 *
 * @author Shino
 */
public class EditarAction implements ICommand {
    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");

        if(id == null || id.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/plano?acao=listar");
            return;
        }

        PlanoDao planoDao = new PlanoDao();
        Plano plano = Plano.getBuilder().comId(Integer.parseInt(id)).constroi();
        Plano planoEncontrado = planoDao.consultarById(plano);

        Gson gson = new Gson();
        request.setAttribute("planoJson", gson.toJson(planoEncontrado));
        request.setAttribute("aviso", StringEscapeUtils.escapeHtml4(request.getParameter("erro")));

        request.getRequestDispatcher("plano/form.jsp").forward(request, response);
    }
}
