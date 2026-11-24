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
public class SalvarAction implements ICommand {
    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        AlunoDao alunoDao = new AlunoDao();
        try {
            String id = request.getParameter("id");
                    
            if(id == null || id.isEmpty()) {
                Aluno novoAluno = Aluno.getBuilder()
                        .comNome(request.getParameter("nome"))
                        .comCpf(request.getParameter("cpf"))
                        .comDataNascimento(request.getParameter("dataNascimento"))
                        .comTelefone(request.getParameter("telefone"))
                        .comEmail(request.getParameter("email"))
                        .comStatus(request.getParameter("status"))
                        .constroi();
                        
                int newId = alunoDao.cadastrar(novoAluno);
                
                request.setAttribute("redirect", request.getContextPath() + "/aluno?acao=editar&id=" + newId);
                request.getRequestDispatcher("redirect.jsp").forward(request, response);
            } else {
                Aluno aluno = Aluno.getBuilder()
                        .comId(Integer.parseInt(id))
                        .comNome(request.getParameter("nome"))
                        .comCpf(request.getParameter("cpf"))
                        .comDataNascimento(request.getParameter("dataNascimento"))
                        .comTelefone(request.getParameter("telefone"))
                        .comEmail(request.getParameter("email"))
                        .comStatus(request.getParameter("status"))
                        .constroi();
                
                alunoDao.atualizar(aluno);
                
                request.setAttribute("redirect", request.getContextPath() + "/aluno?acao=editar&id=" + id);
                request.getRequestDispatcher("redirect.jsp").forward(request, response);
            }

        } catch (Exception ex) {
            request.setAttribute("redirect", request.getContextPath() + "/aluno?acao=criar&erro="+ex.getMessage());
            request.getRequestDispatcher("redirect.jsp").forward(request, response);
        }
    }
}
