/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory.matricula;

import com.google.gson.Gson;
import dao.AlunoDao;
import dao.PlanoDao;
import factory.ICommand;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Matricula;
import org.apache.commons.text.StringEscapeUtils;

/**
 *
 * @author Shino
 */
public class CriarAction implements ICommand {
    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Matricula matricula = Matricula.getBuilder()
                .comFidelidadeMeses(0)
                .comDiaVencimento(5)
                .doSituacaoAtiva()
                .constroi();
        Gson gson = new Gson();

        request.setAttribute("matriculaJson", gson.toJson(matricula));
        request.setAttribute("alunosJson", gson.toJson(new AlunoDao().consultarTodos("", "")));
        request.setAttribute("planosJson", gson.toJson(new PlanoDao().consultarTodos(false)));
        request.setAttribute("aviso", StringEscapeUtils.escapeHtml4(request.getParameter("erro")));

        request.getRequestDispatcher("matricula/form.jsp").forward(request, response);
    }
}
