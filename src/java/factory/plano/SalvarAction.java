/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory.plano;

import model.Dinheiro;
import model.Plano;
import dao.PlanoDao;
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
        PlanoDao planoDao = new PlanoDao();
        String id = request.getParameter("id");
        boolean novo = id == null || id.isEmpty();

        try {
            Plano.PlanoBuilder builder = Plano.getBuilder()
                    .comNome(request.getParameter("nome"))
                    .comDuracaoMeses(Integer.parseInt(request.getParameter("duracaoMeses")))
                    .comValorMensal(Dinheiro.de(request.getParameter("valorMensal")))
                    .comDescricao(request.getParameter("descricao"))
                    // checkbox desmarcado não é enviado no POST
                    .comAtivo(request.getParameter("ativo") != null);

            if(novo) {
                int newId = planoDao.cadastrar(builder.constroi());
                id = String.valueOf(newId);
            } else {
                planoDao.atualizar(builder.comId(Integer.parseInt(id)).constroi());
            }

            request.setAttribute("redirect", request.getContextPath() + "/plano?acao=editar&id=" + id);
            request.getRequestDispatcher("redirect.jsp").forward(request, response);
        } catch (Exception ex) {
            String erro = URLEncoder.encode(Mensagens.paraUsuario(ex), StandardCharsets.UTF_8);
            String volta = novo ? "acao=criar" : "acao=editar&id=" + id;
            request.setAttribute("redirect", request.getContextPath() + "/plano?" + volta + "&erro=" + erro);
            request.getRequestDispatcher("redirect.jsp").forward(request, response);
        }
    }
}
