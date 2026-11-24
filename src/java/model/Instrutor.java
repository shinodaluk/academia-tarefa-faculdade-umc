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
public class Instrutor {
    private int id;
    private String nome;
    private String cref;
    private String especialidade;
    private String telefone;
    private String email;
    private String status;
    private Date criado_em;

    private int fichasVinculadas;

    public Instrutor() {
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCref() { return cref; }
    public String getEspecialidade() { return especialidade; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public Date getCriadoEm() { return criado_em; }
    public int getFichasVinculadas() { return fichasVinculadas; }

    public static InstrutorBuilder getBuilder() {
        return new InstrutorBuilder();
    }

    public static class InstrutorBuilder {

        Instrutor inst = new Instrutor();

        public InstrutorBuilder comId (int id){
            inst.id = id;
            return this;
        }

        public InstrutorBuilder comNome(String nome){
            inst.nome = nome;
            return this;
        }

        public InstrutorBuilder comCref(String cref){
            inst.cref = cref;
            return this;
        }

        public InstrutorBuilder comEspecialidade(String especialidade){
            inst.especialidade = especialidade;
            return this;
        }

        public InstrutorBuilder doEspecialidadeMusculacao(){
            inst.especialidade = "Musculação";
            return this;
        }

        public InstrutorBuilder doEspecialidadeFuncional(){
            inst.especialidade = "Funcional";
            return this;
        }

        public InstrutorBuilder doEspecialidadeCondicionamento(){
            inst.especialidade = "Condicionamento";
            return this;
        }

        public InstrutorBuilder doEspecialidadePilates(){
            inst.especialidade = "Pilates";
            return this;
        }

        public InstrutorBuilder comTelefone(String telefone){
            inst.telefone = telefone;
            return this;
        }

        public InstrutorBuilder comEmail(String email){
            inst.email = email;
            return this;
        }

        public InstrutorBuilder comStatus(String status){
            inst.status = status;
            return this;
        }

        public InstrutorBuilder doStatusAtivo(){
            inst.status = "Ativo";
            return this;
        }

        public InstrutorBuilder doStatusInativo(){
            inst.status = "Inativo";
            return this;
        }

        public InstrutorBuilder comFichasVinculadas(int fichasVinculadas){
            inst.fichasVinculadas = fichasVinculadas;
            return this;
        }

        public InstrutorBuilder comCriadoEm(int dia, int mes, int ano){
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String data = dia + "/" + mes + "/" + ano;
            try{
                inst.criado_em = sdf.parse(data);
            } catch(Exception ex){
                System.out.println("Erro: " + ex.getMessage());
            }
            return this;
        }

        public Instrutor constroi(){
            return inst;
        }
    }
}
