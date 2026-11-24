/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory.aluno;

import model.Aluno;
import dao.AlunoDao;
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
        AlunoDao alunoDao = new AlunoDao();
        Aluno aluno = Aluno.getBuilder().comId(Integer.parseInt(id)).constroi();
        
        if(id == null) {
            response.sendRedirect(request.getContextPath() + "/aluno?acao=listar");
            return;
        }
        
        alunoDao.deletar(aluno);

        request.setAttribute("redirect", request.getContextPath() + "/aluno?acao=listar&aviso=Deletado com sucesso");
        request.getRequestDispatcher("redirect.jsp").forward(request, response);
    }
}
