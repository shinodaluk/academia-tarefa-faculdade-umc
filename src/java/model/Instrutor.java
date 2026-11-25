/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Date;

/**
 *
 * @author mulle
 */
public class Instrutor {
    private int id;
    private String nome;
    private String cref;
    private Especialidade especialidade;
    private String telefone;
    private String email;
    private StatusCadastro status;
    private Date criado_em;

    private int fichasVinculadas;

    public Instrutor() {
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCref() { return cref; }
    public Especialidade getEspecialidade() { return especialidade; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }
    public StatusCadastro getStatus() { return status; }
    public Date getCriadoEm() { return criado_em; }
    public int getFichasVinculadas() { return fichasVinculadas; }

    public static InstrutorBuilder getBuilder() {
        return new InstrutorBuilder();
    }

    public static class InstrutorBuilder {

        Instrutor instrutor = new Instrutor();

        public InstrutorBuilder comId (int id){
            instrutor.id = id;
            return this;
        }

        public InstrutorBuilder comNome(String nome){
            instrutor.nome = nome;
            return this;
        }

        public InstrutorBuilder comCref(String cref){
            instrutor.cref = cref;
            return this;
        }

        public InstrutorBuilder comEspecialidade(String especialidade){
            instrutor.especialidade = Rotulos.de(Especialidade.class, especialidade);
            return this;
        }

        public InstrutorBuilder doEspecialidadeMusculacao(){
            instrutor.especialidade = Especialidade.MUSCULACAO;
            return this;
        }

        public InstrutorBuilder doEspecialidadeFuncional(){
            instrutor.especialidade = Especialidade.FUNCIONAL;
            return this;
        }

        public InstrutorBuilder doEspecialidadeCondicionamento(){
            instrutor.especialidade = Especialidade.CONDICIONAMENTO;
            return this;
        }

        public InstrutorBuilder doEspecialidadePilates(){
            instrutor.especialidade = Especialidade.PILATES;
            return this;
        }

        public InstrutorBuilder comTelefone(String telefone){
            instrutor.telefone = telefone;
            return this;
        }

        public InstrutorBuilder comEmail(String email){
            instrutor.email = email;
            return this;
        }

        public InstrutorBuilder comStatus(String status){
            instrutor.status = Rotulos.de(StatusCadastro.class, status);
            return this;
        }

        public InstrutorBuilder doStatusAtivo(){
            instrutor.status = StatusCadastro.ATIVO;
            return this;
        }

        public InstrutorBuilder doStatusInativo(){
            instrutor.status = StatusCadastro.INATIVO;
            return this;
        }

        public InstrutorBuilder comFichasVinculadas(int fichasVinculadas){
            instrutor.fichasVinculadas = fichasVinculadas;
            return this;
        }


        public Instrutor constroi(){
            return instrutor;
        }
    }
}
