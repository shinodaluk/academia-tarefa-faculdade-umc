/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Date;
import java.util.List;

/**
 *
 * @author mulle
 */
public class Plano {
    private int id;
    private String nome;
    private int duracaoMeses;
    private Dinheiro valorMensal; // null: plano novo vai ao form com o campo vazio, não 0
    private String descricao;
    private boolean ativo;
    private Date criado_em;

    private int matriculasVinculadas;

    private transient Lazy<List<Matricula>> matriculas = Lazy.pronto(List.of());

    @FunctionalInterface
    public interface Relacionamentos {
        List<Matricula> matriculasDoPlano(int planoId) throws Exception;
    }

    public Plano() {
    }

    /** Liga as matrículas à fonte; a consulta só acontece no 1º getMatriculas(). */
    public void vincular(Relacionamentos fonte) {
        matriculas = Lazy.de(() -> fonte.matriculasDoPlano(id));
    }

    /** 1:n */
    public List<Matricula> getMatriculas() { return matriculas.obter(); }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public int getDuracaoMeses() { return duracaoMeses; }
    public Dinheiro getValorMensal() { return valorMensal; }
    public String getDescricao() { return descricao; }
    public boolean isAtivo() { return ativo; }
    public Date getCriadoEm() { return criado_em; }
    public int getMatriculasVinculadas() { return matriculasVinculadas; }

    public static PlanoBuilder getBuilder() {
        return new PlanoBuilder();
    }

    public static class PlanoBuilder {

        Plano plano = new Plano();

        public PlanoBuilder comId (int id){
            plano.id = id;
            return this;
        }

        public PlanoBuilder comNome(String nome){
            plano.nome = nome;
            return this;
        }

        public PlanoBuilder comDuracaoMeses (int duracaoMeses){
            plano.duracaoMeses = duracaoMeses;
            return this;
        }

        public PlanoBuilder comValorMensal (Dinheiro valorMensal){
            plano.valorMensal = valorMensal;
            return this;
        }

        public PlanoBuilder comDescricao(String descricao){
            plano.descricao = descricao;
            return this;
        }

        public PlanoBuilder comAtivo(boolean ativo){
            plano.ativo = ativo;
            return this;
        }

        public PlanoBuilder doAtivo(){
            plano.ativo = true;
            return this;
        }

        public PlanoBuilder doInativo(){
            plano.ativo = false;
            return this;
        }

        public PlanoBuilder comMatriculasVinculadas(int matriculasVinculadas){
            plano.matriculasVinculadas = matriculasVinculadas;
            return this;
        }


        public Plano constroi(){
            return plano;
        }
    }
}
