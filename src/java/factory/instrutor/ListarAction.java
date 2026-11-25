/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory.instrutor;

import model.Instrutor;
import dao.InstrutorDao;
import factory.ICommand;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import util.TextoSeguro;
import util.Mensagens;

/**
 *
 * @author Shino
 */
public class ListarAction implements ICommand {

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        InstrutorDao instrutorDao = new InstrutorDao();
        Gson gson = new Gson();
        String especialidade = request.getParameter("especialidade");
        String status = request.getParameter("status");
        request.setAttribute("aviso", TextoSeguro.paraScript(request.getParameter("aviso")));

        try {
            List<Instrutor> listaInstrutor = instrutorDao.consultarTodos(especialidade, status);
            request.setAttribute("instrutoresJson", gson.toJson(listaInstrutor));
        } catch (ClassNotFoundException | SQLException ex) {
            request.setAttribute("instrutoresJson", "[]");
            request.setAttribute("aviso", TextoSeguro.paraScript(Mensagens.paraUsuario(ex)));
        }

        request.getRequestDispatcher("instrutor/listar.jsp").forward(request, response);
    }
}
