/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import com.google.gson.annotations.JsonAdapter;
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
    private SituacaoMatricula situacao;
    private Date criado_em;

    private String aluno;
    private String plano;
    private Dinheiro valorFinal;
    private int diasAtraso;
    
    private String descricao;
    private Dinheiro custoBase;

    public Matricula() {
        
    }

    public int getId() { return id; }
    public int getAlunoId() { return alunoId; }
    public int getPlanoId() { return planoId; }
    public Date getDataInicio() { return dataInicio; }
    public int getFidelidadeMeses() { return fidelidadeMeses; }
    public boolean isDescontoEstudante() { return descontoEstudante; }
    public int getDiaVencimento() { return diaVencimento; }
    public SituacaoMatricula getSituacao() { return situacao; }
    public Date getCriadoEm() { return criado_em; }
    public String getAluno() { return aluno; }
    public String getPlano() { return plano; }
    public Dinheiro getValorFinal() { return valorFinal; }
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
    public Dinheiro custo(){
        if (custoBase == null) {
            throw new IllegalStateException("Custo do plano não carregado; consulte a matrícula no banco de dados.");
        }
        return custoBase;
    }

    public static class MatriculaBuilder {

        Matricula matricula = new Matricula();

        public MatriculaBuilder comId(int id){
            matricula.id = id;
            return this;
        }

        public MatriculaBuilder comAlunoId(int alunoId){
            matricula.alunoId = alunoId;
            return this;
        }

        public MatriculaBuilder comPlanoId(int planoId){
            matricula.planoId = planoId;
            return this;
        }

        public MatriculaBuilder comDataInicio(String data){
            matricula.dataInicio = Datas.deIso(data);
            return this;
        }

        public MatriculaBuilder comFidelidadeMeses(int fidelidadeMeses){
            matricula.fidelidadeMeses = fidelidadeMeses;
            return this;
        }

        public MatriculaBuilder comDescontoEstudante(boolean descontoEstudante){
            matricula.descontoEstudante = descontoEstudante;
            return this;
        }

        public MatriculaBuilder doDescontoEstudante(){
            matricula.descontoEstudante = true;
            return this;
        }

        public MatriculaBuilder comDiaVencimento(int diaVencimento){
            matricula.diaVencimento = diaVencimento;
            return this;
        }

        public MatriculaBuilder comSituacao(String situacao){
            matricula.situacao = Rotulos.de(SituacaoMatricula.class, situacao);
            return this;
        }

        public MatriculaBuilder doSituacaoAtiva(){
            matricula.situacao = SituacaoMatricula.ATIVA;
            return this;
        }

        public MatriculaBuilder doSituacaoCancelada(){
            matricula.situacao = SituacaoMatricula.CANCELADA;
            return this;
        }

        public MatriculaBuilder doSituacaoSuspensa(){
            matricula.situacao = SituacaoMatricula.SUSPENSA;
            return this;
        }

        public MatriculaBuilder comAluno(String aluno){
            matricula.aluno = aluno;
            return this;
        }

        public MatriculaBuilder comPlano(String plano){
            matricula.plano = plano;
            return this;
        }

        public MatriculaBuilder comValorFinal(Dinheiro valorFinal){
            matricula.valorFinal = valorFinal;
            return this;
        }

        public MatriculaBuilder comDiasAtraso(int diasAtraso){
            matricula.diasAtraso = diasAtraso;
            return this;
        }

        public MatriculaBuilder comDescricao(String descricao){
            matricula.descricao = descricao;
            return this;
        }

        public MatriculaBuilder comCustoBase(Dinheiro custoBase){
            matricula.custoBase = custoBase;
            return this;
        }

        
        

        public Matricula constroi(){
            return matricula;
        }
    }
}
