/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Dinheiro;
import model.Plano;
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
public class PlanoDao {
    public static Connection getConexao() throws ClassNotFoundException, SQLException {
        return DBConnector.getConexaoMySQL();
    }

    public int cadastrar(Plano plano) throws ClassNotFoundException, SQLException, Exception {
        try (Connection con = getConexao()) {
            PreparedStatement comando = con.prepareStatement("INSERT INTO plano (nome, duracao_meses, valor_mensal, descricao, ativo) VALUES (?, ?, ?, ?, ?)", java.sql.Statement.RETURN_GENERATED_KEYS);
            comando.setString(1, plano.getNome());
            comando.setInt(2, plano.getDuracaoMeses());
            comando.setBigDecimal(3, plano.getValorMensal().valor());
            comando.setString(4, plano.getDescricao());
            comando.setBoolean(5, plano.isAtivo());
            comando.executeUpdate();

            ResultSet rs = comando.getGeneratedKeys();

            int newId = 0;
            try {
                if(rs.next())
                {
                    newId = rs.getInt(1);
                }

                return newId;
            } catch (Exception ex) {
                throw new Exception(ex.getMessage());
            }
        }
    }

    public void deletar(Plano plano) throws ClassNotFoundException, SQLException {
        try (Connection con = getConexao()) {
            PreparedStatement comando = con.prepareStatement("DELETE FROM plano WHERE id = ?");
            comando.setInt(1, plano.getId());
            comando.execute();
        }
    }

    public void atualizar(Plano plano) throws ClassNotFoundException, SQLException {
        try (Connection con = getConexao()) {
            PreparedStatement comando = con.prepareStatement("UPDATE plano SET nome = ?, duracao_meses = ?, valor_mensal = ?, descricao = ?, ativo = ? WHERE id = ?");
            comando.setString(1, plano.getNome());
            comando.setInt(2, plano.getDuracaoMeses());
            comando.setBigDecimal(3, plano.getValorMensal().valor());
            comando.setString(4, plano.getDescricao());
            comando.setBoolean(5, plano.isAtivo());
            comando.setInt(6, plano.getId());
            comando.execute();
        }
    }

    public Plano consultarById(Plano plano) throws Exception {
        try (Connection con = getConexao()) {
            PreparedStatement comando = con.prepareStatement("SELECT * FROM plano WHERE id = ?");
            comando.setInt(1, plano.getId());
            ResultSet rs = comando.executeQuery();
            if (rs.next()) {
                Plano p = Plano.getBuilder()
                    .comId(rs.getInt("id"))
                    .comNome(rs.getString("nome"))
                    .comDuracaoMeses(rs.getInt("duracao_meses"))
                    .comValorMensal(Dinheiro.de(rs.getBigDecimal("valor_mensal")))
                    .comDescricao(rs.getString("descricao"))
                    .comAtivo(rs.getBoolean("ativo"))
                    .constroi();
                return p;
            }

            throw new RegistroNaoEncontradoException("Plano não encontrado");
        }
    }

    public List<Plano> consultarTodos(boolean apenasAtivos) throws ClassNotFoundException, SQLException {
        try (Connection con = getConexao()) {
            String sql = """
                SELECT p.id, p.nome, p.duracao_meses, p.valor_mensal, p.descricao, p.ativo,
                       (SELECT COUNT(*) FROM matricula m WHERE m.plano_id = p.id) AS matriculas_vinculadas
                FROM plano p
                WHERE (? = FALSE OR p.ativo = TRUE)
                ORDER BY p.duracao_meses
            """;
            PreparedStatement comando = con.prepareStatement(sql);
            comando.setBoolean(1, apenasAtivos);
            ResultSet rs = comando.executeQuery();
            List<Plano> planos = new ArrayList<>();

            while(rs.next()){
                Plano p = Plano.getBuilder()
                    .comId(rs.getInt("id"))
                    .comNome(rs.getString("nome"))
                    .comDuracaoMeses(rs.getInt("duracao_meses"))
                    .comValorMensal(Dinheiro.de(rs.getBigDecimal("valor_mensal")))
                    .comDescricao(rs.getString("descricao"))
                    .comAtivo(rs.getBoolean("ativo"))
                    .comMatriculasVinculadas(rs.getInt("matriculas_vinculadas"))
                    .constroi();
                planos.add(p);
            }

            return planos;
        }
    }
}
