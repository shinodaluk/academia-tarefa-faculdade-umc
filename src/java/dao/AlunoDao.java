/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Aluno;
import java.util.List;
import util.DBConnector;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;  
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author alunocmc
 */
public class AlunoDao {
    public static Connection getConexao() throws ClassNotFoundException, SQLException {
        return DBConnector.getConexaoMySQL();
    }
    
    public int cadastrar(Aluno aluno) throws ClassNotFoundException, SQLException, Exception {
        Connection con = getConexao();
        PreparedStatement comando = con.prepareStatement("INSERT INTO aluno (nome, cpf, data_nascimento, telefone, email, status) VALUES (?, ?, ?, ?, ?, ?)", java.sql.Statement.RETURN_GENERATED_KEYS);
        comando.setString(1, aluno.getNome());
        comando.setString(2, aluno.getCpf());
        comando.setDate(3, new java.sql.Date(aluno.getDataNascimento().getTime()));
        comando.setString(4, aluno.getTelefone());
        comando.setString(5, aluno.getEmail());
        comando.setString(6, aluno.getStatus());
        comando.executeUpdate();
        
        ResultSet rs = comando.getGeneratedKeys();
        
        int newId = 0;
        try {
            if(rs.next())
            {
                newId = rs.getInt(1);
            }

            con.close();
           return newId;
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }
    
    public void deletar(Aluno aluno) throws ClassNotFoundException, SQLException {
        Connection con = getConexao();
        PreparedStatement comando = con.prepareStatement("DELETE FROM aluno WHERE id = ?");
        comando.setInt(1, aluno.getId());
        comando.execute();
        con.close();
    }
    
    public void atualizar(Aluno aluno) throws ClassNotFoundException, SQLException {
        Connection con = getConexao();
        PreparedStatement comando = con.prepareStatement("UPDATE aluno SET nome = ?, cpf = ?, data_nascimento = ?, telefone = ?, email = ?, status = ? WHERE id = ?");
        comando.setString(1, aluno.getNome());
        comando.setString(2, aluno.getCpf());
        comando.setDate(3,  new java.sql.Date(aluno.getDataNascimento().getTime()));
        comando.setString(4, aluno.getTelefone());
        comando.setString(5, aluno.getEmail());
        comando.setString(6, aluno.getStatus());
        comando.setInt(7, aluno.getId());
        comando.execute();
        con.close();
    }    
    
    public Aluno consultarById(Aluno aluno) throws Exception {
        Connection con = getConexao();
        PreparedStatement comando = con.prepareStatement("SELECT * FROM aluno WHERE id = ?");
        comando.setInt(1, aluno.getId());
        ResultSet rs = comando.executeQuery();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (rs.next()) {
            return Aluno.getBuilder()
                .comId(rs.getInt("id"))
                .comNome(rs.getString("nome"))
                .comCpf(rs.getString("cpf"))
                .comTelefone(rs.getString("telefone"))
                .comDataNascimento(sdf.format(rs.getDate("data_nascimento")))
                .comEmail(rs.getString("email"))
                .comStatus(rs.getString("status"))
                .constroi();
        }       
        con.close();
        
        throw new Exception("Aluno não encontrado");
    }
    
    public List<Aluno> consultarTodos(String busca, String status) throws ClassNotFoundException, SQLException {
        Connection con = getConexao();
        String sql = """
            SELECT a.id, a.nome, a.cpf, a.telefone, a.email, a.status,
                   COALESCE(p.nome, '— sem matrícula') AS plano_atual
            FROM aluno a
            CROSS JOIN (SELECT ? AS busca, ? AS status) AS prm
            LEFT JOIN matricula m ON m.aluno_id = a.id AND m.situacao = 'Ativa'
            LEFT JOIN plano p     ON p.id = m.plano_id
            WHERE (prm.busca = '' OR a.nome LIKE CONCAT('%', prm.busca, '%')
                                  OR a.cpf  LIKE CONCAT('%', prm.busca, '%'))
              AND (prm.status = '' OR a.status = prm.status)
            ORDER BY a.nome
        """;
        PreparedStatement comando = con.prepareStatement(sql);
        comando.setString(1, busca == null ? "" : busca);
        comando.setString(2, status == null ? "" : status);
        ResultSet rs = comando.executeQuery();        
        List<Aluno> lAluno = new ArrayList<>();
        
        while(rs.next()){
            Aluno a = Aluno.getBuilder()
                .comId(rs.getInt("id"))
                .comNome(rs.getString("nome"))
                .comCpf(rs.getString("cpf"))
                .comTelefone(rs.getString("telefone"))
                .comEmail(rs.getString("email"))
                .comStatus(rs.getString("status"))
                .comPlanoAtual(rs.getString("plano_atual"))
                .constroi();
            lAluno.add(a);
        }
        
        con.close();
        return lAluno;
    }    
    
    public Map<String, Number> alunosAtivos() throws ClassNotFoundException, SQLException {
        Connection con = getConexao();
        
        String sql = """
            SELECT
            (SELECT COUNT(*) FROM aluno WHERE status = 'Ativo')       AS alunos_ativos,
            (SELECT COUNT(*) FROM matricula WHERE situacao = 'Ativa') AS matriculas,
            (SELECT COUNT(*) FROM mensalidade
               WHERE data_pagamento IS NULL AND data_vencimento < ?)  AS mensalidades_vencidas
        """;


        PreparedStatement comando = con.prepareStatement(sql);
        comando.setDate(1, new java.sql.Date(new Date().getTime()));
        ResultSet rs = comando.executeQuery();
        Map<String, Number> a = new HashMap<>();

        while(rs.next()){
            a.put("alunosAtivos", rs.getInt("alunos_ativos"));
            a.put("matriculas", rs.getInt("matriculas"));
            a.put("mensalidadesVencidas", rs.getInt("mensalidades_vencidas"));
        }

        // receita somada em Java para usar a mesma regra de desconto do MatriculaDao
        PreparedStatement ativas = con.prepareStatement("""
            SELECT p.valor_mensal, m.fidelidade_meses, m.desconto_estudante
            FROM matricula m
            JOIN plano p ON p.id = m.plano_id
            WHERE m.situacao = 'Ativa'
        """);
        ResultSet rsAtivas = ativas.executeQuery();
        MatriculaDao matriculaDao = new MatriculaDao();
        BigDecimal receita = BigDecimal.ZERO;
        while(rsAtivas.next()){
            receita = receita.add(matriculaDao.calcularValorFinal(rsAtivas.getBigDecimal("valor_mensal"),
                    rsAtivas.getInt("fidelidade_meses"), rsAtivas.getBoolean("desconto_estudante")));
        }
        a.put("receitaMensal", receita.doubleValue());

        con.close();
        return a;
    }
}
