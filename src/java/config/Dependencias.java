package config;

import dao.AlunoDao;
import dao.AvaliacaoFisicaDao;
import dao.FichaTreinoDao;
import dao.InstrutorDao;
import dao.MatriculaDao;
import dao.PlanoDao;
import dao.ServicoAdicionalDao;
import model.Aluno;
import model.FichaTreino;
import model.Instrutor;
import model.Matricula;
import model.Plano;
import repositorio.AvaliacoesFisicas;
import repositorio.CatalogoAdicionais;
import repositorio.Filtros;
import repositorio.IndicadoresAlunos;
import repositorio.RelacionamentosAluno;
import repositorio.Repositorio;
import repositorio.RepositorioComRelacionamentos;
import repositorio.jdbc.AlunoRepositorioJdbc;
import repositorio.jdbc.AvaliacoesFisicasJdbc;
import repositorio.jdbc.CatalogoAdicionaisJdbc;
import repositorio.jdbc.FichaTreinoRepositorioJdbc;
import repositorio.jdbc.InstrutorRepositorioJdbc;
import repositorio.jdbc.MatriculaRepositorioJdbc;
import repositorio.jdbc.PlanoRepositorioJdbc;

/**
 * Ponto único de montagem (composition root): o único lugar que conhece as classes JDBC.
 * API e telas recebem estas dependências prontas; os testes passam versões em memória.
 */
public record Dependencias(
        Repositorio<Aluno> alunos,
        Repositorio<Plano> planos,
        Repositorio<Instrutor> instrutores,
        Repositorio<Matricula> matriculas,
        Repositorio<FichaTreino> fichas,
        AvaliacoesFisicas avaliacoes,
        IndicadoresAlunos indicadoresAlunos,
        CatalogoAdicionais adicionais) {

    public static Dependencias jdbc() {
        AlunoDao alunoDao = new AlunoDao();
        Repositorio<Matricula> matriculas = new MatriculaRepositorioJdbc(new MatriculaDao());
        Repositorio<FichaTreino> fichas = new FichaTreinoRepositorioJdbc(new FichaTreinoDao());
        AvaliacoesFisicas avaliacoes = new AvaliacoesFisicasJdbc(new AvaliacaoFisicaDao());

        // entidades lidas saem com os relacionamentos ligados, carregados só se acessados
        RelacionamentosAluno doAluno = new RelacionamentosAluno(matriculas, fichas, avaliacoes);
        return new Dependencias(
                new RepositorioComRelacionamentos<>(new AlunoRepositorioJdbc(alunoDao), doAluno::vincular),
                new RepositorioComRelacionamentos<>(new PlanoRepositorioJdbc(new PlanoDao()),
                        plano -> plano.vincular(id -> matriculas.listar(Filtros.de("planoId", String.valueOf(id))))),
                new RepositorioComRelacionamentos<>(new InstrutorRepositorioJdbc(new InstrutorDao()),
                        instrutor -> instrutor.vincular(
                                id -> fichas.listar(Filtros.de("instrutorId", String.valueOf(id))))),
                matriculas,
                fichas,
                avaliacoes,
                alunoDao::alunosAtivos,
                new CatalogoAdicionaisJdbc(new ServicoAdicionalDao()));
    }
}
