/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author mulle
 */
public class Aluno {
    private int id;
    private String nome;
    private String cpf;
    @SerializedName("dataNascimento") // form.jsp lê modelo.dataNascimento
    @JsonAdapter(DataIsoAdapter.class)
    private Date data_nascimento;
    private String telefone; 
    private String email;
    private String status;
    private Date criado_em;
    private Date atualizado_em;
    
    private String planoAtual;
    
    public Aluno(){
    }
    
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public Date getDataNascimento() { return data_nascimento; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public Date getCriadoEm() { return criado_em; }
    public Date getAtualizadoEm() { return atualizado_em; }
    public String getPlanoAtual() { return planoAtual; }
    
    public static AlunoBuilder getBuilder(){
        return new AlunoBuilder();
    }
    
    public static class AlunoBuilder {
        
        Aluno alu = new Aluno();
        
        public AlunoBuilder comId (int id){
            alu.id = id;
            return this;
        }

        public AlunoBuilder comNome(String nome){
            alu.nome = nome;
            return this;
        }

        public AlunoBuilder comCpf(String cpf){
            alu.cpf = cpf;
            return this;
        }

        public AlunoBuilder comDataNascimento(String data){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try{
                alu.data_nascimento = sdf.parse(data);
            } catch(Exception ex){
                System.out.println("Erro: " + ex.getMessage());
            }
            return this;
        }

        public AlunoBuilder comTelefone(String telefone){
            alu.telefone = telefone;
            return this;
        }

        public AlunoBuilder comEmail(String email){
            alu.email = email;
            return this;
        }

        public AlunoBuilder comStatus(String status){
            alu.status = status;
            return this;
        }

        public AlunoBuilder doStatusAtivo(){
            alu.status = "Ativo";
            return this;
        }

        public AlunoBuilder doStatusInativo(){
            alu.status = "Inativo";
            return this;
        }

        public AlunoBuilder comPlanoAtual(String planoAtual){
            alu.planoAtual = planoAtual;
            return this;
        }

        public AlunoBuilder comCriadoEm(int dia, int mes, int ano){
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String data = dia + "/" + mes + "/" + ano;
            try{
                alu.criado_em = sdf.parse(data);
            } catch(Exception ex){
                System.out.println("Erro: " + ex.getMessage());
            }
            return this;
        }

        public AlunoBuilder comAtualizadoEm(int dia, int mes, int ano){
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String data = dia + "/" + mes + "/" + ano;
            try{
                alu.atualizado_em = sdf.parse(data);
            } catch(Exception ex){
                System.out.println("Erro: " + ex.getMessage());
            }
            return this;
        }

        public Aluno constroi(){
            return alu;
        }
    }

    public static class DataIsoAdapter extends TypeAdapter<Date> {
        private static final String FORMATO = "yyyy-MM-dd";

        @Override
        public void write(JsonWriter out, Date data) throws IOException {
            if (data == null) {
                out.nullValue();
                return;
            }
            out.value(new SimpleDateFormat(FORMATO).format(data));
        }

        @Override
        public Date read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                return new SimpleDateFormat(FORMATO).parse(in.nextString());
            } catch (ParseException ex) {
                throw new IOException(ex);
            }
        }
    }
}
