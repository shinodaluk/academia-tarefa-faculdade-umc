package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.ServicoAdicional;
import util.DBConnector;

public class ServicoAdicionalDao {
    public List<ServicoAdicional> consultarTodos() throws ClassNotFoundException, SQLException {
        String sql = "SELECT codigo, descricao, valor_mensal FROM servico_adicional ORDER BY codigo";
        List<ServicoAdicional> servicos = new ArrayList<>();

        try (Connection con = DBConnector.getConexaoMySQL();
             PreparedStatement comando = con.prepareStatement(sql);
             ResultSet rs = comando.executeQuery()) {
            while (rs.next()) {
                servicos.add(new ServicoAdicional(
                        rs.getString("codigo"),
                        rs.getString("descricao"),
                        rs.getBigDecimal("valor_mensal")));
            }
        }

        return servicos;
    }

    public ServicoAdicional consultarPorCodigo(String codigo) throws ClassNotFoundException, SQLException {
        String sql = "SELECT codigo, descricao, valor_mensal FROM servico_adicional WHERE codigo = ?";

        try (Connection con = DBConnector.getConexaoMySQL();
             PreparedStatement comando = con.prepareStatement(sql)) {
            comando.setString(1, codigo);

            try (ResultSet rs = comando.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Adicional não cadastrado: " + codigo);
                }

                return new ServicoAdicional(
                        rs.getString("codigo"),
                        rs.getString("descricao"),
                        rs.getBigDecimal("valor_mensal"));
            }
        }
    }
}
