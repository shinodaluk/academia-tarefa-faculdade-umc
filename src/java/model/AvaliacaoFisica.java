package model;

import com.google.gson.annotations.JsonAdapter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/** Avaliação física do aluno: relacionamento 1:1 (cada aluno tem no máximo uma). */
public class AvaliacaoFisica {
    private int id;
    private int alunoId;
    @JsonAdapter(Aluno.DataIsoAdapter.class)
    private Date dataAvaliacao;
    private BigDecimal pesoKg;
    private BigDecimal alturaM;
    private BigDecimal percentualGordura;
    private BigDecimal massaMagraKg;
    private BigDecimal cinturaCm;
    private String pressaoArterial;
    private Integer frequenciaCardiaca;
    private String observacoes;
    private Date atualizadoEm;

    private BigDecimal imc; // só para o JSON de saída; a fonte é getImc()

    public AvaliacaoFisica() {
    }

    public int getId() { return id; }
    public int getAlunoId() { return alunoId; }
    public Date getDataAvaliacao() { return dataAvaliacao; }
    public BigDecimal getPesoKg() { return pesoKg; }
    public BigDecimal getAlturaM() { return alturaM; }
    public BigDecimal getPercentualGordura() { return percentualGordura; }
    public BigDecimal getMassaMagraKg() { return massaMagraKg; }
    public BigDecimal getCinturaCm() { return cinturaCm; }
    public String getPressaoArterial() { return pressaoArterial; }
    public Integer getFrequenciaCardiaca() { return frequenciaCardiaca; }
    public String getObservacoes() { return observacoes; }
    public Date getAtualizadoEm() { return atualizadoEm; }

    /** peso / altura², uma casa decimal; null sem peso ou altura. */
    public BigDecimal getImc() {
        if (pesoKg == null || alturaM == null || alturaM.signum() <= 0) {
            return null;
        }
        return pesoKg.divide(alturaM.multiply(alturaM), 1, RoundingMode.HALF_UP);
    }

    public static AvaliacaoFisicaBuilder getBuilder() {
        return new AvaliacaoFisicaBuilder();
    }

    public static class AvaliacaoFisicaBuilder {

        AvaliacaoFisica avaliacao = new AvaliacaoFisica();

        public AvaliacaoFisicaBuilder comId(int id) {
            avaliacao.id = id;
            return this;
        }

        public AvaliacaoFisicaBuilder comAlunoId(int alunoId) {
            avaliacao.alunoId = alunoId;
            return this;
        }

        public AvaliacaoFisicaBuilder comDataAvaliacao(String data) {
            avaliacao.dataAvaliacao = Datas.deIso(data);
            return this;
        }

        public AvaliacaoFisicaBuilder comPesoKg(BigDecimal pesoKg) {
            avaliacao.pesoKg = pesoKg;
            return this;
        }

        public AvaliacaoFisicaBuilder comAlturaM(BigDecimal alturaM) {
            avaliacao.alturaM = alturaM;
            return this;
        }

        public AvaliacaoFisicaBuilder comPercentualGordura(BigDecimal percentualGordura) {
            avaliacao.percentualGordura = percentualGordura;
            return this;
        }

        public AvaliacaoFisicaBuilder comMassaMagraKg(BigDecimal massaMagraKg) {
            avaliacao.massaMagraKg = massaMagraKg;
            return this;
        }

        public AvaliacaoFisicaBuilder comCinturaCm(BigDecimal cinturaCm) {
            avaliacao.cinturaCm = cinturaCm;
            return this;
        }

        public AvaliacaoFisicaBuilder comPressaoArterial(String pressaoArterial) {
            avaliacao.pressaoArterial = pressaoArterial;
            return this;
        }

        public AvaliacaoFisicaBuilder comFrequenciaCardiaca(Integer frequenciaCardiaca) {
            avaliacao.frequenciaCardiaca = frequenciaCardiaca;
            return this;
        }

        public AvaliacaoFisicaBuilder comObservacoes(String observacoes) {
            avaliacao.observacoes = observacoes;
            return this;
        }

        public AvaliacaoFisicaBuilder comAtualizadoEm(Date atualizadoEm) {
            avaliacao.atualizadoEm = atualizadoEm;
            return this;
        }

        public AvaliacaoFisica constroi() {
            avaliacao.imc = avaliacao.getImc();
            return avaliacao;
        }
    }
}
