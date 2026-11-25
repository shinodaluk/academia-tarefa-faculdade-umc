/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Dinheiro;
import model.Matricula;
import java.util.List;
import util.DBConnector;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

/**
 *
 * @author alunocmc
 */
public class MatriculaDao {
    public static Connection getConexao() throws ClassNotFoundException, SQLException {
        return DBConnector.getConexaoMySQL();
    }

    // grava a matrícula e a 1ª mensalidade, sempre no mês seguinte ao início
    public int cadastrar(Matricula matricula) throws ClassNotFoundException, SQLException {
        Connection con = getConexao();
        // matrícula sem mensalidade não pode sobrar se o 2º INSERT falhar
        con.setAutoCommit(false);
        try {
            PreparedStatement comando = con.prepareStatement("INSERT INTO matricula (aluno_id, plano_id, data_inicio, fidelidade_meses, desconto_estudante, dia_vencimento, situacao) VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            comando.setInt(1, matricula.getAlunoId());
            comando.setInt(2, matricula.getPlanoId());
            comando.setDate(3, new java.sql.Date(matricula.getDataInicio().getTime()));
            comando.setInt(4, matricula.getFidelidadeMeses());
            comando.setBoolean(5, matricula.isDescontoEstudante());
            comando.setInt(6, matricula.getDiaVencimento());
            comando.setString(7, matricula.getSituacao());
            comando.executeUpdate();

            ResultSet chaves = comando.getGeneratedKeys();
            chaves.next();
            int newId = chaves.getInt(1);

            PreparedStatement consultaPlano = con.prepareStatement("SELECT valor_mensal FROM plano WHERE id = ?");
            consultaPlano.setInt(1, matricula.getPlanoId());
            ResultSet rsPlano = consultaPlano.executeQuery();
            rsPlano.next();
            BigDecimal valor = calcularValorFinal(rsPlano.getBigDecimal("valor_mensal"),
                    matricula.getFidelidadeMeses(), matricula.isDescontoEstudante());

            // dia_vencimento vai até 28 (CHECK no banco), então atDay nunca estoura o mês
            LocalDate inicio = new java.sql.Date(matricula.getDataInicio().getTime()).toLocalDate();
            YearMonth competencia = YearMonth.from(inicio).plusMonths(1);
            LocalDate vencimento = competencia.atDay(matricula.getDiaVencimento());

            PreparedStatement mensalidade = con.prepareStatement("INSERT INTO mensalidade (matricula_id, competencia, data_vencimento, valor) VALUES (?, ?, ?, ?)");
            mensalidade.setInt(1, newId);
            mensalidade.setString(2, competencia.toString());
            mensalidade.setDate(3, java.sql.Date.valueOf(vencimento));
            mensalidade.setBigDecimal(4, valor);
            mensalidade.executeUpdate();

            con.commit();
            return newId;
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        } finally {
            con.close();
        }
    }

    public void deletar(Matricula matricula) throws ClassNotFoundException, SQLException {
        try (Connection con = getConexao()) {
            PreparedStatement comando = con.prepareStatement("DELETE FROM matricula WHERE id = ?");
            comando.setInt(1, matricula.getId());
            comando.execute();
        }
    }

    public void atualizar(Matricula matricula) throws ClassNotFoundException, SQLException {
        try (Connection con = getConexao()) {
            PreparedStatement comando = con.prepareStatement("UPDATE matricula SET aluno_id = ?, plano_id = ?, data_inicio = ?, fidelidade_meses = ?, desconto_estudante = ?, dia_vencimento = ?, situacao = ? WHERE id = ?");
            comando.setInt(1, matricula.getAlunoId());
            comando.setInt(2, matricula.getPlanoId());
            comando.setDate(3, new java.sql.Date(matricula.getDataInicio().getTime()));
            comando.setInt(4, matricula.getFidelidadeMeses());
            comando.setBoolean(5, matricula.isDescontoEstudante());
            comando.setInt(6, matricula.getDiaVencimento());
            comando.setString(7, matricula.getSituacao());
            comando.setInt(8, matricula.getId());
            comando.execute();
        }
    }

    public Matricula consultarById(Matricula matricula) throws Exception {
        try (Connection con = getConexao()) {
            PreparedStatement comando = con.prepareStatement(
                    "SELECT m.*, p.nome AS plano, p.valor_mensal "
                    + "FROM matricula m JOIN plano p ON p.id = m.plano_id WHERE m.id = ?");
            comando.setInt(1, matricula.getId());
            ResultSet rs = comando.executeQuery();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if (rs.next()) {
                BigDecimal valorFinal = calcularValorFinal(rs.getBigDecimal("valor_mensal"),
                        rs.getInt("fidelidade_meses"), rs.getBoolean("desconto_estudante"));
                Matricula m = Matricula.getBuilder()
                    .comId(rs.getInt("id"))
                    .comAlunoId(rs.getInt("aluno_id"))
                    .comPlanoId(rs.getInt("plano_id"))
                    .comDescricao(rs.getString("plano"))
                    .comCustoBase(Dinheiro.de(valorFinal))
                    .comValorFinal(Dinheiro.de(valorFinal))
                    .comDataInicio(sdf.format(rs.getDate("data_inicio")))
                    .comFidelidadeMeses(rs.getInt("fidelidade_meses"))
                    .comDescontoEstudante(rs.getBoolean("desconto_estudante"))
                    .comDiaVencimento(rs.getInt("dia_vencimento"))
                    .comSituacao(rs.getString("situacao"))
                    .constroi();
                return m;
            }

            throw new RegistroNaoEncontradoException("Matrícula não encontrada");
        }
    }

    public List<Matricula> consultarTodos(String situacao) throws ClassNotFoundException, SQLException {
        try (Connection con = getConexao()) {
            String sql = """
                SELECT m.id, a.nome AS aluno, p.nome AS plano, m.data_inicio, p.valor_mensal,
                       m.fidelidade_meses, m.desconto_estudante, m.dia_vencimento, m.situacao,
                       COALESCE((SELECT DATEDIFF(CURDATE(), MIN(me.data_vencimento)) FROM mensalidade me
                                  WHERE me.matricula_id = m.id AND me.data_pagamento IS NULL
                                    AND me.data_vencimento < CURDATE()), 0) AS dias_atraso
                FROM matricula m
                JOIN aluno a ON a.id = m.aluno_id
                JOIN plano p ON p.id = m.plano_id
                CROSS JOIN (SELECT ? AS situacao) AS prm
                WHERE (prm.situacao = '' OR m.situacao = prm.situacao)
                ORDER BY a.nome
            """;
            PreparedStatement comando = con.prepareStatement(sql);
            comando.setString(1, situacao == null ? "" : situacao);
            ResultSet rs = comando.executeQuery();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            List<Matricula> matriculas = new ArrayList<>();

            while(rs.next()){
                BigDecimal valorFinal = calcularValorFinal(rs.getBigDecimal("valor_mensal"),
                        rs.getInt("fidelidade_meses"), rs.getBoolean("desconto_estudante"));
                Matricula m = Matricula.getBuilder()
                    .comId(rs.getInt("id"))
                    .comAluno(rs.getString("aluno"))
                    .comPlano(rs.getString("plano"))
                    .comDescricao(rs.getString("plano"))
                    .comDataInicio(sdf.format(rs.getDate("data_inicio")))
                    .comValorFinal(Dinheiro.de(valorFinal))
                    .comCustoBase(Dinheiro.de(valorFinal))
                    .comDiaVencimento(rs.getInt("dia_vencimento"))
                    .comSituacao(rs.getString("situacao"))
                    .comDiasAtraso(rs.getInt("dias_atraso"))
                    .constroi();
                matriculas.add(m);
            }

            return matriculas;
        }
    }

    // fidelidade 6m = 5%, 12m = 10%; estudante = 15%; descontos somam sobre o valor base
    public BigDecimal calcularValorFinal(BigDecimal valorBase, int fidelidadeMeses, boolean descontoEstudante) {
        BigDecimal taxaFidelidade = fidelidadeMeses >= 12 ? new BigDecimal("0.10")
                                  : fidelidadeMeses >= 6  ? new BigDecimal("0.05")
                                  : BigDecimal.ZERO;
        BigDecimal taxaEstudante = descontoEstudante ? new BigDecimal("0.15") : BigDecimal.ZERO;
        BigDecimal fator = BigDecimal.ONE.subtract(taxaFidelidade).subtract(taxaEstudante);
        // HALF_UP = mesmo arredondamento do ROUND(x, 2) do MySQL em DECIMAL
        return valorBase.multiply(fator).setScale(2, RoundingMode.HALF_UP);
    }
}
