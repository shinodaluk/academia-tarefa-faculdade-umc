/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author alunocmc
 */
public class CadeiraMassagem extends AdicionaisMatricula {
    
    public CadeiraMassagem(Servico servicodecorator, ServicoAdicional adicional) {
        super(servicodecorator, adicional, "CADEIRA_MASSAGEM");
    }
    
    @Override
    public String getDescricao() {
        return servicodecorator.getDescricao() + ", " + descricaoAdicional();
    }

    @Override
    public Dinheiro custo() {
        return custoComAdicional();
    }
    
}
