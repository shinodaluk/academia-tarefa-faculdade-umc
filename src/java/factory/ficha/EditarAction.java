/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory.ficha;

import com.google.gson.Gson;
import dao.AlunoDao;
import dao.FichaTreinoDao;
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
public class EditarAction implements ICommand {
    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");

        if(id == null || id.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/ficha?acao=listar");
            return;
        }

        FichaTreinoDao fichaDao = new FichaTreinoDao();
        FichaTreino ficha = FichaTreino.getBuilder().comId(Integer.parseInt(id)).constroi();
        FichaTreino fichaEncontrada = fichaDao.consultarById(ficha);

        Gson gson = new Gson();
        request.setAttribute("fichaJson", gson.toJson(fichaEncontrada));
        request.setAttribute("alunosJson", gson.toJson(new AlunoDao().consultarTodos("", "")));
        request.setAttribute("instrutoresJson", gson.toJson(new InstrutorDao().consultarTodos("", "")));
        request.setAttribute("aviso", StringEscapeUtils.escapeHtml4(request.getParameter("erro")));

        request.getRequestDispatcher("ficha/form.jsp").forward(request, response);
    }
}
