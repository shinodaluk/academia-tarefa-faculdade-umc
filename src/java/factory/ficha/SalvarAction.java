/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory.ficha;

import model.FichaExercicio;
import model.FichaTreino;
import dao.FichaTreinoDao;
import factory.ICommand;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Shino
 */
public class SalvarAction implements ICommand {
    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        FichaTreinoDao fichaDao = new FichaTreinoDao();
        String id = request.getParameter("id");
        boolean novo = id == null || id.isEmpty();

        try {
            String instrutorId = request.getParameter("instrutorId");
            String dataCriacao = request.getParameter("dataCriacao");

            FichaTreino.FichaTreinoBuilder builder = FichaTreino.getBuilder()
                    .comAlunoId(Integer.parseInt(request.getParameter("alunoId")))
                    .comInstrutorId(instrutorId == null || instrutorId.isEmpty() ? null : Integer.valueOf(instrutorId))
                    .comObjetivo(request.getParameter("objetivo"))
                    .comDataCriacao(dataCriacao == null || dataCriacao.isEmpty() ? LocalDate.now().toString() : dataCriacao)
                    .comExercicios(lerExercicios(request));

            if(novo) {
                int newId = fichaDao.cadastrar(builder.constroi());
                id = String.valueOf(newId);
            } else {
                fichaDao.atualizar(builder.comId(Integer.parseInt(id)).constroi());
            }

            request.setAttribute("redirect", request.getContextPath() + "/ficha?acao=editar&id=" + id);
            request.getRequestDispatcher("redirect.jsp").forward(request, response);
        } catch (Exception ex) {
            String erro = URLEncoder.encode(String.valueOf(ex.getMessage()), StandardCharsets.UTF_8);
            String volta = novo ? "acao=criar" : "acao=editar&id=" + id;
            request.setAttribute("redirect", request.getContextPath() + "/ficha?" + volta + "&erro=" + erro);
            request.getRequestDispatcher("redirect.jsp").forward(request, response);
        }
    }

    // o form manda exercicios[0].nome, exercicios[1].nome... com índices contíguos
    private List<FichaExercicio> lerExercicios(HttpServletRequest request) {
        List<FichaExercicio> exercicios = new ArrayList<>();
        for (int i = 0; request.getParameter("exercicios[" + i + "].nome") != null; i++) {
            String prefixo = "exercicios[" + i + "].";
            String carga = request.getParameter(prefixo + "cargaKg");
            exercicios.add(FichaExercicio.getBuilder()
                    .comOrdem(i + 1)
                    .comNome(request.getParameter(prefixo + "nome"))
                    .comSeries(Integer.parseInt(request.getParameter(prefixo + "series")))
                    .comRepeticoes(request.getParameter(prefixo + "repeticoes"))
                    .comCargaKg(carga == null || carga.isEmpty() ? null : carga)
                    .constroi());
        }
        return exercicios;
    }
}
