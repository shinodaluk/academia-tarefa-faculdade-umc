/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory.instrutor;

import com.google.gson.Gson;
import dao.InstrutorDao;
import factory.ICommand;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Instrutor;
import util.TextoSeguro;

/**
 *
 * @author Shino
 */
public class EditarAction implements ICommand {
    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");

        if(id == null || id.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/instrutor?acao=listar");
            return;
        }

        InstrutorDao instrutorDao = new InstrutorDao();
        Instrutor instrutor = Instrutor.getBuilder().comId(Integer.parseInt(id)).constroi();
        Instrutor instrutorEncontrado = instrutorDao.consultarById(instrutor);

        Gson gson = new Gson();
        request.setAttribute("instrutorJson", gson.toJson(instrutorEncontrado));
        request.setAttribute("aviso", TextoSeguro.paraScript(request.getParameter("erro")));

        request.getRequestDispatcher("instrutor/form.jsp").forward(request, response);
    }
}
