/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory.aluno;

import com.google.gson.Gson;
import dao.AlunoDao;
import factory.ICommand;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Aluno;

/**
 *
 * @author Shino
 */
public class EditarAction implements ICommand {
    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        AlunoDao alunoDao = new AlunoDao();
        Aluno aluno = Aluno.getBuilder().comId(Integer.parseInt(id)).constroi();
        
        if(id == null) {
            response.sendRedirect(request.getContextPath() + "/aluno?acao=listar");
            return;
        }
        
        Aluno alunoEncontrado = alunoDao.consultarById(aluno);
        
        Gson gson = new Gson();
        String alunoJson = gson.toJson(alunoEncontrado);
        request.setAttribute("alunoJson", alunoJson);
        
        request.getRequestDispatcher("aluno/form.jsp").forward(request, response);
    }
}
