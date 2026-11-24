package factory.matricula;

import com.google.gson.Gson;
import dao.MatriculaDao;
import dao.ServicoAdicionalDao;
import factory.ICommand;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ServicoAdicional;

public class AdicionaisAction implements ICommand {
    private static final Set<String> CODIGOS_RECONHECIDOS = new HashSet<>(Arrays.asList(
            "BIOIMPEDANCIA", "CADEIRA_MASSAGEM", "PRE_TREINO"));

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Gson gson = new Gson();
        request.setAttribute("avisoJson", gson.toJson(""));

        try {
            request.setAttribute("matriculasJson", gson.toJson(new MatriculaDao().consultarTodos("")));
        } catch (Exception ex) {
            request.setAttribute("matriculasJson", gson.toJson(Collections.emptyList()));
            request.setAttribute("avisoJson", gson.toJson("Não foi possível carregar as matrículas."));
        }

        try {
            List<ServicoAdicional> servicos = new ServicoAdicionalDao().consultarTodos();
            servicos.removeIf(servico -> !CODIGOS_RECONHECIDOS.contains(servico.getCodigo()));
            request.setAttribute("servicosJson", gson.toJson(servicos));
        } catch (Exception ex) {
            request.setAttribute("servicosJson", gson.toJson(Collections.emptyList()));
            request.setAttribute("avisoJson", gson.toJson("Não foi possível carregar os serviços adicionais."));
        }

        request.getRequestDispatcher("matricula/adicionais.jsp").forward(request, response);
    }
}
