/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author mulle
 */
public class Plano {
    private int id;
    private String nome;
    private int duracaoMeses;
    private Double valorMensal; // wrapper: plano novo vai ao form com o campo vazio, não 0
    private String descricao;
    private boolean ativo;
    private Date criado_em;

    private int matriculasVinculadas;

    public Plano() {
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public int getDuracaoMeses() { return duracaoMeses; }
    public Double getValorMensal() { return valorMensal; }
    public String getDescricao() { return descricao; }
    public boolean isAtivo() { return ativo; }
    public Date getCriadoEm() { return criado_em; }
    public int getMatriculasVinculadas() { return matriculasVinculadas; }

    public static PlanoBuilder getBuilder() {
        return new PlanoBuilder();
    }

    public static class PlanoBuilder {

        Plano pla = new Plano();

        public PlanoBuilder comId (int id){
            pla.id = id;
            return this;
        }

        public PlanoBuilder comNome(String nome){
            pla.nome = nome;
            return this;
        }

        public PlanoBuilder comDuracaoMeses (int duracaoMeses){
            pla.duracaoMeses = duracaoMeses;
            return this;
        }

        public PlanoBuilder comValorMensal (Double valorMensal){
            pla.valorMensal = valorMensal;
            return this;
        }

        public PlanoBuilder comDescricao(String descricao){
            pla.descricao = descricao;
            return this;
        }

        public PlanoBuilder comAtivo(boolean ativo){
            pla.ativo = ativo;
            return this;
        }

        public PlanoBuilder doAtivo(){
            pla.ativo = true;
            return this;
        }

        public PlanoBuilder doInativo(){
            pla.ativo = false;
            return this;
        }

        public PlanoBuilder comMatriculasVinculadas(int matriculasVinculadas){
            pla.matriculasVinculadas = matriculasVinculadas;
            return this;
        }

        public PlanoBuilder comCriadoEm(int dia, int mes, int ano){
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String data = dia + "/" + mes + "/" + ano;
            try{
                pla.criado_em = sdf.parse(data);
            } catch(Exception ex){
                System.out.println("Erro: " + ex.getMessage());
            }
            return this;
        }

        public Plano constroi(){
            return pla;
        }
    }
}
