/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory.matricula;

import model.Matricula;
import dao.MatriculaDao;
import factory.ICommand;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.Mensagens;

/**
 *
 * @author Shino
 */
public class SalvarAction implements ICommand {
    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        MatriculaDao matriculaDao = new MatriculaDao();
        String id = request.getParameter("id");
        boolean novo = id == null || id.isEmpty();

        try {
            Matricula.MatriculaBuilder builder = Matricula.getBuilder()
                    .comAlunoId(Integer.parseInt(request.getParameter("alunoId")))
                    .comPlanoId(Integer.parseInt(request.getParameter("planoId")))
                    .comDataInicio(request.getParameter("dataInicio"))
                    .comFidelidadeMeses(Integer.parseInt(request.getParameter("fidelidadeMeses")))
                    // checkbox desmarcado não é enviado no POST
                    .comDescontoEstudante(request.getParameter("descontoEstudante") != null)
                    .comDiaVencimento(Integer.parseInt(request.getParameter("diaVencimento")))
                    .comSituacao(request.getParameter("situacao"));

            if(novo) {
                int newId = matriculaDao.cadastrar(builder.constroi());
                id = String.valueOf(newId);
            } else {
                matriculaDao.atualizar(builder.comId(Integer.parseInt(id)).constroi());
            }

            request.setAttribute("redirect", request.getContextPath() + "/matricula?acao=editar&id=" + id);
            request.getRequestDispatcher("redirect.jsp").forward(request, response);
        } catch (Exception ex) {
            String erro = URLEncoder.encode(Mensagens.paraUsuario(ex), StandardCharsets.UTF_8);
            String volta = novo ? "acao=criar" : "acao=editar&id=" + id;
            request.setAttribute("redirect", request.getContextPath() + "/matricula?" + volta + "&erro=" + erro);
            request.getRequestDispatcher("redirect.jsp").forward(request, response);
        }
    }
}
