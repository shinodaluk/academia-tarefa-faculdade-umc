/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author mulle
 */
public class FichaExercicio {
    private int id;
    private int fichaId;
    private int ordem;
    private String nome;
    private int series;
    private String repeticoes;
    private String cargaKg;

    public FichaExercicio() {
    }

    public int getId() { return id; }
    public int getFichaId() { return fichaId; }
    public int getOrdem() { return ordem; }
    public String getNome() { return nome; }
    public int getSeries() { return series; }
    public String getRepeticoes() { return repeticoes; }
    public String getCargaKg() { return cargaKg; }

    public static FichaExercicioBuilder getBuilder() {
        return new FichaExercicioBuilder();
    }

    public static class FichaExercicioBuilder {

        FichaExercicio ficExer = new FichaExercicio();

        public FichaExercicioBuilder comId (int id){
            ficExer.id = id;
            return this;
        }

        public FichaExercicioBuilder comFichaId(int fichaId){
            ficExer.fichaId = fichaId;
            return this;
        }

        public FichaExercicioBuilder comOrdem(int ordem){
            ficExer.ordem = ordem;
            return this;
        }

        public FichaExercicioBuilder comNome(String nome){
            ficExer.nome = nome;
            return this;
        }

        public FichaExercicioBuilder comSeries(int series){
            ficExer.series = series;
            return this;
        }

        public FichaExercicioBuilder comRepeticoes(String repeticoes){
            ficExer.repeticoes = repeticoes;
            return this;
        }

        public FichaExercicioBuilder comCargaKg(String cargaKg){
            ficExer.cargaKg = cargaKg;
            return this;
        }

        public FichaExercicio constroi(){
            return ficExer;
        }
    }
}
