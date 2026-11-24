/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.FichaTreino;
import java.util.List;
import util.DBConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 *
 * @author alunocmc
 */
public class FichaTreinoDao {
    public static Connection getConexao() throws ClassNotFoundException, SQLException {
        return DBConnector.getConexaoMySQL();
    }

    public int cadastrar(FichaTreino ficha) throws ClassNotFoundException, SQLException {
        Connection con = getConexao();
        // ficha e exercícios gravam juntos ou nada
        con.setAutoCommit(false);
        try {
            PreparedStatement comando = con.prepareStatement("INSERT INTO ficha_treino (aluno_id, instrutor_id, objetivo, data_criacao, observacoes) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            comando.setInt(1, ficha.getAlunoId());
            setInstrutor(comando, 2, ficha.getInstrutorId());
            comando.setString(3, ficha.getObjetivo());
            comando.setDate(4, new java.sql.Date(ficha.getDataCriacao().getTime()));
            comando.setString(5, ficha.getObservacoes());
            comando.executeUpdate();

            ResultSet chaves = comando.getGeneratedKeys();
            chaves.next();
            int newId = chaves.getInt(1);

            new FichaExercicioDao().substituirTodos(con, newId, ficha.getExercicios());

            con.commit();
            return newId;
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        } finally {
            con.close();
        }
    }

    public void deletar(FichaTreino ficha) throws ClassNotFoundException, SQLException {
        Connection con = getConexao();
        PreparedStatement comando = con.prepareStatement("DELETE FROM ficha_treino WHERE id = ?");
        comando.setInt(1, ficha.getId());
        comando.execute();
        con.close();
    }

    // observacoes fica de fora: o form não tem o campo e apagaria o valor gravado
    public void atualizar(FichaTreino ficha) throws ClassNotFoundException, SQLException {
        Connection con = getConexao();
        con.setAutoCommit(false);
        try {
            PreparedStatement comando = con.prepareStatement("UPDATE ficha_treino SET aluno_id = ?, instrutor_id = ?, objetivo = ?, data_criacao = ? WHERE id = ?");
            comando.setInt(1, ficha.getAlunoId());
            setInstrutor(comando, 2, ficha.getInstrutorId());
            comando.setString(3, ficha.getObjetivo());
            comando.setDate(4, new java.sql.Date(ficha.getDataCriacao().getTime()));
            comando.setInt(5, ficha.getId());
            comando.execute();

            new FichaExercicioDao().substituirTodos(con, ficha.getId(), ficha.getExercicios());

            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        } finally {
            con.close();
        }
    }

    public FichaTreino consultarById(FichaTreino ficha) throws Exception {
        Connection con = getConexao();
        PreparedStatement comando = con.prepareStatement("SELECT * FROM ficha_treino WHERE id = ?");
        comando.setInt(1, ficha.getId());
        ResultSet rs = comando.executeQuery();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (rs.next()) {
            // wasNull só vale logo após o getInt da coluna
            Integer instrutorId = rs.getInt("instrutor_id");
            if (rs.wasNull()) instrutorId = null;
            FichaTreino f = FichaTreino.getBuilder()
                .comId(rs.getInt("id"))
                .comAlunoId(rs.getInt("aluno_id"))
                .comInstrutorId(instrutorId)
                .comObjetivo(rs.getString("objetivo"))
                .comDataCriacao(sdf.format(rs.getDate("data_criacao")))
                .comObservacoes(rs.getString("observacoes"))
                .comExercicios(new FichaExercicioDao().consultarTodos(rs.getInt("id")))
                .constroi();
            con.close();
            return f;
        }
        con.close();

        throw new Exception("Ficha de treino não encontrada");
    }

    public List<FichaTreino> consultarTodos(String objetivo) throws ClassNotFoundException, SQLException {
        Connection con = getConexao();
        String sql = """
            SELECT f.id, a.nome AS aluno, f.objetivo,
                   COALESCE(i.nome, '— não vinculado') AS instrutor,
                   COUNT(e.id) AS qtd_exercicios, f.data_criacao
            FROM ficha_treino f
            JOIN aluno a          ON a.id = f.aluno_id
            LEFT JOIN instrutor i ON i.id = f.instrutor_id
            LEFT JOIN ficha_exercicio e ON e.ficha_id = f.id
            CROSS JOIN (SELECT ? AS objetivo) AS prm
            WHERE (prm.objetivo = '' OR f.objetivo = prm.objetivo)
            GROUP BY f.id, a.nome, f.objetivo, i.nome, f.data_criacao
            ORDER BY f.data_criacao DESC
        """;
        PreparedStatement comando = con.prepareStatement(sql);
        comando.setString(1, objetivo == null ? "" : objetivo);
        ResultSet rs = comando.executeQuery();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<FichaTreino> lFicha = new ArrayList<>();

        while(rs.next()){
            FichaTreino f = FichaTreino.getBuilder()
                .comId(rs.getInt("id"))
                .comAluno(rs.getString("aluno"))
                .comObjetivo(rs.getString("objetivo"))
                .comInstrutor(rs.getString("instrutor"))
                .comQtdExercicios(rs.getInt("qtd_exercicios"))
                .comDataCriacao(sdf.format(rs.getDate("data_criacao")))
                .constroi();
            lFicha.add(f);
        }

        con.close();
        return lFicha;
    }

    private void setInstrutor(PreparedStatement comando, int posicao, Integer instrutorId) throws SQLException {
        if (instrutorId == null) {
            comando.setNull(posicao, Types.INTEGER);
        } else {
            comando.setInt(posicao, instrutorId);
        }
    }
}
