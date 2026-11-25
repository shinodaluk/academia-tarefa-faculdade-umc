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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author mulle
 */
public class Aluno {
    private int id;
    private String nome;
    private Cpf cpf;
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
    public Cpf getCpf() { return cpf; }
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
        
        Aluno aluno = new Aluno();
        
        public AlunoBuilder comId (int id){
            aluno.id = id;
            return this;
        }

        public AlunoBuilder comNome(String nome){
            aluno.nome = nome;
            return this;
        }

        public AlunoBuilder comCpf(String cpf){
            aluno.cpf = cpf == null ? null : Cpf.de(cpf);
            return this;
        }

        public AlunoBuilder comDataNascimento(String data){
            aluno.data_nascimento = Datas.deIso(data);
            return this;
        }

        public AlunoBuilder comTelefone(String telefone){
            aluno.telefone = telefone;
            return this;
        }

        public AlunoBuilder comEmail(String email){
            aluno.email = email;
            return this;
        }

        public AlunoBuilder comStatus(String status){
            aluno.status = status;
            return this;
        }

        public AlunoBuilder doStatusAtivo(){
            aluno.status = "Ativo";
            return this;
        }

        public AlunoBuilder doStatusInativo(){
            aluno.status = "Inativo";
            return this;
        }

        public AlunoBuilder comPlanoAtual(String planoAtual){
            aluno.planoAtual = planoAtual;
            return this;
        }



        public Aluno constroi(){
            return aluno;
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
            // IllegalArgumentException sobe até a API, que responde 400 com o motivo
            return Datas.deIso(in.nextString());
        }
    }
}
