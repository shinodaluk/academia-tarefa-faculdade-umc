/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Instrutor;
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
public class InstrutorDao {
    public static Connection getConexao() throws ClassNotFoundException, SQLException {
        return DBConnector.getConexaoMySQL();
    }

    public int cadastrar(Instrutor instrutor) throws ClassNotFoundException, SQLException, Exception {
        try (Connection con = getConexao()) {
            PreparedStatement comando = con.prepareStatement("INSERT INTO instrutor (nome, cref, especialidade, telefone, email, status) VALUES (?, ?, ?, ?, ?, ?)", java.sql.Statement.RETURN_GENERATED_KEYS);
            comando.setString(1, instrutor.getNome());
            comando.setString(2, instrutor.getCref());
            comando.setString(3, instrutor.getEspecialidade());
            comando.setString(4, instrutor.getTelefone());
            comando.setString(5, instrutor.getEmail());
            comando.setString(6, instrutor.getStatus());
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

    public void deletar(Instrutor instrutor) throws ClassNotFoundException, SQLException {
        try (Connection con = getConexao()) {
            PreparedStatement comando = con.prepareStatement("DELETE FROM instrutor WHERE id = ?");
            comando.setInt(1, instrutor.getId());
            comando.execute();
        }
    }

    public void atualizar(Instrutor instrutor) throws ClassNotFoundException, SQLException {
        try (Connection con = getConexao()) {
            PreparedStatement comando = con.prepareStatement("UPDATE instrutor SET nome = ?, cref = ?, especialidade = ?, telefone = ?, email = ?, status = ? WHERE id = ?");
            comando.setString(1, instrutor.getNome());
            comando.setString(2, instrutor.getCref());
            comando.setString(3, instrutor.getEspecialidade());
            comando.setString(4, instrutor.getTelefone());
            comando.setString(5, instrutor.getEmail());
            comando.setString(6, instrutor.getStatus());
            comando.setInt(7, instrutor.getId());
            comando.execute();
        }
    }

    public Instrutor consultarById(Instrutor instrutor) throws Exception {
        try (Connection con = getConexao()) {
            PreparedStatement comando = con.prepareStatement("SELECT * FROM instrutor WHERE id = ?");
            comando.setInt(1, instrutor.getId());
            ResultSet rs = comando.executeQuery();
            if (rs.next()) {
                Instrutor i = Instrutor.getBuilder()
                    .comId(rs.getInt("id"))
                    .comNome(rs.getString("nome"))
                    .comCref(rs.getString("cref"))
                    .comEspecialidade(rs.getString("especialidade"))
                    .comTelefone(rs.getString("telefone"))
                    .comEmail(rs.getString("email"))
                    .comStatus(rs.getString("status"))
                    .constroi();
                return i;
            }

            throw new RegistroNaoEncontradoException("Instrutor não encontrado");
        }
    }

    public List<Instrutor> consultarTodos(String especialidade, String status) throws ClassNotFoundException, SQLException {
        try (Connection con = getConexao()) {
            String sql = """
                SELECT i.id, i.nome, i.cref, i.especialidade, i.telefone, i.email, i.status,
                       (SELECT COUNT(*) FROM ficha_treino f WHERE f.instrutor_id = i.id) AS fichas_vinculadas
                FROM instrutor i
                CROSS JOIN (SELECT ? AS especialidade, ? AS status) AS prm
                WHERE (prm.especialidade = '' OR i.especialidade = prm.especialidade)
                  AND (prm.status = '' OR i.status = prm.status)
                ORDER BY i.nome
            """;
            PreparedStatement comando = con.prepareStatement(sql);
            comando.setString(1, especialidade == null ? "" : especialidade);
            comando.setString(2, status == null ? "" : status);
            ResultSet rs = comando.executeQuery();
            List<Instrutor> instrutores = new ArrayList<>();

            while(rs.next()){
                Instrutor i = Instrutor.getBuilder()
                    .comId(rs.getInt("id"))
                    .comNome(rs.getString("nome"))
                    .comCref(rs.getString("cref"))
                    .comEspecialidade(rs.getString("especialidade"))
                    .comTelefone(rs.getString("telefone"))
                    .comEmail(rs.getString("email"))
                    .comStatus(rs.getString("status"))
                    .comFichasVinculadas(rs.getInt("fichas_vinculadas"))
                    .constroi();
                instrutores.add(i);
            }

            return instrutores;
        }
    }
}
