/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory.aluno;

import com.google.gson.Gson;
import factory.ICommand;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Aluno;
import util.TextoSeguro;

/**
 *
 * @author Shino
 */
public class CriarAction implements ICommand {
    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Aluno aluno = Aluno.getBuilder().doStatusAtivo().constroi();
        Gson gson = new Gson();

        String alunoJson = gson.toJson(aluno);
        request.setAttribute("alunoJson", alunoJson);
        request.setAttribute("aviso", TextoSeguro.paraScript(request.getParameter("erro")));
        
        request.getRequestDispatcher("aluno/form.jsp").forward(request, response);
    }
}
