/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory.aluno;

import model.Aluno;
import dao.AlunoDao;
import factory.ICommand;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import java.util.Map;
import util.TextoSeguro;
import util.Mensagens;

/**
 *
 * @author Shino
 */
public class ListarAction implements ICommand {
    
    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        AlunoDao alunoDao = new AlunoDao();
        Gson gson = new Gson();
        String busca = request.getParameter("busca");
        String status = request.getParameter("status");
        String aviso = request.getParameter("aviso");
        request.setAttribute("aviso", TextoSeguro.paraScript(aviso));
        
        try {
            List<Aluno> listaAluno = alunoDao.consultarTodos(busca, status);
            String alunosJson = gson.toJson(listaAluno);
            request.setAttribute("alunosJson", alunosJson);
            
            Map<String, Number> alunosAtivos = alunoDao.alunosAtivos();
            String indicadoresJson = gson.toJson(alunosAtivos);
            request.setAttribute("indicadoresJson", indicadoresJson);
        } catch (ClassNotFoundException | SQLException | NumberFormatException ex) {
            request.setAttribute("aviso", TextoSeguro.paraScript(Mensagens.paraUsuario(ex)));
        }
      
        request.getRequestDispatcher("aluno/listar.jsp").forward(request, response);
    }
}
