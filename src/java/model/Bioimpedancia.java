/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author alunocmc
 */
public class Bioimpedancia extends AdicionaisMatricula{
    
    public Bioimpedancia(Servico servicodecorator) {
        super(servicodecorator, "BIOIMPEDANCIA");
    }
    
    @Override
    public String getDescricao() {
        return servicodecorator.getDescricao() + ", " + descricaoAdicional();
    }

    @Override
    public double custo() {
        return custoComAdicional();
    }
    
}
