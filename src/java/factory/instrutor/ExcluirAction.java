/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory.instrutor;

import model.Instrutor;
import dao.InstrutorDao;
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
            response.sendRedirect(request.getContextPath() + "/instrutor?acao=listar");
            return;
        }

        InstrutorDao instrutorDao = new InstrutorDao();
        Instrutor instrutor = Instrutor.getBuilder().comId(Integer.parseInt(id)).constroi();
        // FK de ficha_treino é ON DELETE SET NULL: fichas ficam sem responsável
        instrutorDao.deletar(instrutor);

        request.setAttribute("redirect", request.getContextPath() + "/instrutor?acao=listar&aviso=Deletado com sucesso");
        request.getRequestDispatcher("redirect.jsp").forward(request, response);
    }
}
