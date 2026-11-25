package apoio;

import config.Dependencias;
import dao.RegistroNaoEncontradoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Aluno;
import model.FichaTreino;
import model.Instrutor;
import model.Matricula;
import model.Plano;
import model.ServicoAdicional;
import repositorio.CatalogoAdicionais;

/** Todas as dependências das Actions em memória; cada teste configura só o que usa. */
public final class DependenciasFalsas {

    public final RepositorioFalso<Aluno> alunos = new RepositorioFalso<>();
    public final RepositorioFalso<Plano> planos = new RepositorioFalso<>();
    public final RepositorioFalso<Instrutor> instrutores = new RepositorioFalso<>();
    public final RepositorioFalso<Matricula> matriculas = new RepositorioFalso<>();
    public final RepositorioFalso<FichaTreino> fichas = new RepositorioFalso<>();
    public final Map<String, Number> indicadores = new HashMap<>();
    public final List<ServicoAdicional> adicionais = new ArrayList<>();

    public Dependencias criar() {
        CatalogoAdicionais catalogo = new CatalogoAdicionais() {
            @Override
            public List<ServicoAdicional> todos() {
                return new ArrayList<>(adicionais);
            }

            @Override
            public ServicoAdicional porCodigo(String codigo) throws RegistroNaoEncontradoException {
                return adicionais.stream().filter(a -> a.getCodigo().equals(codigo)).findFirst()
                        .orElseThrow(() -> new RegistroNaoEncontradoException("Adicional não cadastrado: " + codigo));
            }
        };
        return new Dependencias(alunos, planos, instrutores, matriculas, fichas, () -> indicadores, catalogo);
    }
}
