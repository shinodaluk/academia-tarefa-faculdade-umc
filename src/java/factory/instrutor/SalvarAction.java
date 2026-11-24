/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory.instrutor;

import model.Instrutor;
import dao.InstrutorDao;
import factory.ICommand;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Shino
 */
public class SalvarAction implements ICommand {
    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        InstrutorDao instrutorDao = new InstrutorDao();
        String id = request.getParameter("id");
        boolean novo = id == null || id.isEmpty();

        try {
            Instrutor.InstrutorBuilder builder = Instrutor.getBuilder()
                    .comNome(request.getParameter("nome"))
                    .comCref(request.getParameter("cref"))
                    .comEspecialidade(request.getParameter("especialidade"))
                    .comTelefone(request.getParameter("telefone"))
                    .comEmail(request.getParameter("email"))
                    .comStatus(request.getParameter("status"));

            if(novo) {
                int newId = instrutorDao.cadastrar(builder.constroi());
                id = String.valueOf(newId);
            } else {
                instrutorDao.atualizar(builder.comId(Integer.parseInt(id)).constroi());
            }

            request.setAttribute("redirect", request.getContextPath() + "/instrutor?acao=editar&id=" + id);
            request.getRequestDispatcher("redirect.jsp").forward(request, response);
        } catch (Exception ex) {
            String erro = URLEncoder.encode(String.valueOf(ex.getMessage()), StandardCharsets.UTF_8);
            String volta = novo ? "acao=criar" : "acao=editar&id=" + id;
            request.setAttribute("redirect", request.getContextPath() + "/instrutor?" + volta + "&erro=" + erro);
            request.getRequestDispatcher("redirect.jsp").forward(request, response);
        }
    }
}
