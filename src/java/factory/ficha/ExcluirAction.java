/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory.ficha;

import model.FichaTreino;
import dao.FichaTreinoDao;
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
            response.sendRedirect(request.getContextPath() + "/ficha?acao=listar");
            return;
        }

        FichaTreinoDao fichaDao = new FichaTreinoDao();
        FichaTreino ficha = FichaTreino.getBuilder().comId(Integer.parseInt(id)).constroi();
        // exercícios somem junto (FK ON DELETE CASCADE)
        fichaDao.deletar(ficha);

        request.setAttribute("redirect", request.getContextPath() + "/ficha?acao=listar&aviso=Deletado com sucesso");
        request.getRequestDispatcher("redirect.jsp").forward(request, response);
    }
}
