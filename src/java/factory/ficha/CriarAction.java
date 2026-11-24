/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory.ficha;

import com.google.gson.Gson;
import dao.AlunoDao;
import dao.InstrutorDao;
import factory.ICommand;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.FichaTreino;
import org.apache.commons.text.StringEscapeUtils;

/**
 *
 * @author Shino
 */
public class CriarAction implements ICommand {
    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        FichaTreino ficha = FichaTreino.getBuilder()
                .doObjetivoHipertrofia()
                .constroi();
        Gson gson = new Gson();

        request.setAttribute("fichaJson", gson.toJson(ficha));
        request.setAttribute("alunosJson", gson.toJson(new AlunoDao().consultarTodos("", "")));
        request.setAttribute("instrutoresJson", gson.toJson(new InstrutorDao().consultarTodos("", "")));
        request.setAttribute("aviso", StringEscapeUtils.escapeHtml4(request.getParameter("erro")));

        request.getRequestDispatcher("ficha/form.jsp").forward(request, response);
    }
}
