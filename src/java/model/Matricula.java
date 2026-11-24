/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import com.google.gson.annotations.JsonAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author mulle
 */
public class Matricula implements Servico {
    private int id;
    private int alunoId;
    private int planoId;
    @JsonAdapter(Aluno.DataIsoAdapter.class)
    private Date dataInicio;
    private int fidelidadeMeses;
    private boolean descontoEstudante;
    private int diaVencimento;
    private String situacao;
    private Date criado_em;

    private String aluno;
    private String plano;
    private double valorFinal;
    private int diasAtraso;
    
    private String descricao;
    private Double custoBase;

    public Matricula() {
        
    }

    public int getId() { return id; }
    public int getAlunoId() { return alunoId; }
    public int getPlanoId() { return planoId; }
    public Date getDataInicio() { return dataInicio; }
    public int getFidelidadeMeses() { return fidelidadeMeses; }
    public boolean isDescontoEstudante() { return descontoEstudante; }
    public int getDiaVencimento() { return diaVencimento; }
    public String getSituacao() { return situacao; }
    public Date getCriadoEm() { return criado_em; }
    public String getAluno() { return aluno; }
    public String getPlano() { return plano; }
    public double getValorFinal() { return valorFinal; }
    public int getDiasAtraso() { return diasAtraso; }

    public static MatriculaBuilder getBuilder() {
        return new MatriculaBuilder();
    }

    @Override
    public String getDescricao(){
        if (descricao == null) {
            throw new IllegalStateException("Descrição do plano não carregada; consulte a matrícula no banco de dados.");
        }
        return descricao;
    } 

    @Override
    public double custo(){
        if (custoBase == null) {
            throw new IllegalStateException("Custo do plano não carregado; consulte a matrícula no banco de dados.");
        }
        return custoBase;
    }

    public static class MatriculaBuilder {

        Matricula mat = new Matricula();

        public MatriculaBuilder comId(int id){
            mat.id = id;
            return this;
        }

        public MatriculaBuilder comAlunoId(int alunoId){
            mat.alunoId = alunoId;
            return this;
        }

        public MatriculaBuilder comPlanoId(int planoId){
            mat.planoId = planoId;
            return this;
        }

        public MatriculaBuilder comDataInicio(String data){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try{
                mat.dataInicio = sdf.parse(data);
            } catch(Exception ex){
                System.out.println("Erro: " + ex.getMessage());
            }
            return this;
        }

        public MatriculaBuilder comFidelidadeMeses(int fidelidadeMeses){
            mat.fidelidadeMeses = fidelidadeMeses;
            return this;
        }

        public MatriculaBuilder comDescontoEstudante(boolean descontoEstudante){
            mat.descontoEstudante = descontoEstudante;
            return this;
        }

        public MatriculaBuilder doDescontoEstudante(){
            mat.descontoEstudante = true;
            return this;
        }

        public MatriculaBuilder comDiaVencimento(int diaVencimento){
            mat.diaVencimento = diaVencimento;
            return this;
        }

        public MatriculaBuilder comSituacao(String situacao){
            mat.situacao = situacao;
            return this;
        }

        public MatriculaBuilder doSituacaoAtiva(){
            mat.situacao = "Ativa";
            return this;
        }

        public MatriculaBuilder doSituacaoCancelada(){
            mat.situacao = "Cancelada";
            return this;
        }

        public MatriculaBuilder doSituacaoSuspensa(){
            mat.situacao = "Suspensa";
            return this;
        }

        public MatriculaBuilder comAluno(String aluno){
            mat.aluno = aluno;
            return this;
        }

        public MatriculaBuilder comPlano(String plano){
            mat.plano = plano;
            return this;
        }

        public MatriculaBuilder comValorFinal(double valorFinal){
            mat.valorFinal = valorFinal;
            return this;
        }

        public MatriculaBuilder comDiasAtraso(int diasAtraso){
            mat.diasAtraso = diasAtraso;
            return this;
        }

        public MatriculaBuilder comDescricao(String descricao){
            mat.descricao = descricao;
            return this;
        }

        public MatriculaBuilder comCustoBase(double custoBase){
            mat.custoBase = custoBase;
            return this;
        }

        public MatriculaBuilder comCriadoEm(int dia, int mes, int ano){
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String data = dia + "/" + mes + "/" + ano;
            try{
                mat.criado_em = sdf.parse(data);
            } catch(Exception ex){
                System.out.println("Erro: " + ex.getMessage());
            }
            return this;
        }
        
        

        public Matricula constroi(){
            return mat;
        }
    }
}
