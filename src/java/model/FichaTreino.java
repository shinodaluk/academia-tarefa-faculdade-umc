/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import com.google.gson.annotations.JsonAdapter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 *
 * @author mulle
 */
public class FichaTreino {
    private int id;
    private int alunoId;
    private Integer instrutorId; // coluna aceita NULL: instrutor excluído deixa a ficha sem responsável
    private String objetivo;
    @JsonAdapter(Aluno.DataIsoAdapter.class)
    private Date dataCriacao;
    private String observacoes;

    private String aluno;
    private String instrutor;
    private int qtdExercicios;
    private List<FichaExercicio> exercicios = new ArrayList<>();

    public FichaTreino() {
    }

    public int getId() { return id; }
    public int getAlunoId() { return alunoId; }
    public Integer getInstrutorId() { return instrutorId; }
    public String getObjetivo() { return objetivo; }
    public Date getDataCriacao() { return dataCriacao; }
    public String getObservacoes() { return observacoes; }
    public String getAluno() { return aluno; }
    public String getInstrutor() { return instrutor; }
    public int getQtdExercicios() { return qtdExercicios; }
    public List<FichaExercicio> getExercicios() { return exercicios; }

    public static FichaTreinoBuilder getBuilder() {
        return new FichaTreinoBuilder();
    }

    public static class FichaTreinoBuilder {

        FichaTreino ficTrei = new FichaTreino();

        public FichaTreinoBuilder comId(int id){
            ficTrei.id = id;
            return this;
        }

        public FichaTreinoBuilder comAlunoId(int alunoId){
            ficTrei.alunoId = alunoId;
            return this;
        }

        public FichaTreinoBuilder comInstrutorId(Integer instrutorId){
            ficTrei.instrutorId = instrutorId;
            return this;
        }

        public FichaTreinoBuilder comObjetivo(String objetivo){
            ficTrei.objetivo = objetivo;
            return this;
        }

        public FichaTreinoBuilder doObjetivoHipertrofia(){
            ficTrei.objetivo = "Hipertrofia";
            return this;
        }

        public FichaTreinoBuilder doObjetivoEmagrecimento(){
            ficTrei.objetivo = "Emagrecimento";
            return this;
        }

        public FichaTreinoBuilder doObjetivoCondicionamento(){
            ficTrei.objetivo = "Condicionamento";
            return this;
        }

        public FichaTreinoBuilder comDataCriacao(String data){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try{
                ficTrei.dataCriacao = sdf.parse(data);
            } catch(Exception ex){
                System.out.println("Erro: " + ex.getMessage());
            }
            return this;
        }

        public FichaTreinoBuilder comObservacoes(String observacoes){
            ficTrei.observacoes = observacoes;
            return this;
        }

        public FichaTreinoBuilder comAluno(String aluno){
            ficTrei.aluno = aluno;
            return this;
        }

        public FichaTreinoBuilder comInstrutor(String instrutor){
            ficTrei.instrutor = instrutor;
            return this;
        }

        public FichaTreinoBuilder comQtdExercicios(int qtdExercicios){
            ficTrei.qtdExercicios = qtdExercicios;
            return this;
        }

        public FichaTreinoBuilder comExercicios(List<FichaExercicio> exercicios){
            ficTrei.exercicios = exercicios;
            return this;
        }

        public FichaTreino constroi(){
            return ficTrei;
        }
    }
}
