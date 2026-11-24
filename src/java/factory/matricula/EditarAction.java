/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory.matricula;

import com.google.gson.Gson;
import dao.AlunoDao;
import dao.MatriculaDao;
import dao.PlanoDao;
import factory.ICommand;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Matricula;
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
            response.sendRedirect(request.getContextPath() + "/matricula?acao=listar");
            return;
        }

        MatriculaDao matriculaDao = new MatriculaDao();
        Matricula matricula = Matricula.getBuilder().comId(Integer.parseInt(id)).constroi();
        Matricula matriculaEncontrada = matriculaDao.consultarById(matricula);

        Gson gson = new Gson();
        request.setAttribute("matriculaJson", gson.toJson(matriculaEncontrada));
        request.setAttribute("alunosJson", gson.toJson(new AlunoDao().consultarTodos("", "")));
        request.setAttribute("planosJson", gson.toJson(new PlanoDao().consultarTodos(false)));
        request.setAttribute("aviso", StringEscapeUtils.escapeHtml4(request.getParameter("erro")));

        request.getRequestDispatcher("matricula/form.jsp").forward(request, response);
    }
}
