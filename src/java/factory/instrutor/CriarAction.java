/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory.instrutor;

import com.google.gson.Gson;
import factory.ICommand;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Instrutor;
import util.TextoSeguro;

/**
 *
 * @author Shino
 */
public class CriarAction implements ICommand {
    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Instrutor instrutor = Instrutor.getBuilder()
                .doEspecialidadeMusculacao()
                .doStatusAtivo()
                .constroi();
        Gson gson = new Gson();

        request.setAttribute("instrutorJson", gson.toJson(instrutor));
        request.setAttribute("aviso", TextoSeguro.paraScript(request.getParameter("erro")));

        request.getRequestDispatcher("instrutor/form.jsp").forward(request, response);
    }
}
