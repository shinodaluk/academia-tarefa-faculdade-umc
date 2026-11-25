package factory.matricula;

import com.google.gson.Gson;
import config.Dependencias;
import factory.ICommand;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Matricula;
import model.MontadorAdicionais;
import model.ServicoAdicional;
import repositorio.CatalogoAdicionais;
import repositorio.Filtros;
import repositorio.Repositorio;

public class AdicionaisAction implements ICommand {

    private final Repositorio<Matricula> matriculas;
    private final CatalogoAdicionais adicionais;

    public AdicionaisAction(Dependencias dependencias) {
        this.matriculas = dependencias.matriculas();
        this.adicionais = dependencias.adicionais();
    }

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Gson gson = new Gson();
        request.setAttribute("avisoJson", gson.toJson(""));

        try {
            request.setAttribute("matriculasJson", gson.toJson(matriculas.listar(Filtros.de())));
        } catch (Exception ex) {
            request.setAttribute("matriculasJson", gson.toJson(Collections.emptyList()));
            request.setAttribute("avisoJson", gson.toJson("Não foi possível carregar as matrículas."));
        }

        try {
            List<ServicoAdicional> servicos = adicionais.todos();
            // só os que têm decorator; um cadastro novo no banco sem classe não aparece na tela
            servicos.removeIf(servico -> !MontadorAdicionais.reconhece(servico.getCodigo()));
            request.setAttribute("servicosJson", gson.toJson(servicos));
        } catch (Exception ex) {
            request.setAttribute("servicosJson", gson.toJson(Collections.emptyList()));
            request.setAttribute("avisoJson", gson.toJson("Não foi possível carregar os serviços adicionais."));
        }

        request.getRequestDispatcher("matricula/adicionais.jsp").forward(request, response);
    }
}
