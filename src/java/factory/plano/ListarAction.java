/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory.plano;

import model.Plano;
import dao.PlanoDao;
import factory.ICommand;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

/**
 *
 * @author Shino
 */
public class ListarAction implements ICommand {

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PlanoDao planoDao = new PlanoDao();
        Gson gson = new Gson();
        boolean somenteAtivos = "1".equals(request.getParameter("somenteAtivos"));
        request.setAttribute("aviso", request.getParameter("aviso"));

        try {
            List<Plano> listaPlano = planoDao.consultarTodos(somenteAtivos);
            request.setAttribute("planosJson", gson.toJson(listaPlano));
        } catch (ClassNotFoundException | SQLException ex) {
            request.setAttribute("planosJson", "[]");
            request.setAttribute("aviso", ex.getMessage());
        }

        request.getRequestDispatcher("plano/listar.jsp").forward(request, response);
    }
}
