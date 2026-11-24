/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import dao.ServicoAdicionalDao;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Objects;

/**
 *
 * @author alunocmc
 */
public abstract class AdicionaisMatricula implements Servico{
    
    protected final Servico servicodecorator;
    private final ServicoAdicional adicional;

    protected AdicionaisMatricula(Servico servicodecorator, String codigoAdicional) {
        this.servicodecorator = Objects.requireNonNull(servicodecorator, "O serviço base é obrigatório.");
        try {
            this.adicional = new ServicoAdicionalDao().consultarPorCodigo(codigoAdicional);
        } catch (ClassNotFoundException | SQLException ex) {
            throw new IllegalStateException(
                    "Não foi possível carregar o adicional '" + codigoAdicional + "' do banco de dados.", ex);
        }
    }

    protected final String descricaoAdicional() {
        return adicional.getDescricao();
    }

    protected final double custoComAdicional() {
        return BigDecimal.valueOf(servicodecorator.custo())
                .add(adicional.getValorMensal())
                .doubleValue();
    }
    
}
