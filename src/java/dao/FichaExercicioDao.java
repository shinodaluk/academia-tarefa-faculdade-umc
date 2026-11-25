/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.FichaExercicio;
import java.util.List;
import util.DBConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author alunocmc
 */
public class FichaExercicioDao {
    public static Connection getConexao() throws ClassNotFoundException, SQLException {
        return DBConnector.getConexaoMySQL();
    }

    // recebe a conexão para rodar na mesma transação da ficha
    public void substituirTodos(Connection con, int fichaId, List<FichaExercicio> exercicios) throws SQLException {
        PreparedStatement apaga = con.prepareStatement("DELETE FROM ficha_exercicio WHERE ficha_id = ?");
        apaga.setInt(1, fichaId);
        apaga.execute();

        PreparedStatement comando = con.prepareStatement("INSERT INTO ficha_exercicio (ficha_id, ordem, nome, series, repeticoes, carga_kg) VALUES (?, ?, ?, ?, ?, ?)");
        for (FichaExercicio e : exercicios) {
            comando.setInt(1, fichaId);
            comando.setInt(2, e.getOrdem());
            comando.setString(3, e.getNome());
            comando.setInt(4, e.getSeries());
            comando.setString(5, e.getRepeticoes());
            comando.setString(6, e.getCargaKg());
            comando.addBatch();
        }
        comando.executeBatch();
    }

    public List<FichaExercicio> consultarTodos(int fichaId) throws ClassNotFoundException, SQLException {
        try (Connection con = getConexao()) {
            PreparedStatement comando = con.prepareStatement("SELECT id, ficha_id, ordem, nome, series, repeticoes, carga_kg FROM ficha_exercicio WHERE ficha_id = ? ORDER BY ordem");
            comando.setInt(1, fichaId);
            ResultSet rs = comando.executeQuery();
            List<FichaExercicio> exercicios = new ArrayList<>();

            while(rs.next()){
                FichaExercicio e = FichaExercicio.getBuilder()
                    .comId(rs.getInt("id"))
                    .comFichaId(rs.getInt("ficha_id"))
                    .comOrdem(rs.getInt("ordem"))
                    .comNome(rs.getString("nome"))
                    .comSeries(rs.getInt("series"))
                    .comRepeticoes(rs.getString("repeticoes"))
                    .comCargaKg(rs.getString("carga_kg"))
                    .constroi();
                exercicios.add(e);
            }

            return exercicios;
        }
    }
}
