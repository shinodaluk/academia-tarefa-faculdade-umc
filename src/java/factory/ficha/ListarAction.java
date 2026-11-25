/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory.ficha;

import model.FichaTreino;
import dao.FichaTreinoDao;
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
        FichaTreinoDao fichaDao = new FichaTreinoDao();
        Gson gson = new Gson();
        String objetivo = request.getParameter("objetivo");
        request.setAttribute("aviso", TextoSeguro.paraScript(request.getParameter("aviso")));

        try {
            List<FichaTreino> listaFicha = fichaDao.consultarTodos(objetivo);
            request.setAttribute("fichasJson", gson.toJson(listaFicha));
        } catch (ClassNotFoundException | SQLException ex) {
            request.setAttribute("fichasJson", "[]");
            request.setAttribute("aviso", TextoSeguro.paraScript(Mensagens.paraUsuario(ex)));
        }

        request.getRequestDispatcher("ficha/listar.jsp").forward(request, response);
    }
}
