/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory.matricula;

import model.Matricula;
import dao.MatriculaDao;
import factory.ICommand;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Shino
 */
public class ExcluirAction implements ICommand {
    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");

        if(id == null || id.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/matricula?acao=listar");
            return;
        }

        MatriculaDao matriculaDao = new MatriculaDao();
        Matricula matricula = Matricula.getBuilder().comId(Integer.parseInt(id)).constroi();
        // mensalidades somem junto (FK ON DELETE CASCADE)
        matriculaDao.deletar(matricula);

        request.setAttribute("redirect", request.getContextPath() + "/matricula?acao=listar&aviso=Deletado com sucesso");
        request.getRequestDispatcher("redirect.jsp").forward(request, response);
    }
}
