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
import repositorio.IndicadoresAlunos;
import repositorio.Repositorio;
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
        return new Dependencias(
                new AlunoRepositorioJdbc(alunoDao),
                new PlanoRepositorioJdbc(new PlanoDao()),
                new InstrutorRepositorioJdbc(new InstrutorDao()),
                new MatriculaRepositorioJdbc(new MatriculaDao()),
                new FichaTreinoRepositorioJdbc(new FichaTreinoDao()),
                new AvaliacoesFisicasJdbc(new AvaliacaoFisicaDao()),
                alunoDao::alunosAtivos,
                new CatalogoAdicionaisJdbc(new ServicoAdicionalDao()));
    }
}
