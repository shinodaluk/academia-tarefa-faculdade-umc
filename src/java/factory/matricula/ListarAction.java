/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory.matricula;

import model.Matricula;
import dao.MatriculaDao;
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
        MatriculaDao matriculaDao = new MatriculaDao();
        Gson gson = new Gson();
        String situacao = request.getParameter("situacao");
        request.setAttribute("aviso", TextoSeguro.paraScript(request.getParameter("aviso")));

        try {
            List<Matricula> listaMatricula = matriculaDao.consultarTodos(situacao);
            request.setAttribute("matriculasJson", gson.toJson(listaMatricula));
        } catch (ClassNotFoundException | SQLException ex) {
            request.setAttribute("matriculasJson", "[]");
            request.setAttribute("aviso", TextoSeguro.paraScript(Mensagens.paraUsuario(ex)));
        }

        request.getRequestDispatcher("matricula/listar.jsp").forward(request, response);
    }
}
