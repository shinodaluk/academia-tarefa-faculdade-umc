/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory.plano;

import com.google.gson.Gson;
import factory.ICommand;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Plano;
import org.apache.commons.text.StringEscapeUtils;

/**
 *
 * @author Shino
 */
public class CriarAction implements ICommand {
    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Plano plano = Plano.getBuilder()
                .comDuracaoMeses(1)
                .doAtivo()
                .constroi();
        Gson gson = new Gson();

        request.setAttribute("planoJson", gson.toJson(plano));
        request.setAttribute("aviso", StringEscapeUtils.escapeHtml4(request.getParameter("erro")));

        request.getRequestDispatcher("plano/form.jsp").forward(request, response);
    }
}
