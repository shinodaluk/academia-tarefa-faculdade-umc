package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Optional;
import model.AvaliacaoFisica;
import util.DBConnector;

public class AvaliacaoFisicaDao {

    public static Connection getConexao() throws ClassNotFoundException, SQLException {
        return DBConnector.getConexaoMySQL();
    }

    public Optional<AvaliacaoFisica> consultarPorAluno(int alunoId) throws Exception {
        try (Connection con = getConexao()) {
            PreparedStatement comando = con.prepareStatement("SELECT * FROM avaliacao_fisica WHERE aluno_id = ?");
            comando.setInt(1, alunoId);
            ResultSet rs = comando.executeQuery();
            if (!rs.next()) {
                return Optional.empty();
            }
            Integer frequencia = rs.getInt("frequencia_cardiaca");
            if (rs.wasNull()) frequencia = null;
            return Optional.of(AvaliacaoFisica.getBuilder()
                    .comId(rs.getInt("id"))
                    .comAlunoId(rs.getInt("aluno_id"))
                    .comDataAvaliacao(rs.getDate("data_avaliacao").toLocalDate().toString())
                    .comPesoKg(rs.getBigDecimal("peso_kg"))
                    .comAlturaM(rs.getBigDecimal("altura_m"))
                    .comPercentualGordura(rs.getBigDecimal("percentual_gordura"))
                    .comMassaMagraKg(rs.getBigDecimal("massa_magra_kg"))
                    .comCinturaCm(rs.getBigDecimal("cintura_cm"))
                    .comPressaoArterial(rs.getString("pressao_arterial"))
                    .comFrequenciaCardiaca(frequencia)
                    .comObservacoes(rs.getString("observacoes"))
                    .comAtualizadoEm(rs.getTimestamp("atualizado_em"))
                    .constroi());
        }
    }

    // aluno_id é UNIQUE: a segunda gravação do mesmo aluno vira UPDATE
    public void salvar(AvaliacaoFisica avaliacao) throws ClassNotFoundException, SQLException {
        try (Connection con = getConexao()) {
            PreparedStatement comando = con.prepareStatement("""
                INSERT INTO avaliacao_fisica (aluno_id, data_avaliacao, peso_kg, altura_m, percentual_gordura,
                    massa_magra_kg, cintura_cm, pressao_arterial, frequencia_cardiaca, observacoes)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    data_avaliacao = VALUES(data_avaliacao), peso_kg = VALUES(peso_kg),
                    altura_m = VALUES(altura_m), percentual_gordura = VALUES(percentual_gordura),
                    massa_magra_kg = VALUES(massa_magra_kg), cintura_cm = VALUES(cintura_cm),
                    pressao_arterial = VALUES(pressao_arterial),
                    frequencia_cardiaca = VALUES(frequencia_cardiaca), observacoes = VALUES(observacoes)
            """);
            comando.setInt(1, avaliacao.getAlunoId());
            comando.setDate(2, new java.sql.Date(avaliacao.getDataAvaliacao().getTime()));
            comando.setBigDecimal(3, avaliacao.getPesoKg());
            comando.setBigDecimal(4, avaliacao.getAlturaM());
            decimal(comando, 5, avaliacao.getPercentualGordura());
            decimal(comando, 6, avaliacao.getMassaMagraKg());
            decimal(comando, 7, avaliacao.getCinturaCm());
            comando.setString(8, avaliacao.getPressaoArterial());
            if (avaliacao.getFrequenciaCardiaca() == null) {
                comando.setNull(9, Types.INTEGER);
            } else {
                comando.setInt(9, avaliacao.getFrequenciaCardiaca());
            }
            comando.setString(10, avaliacao.getObservacoes());
            comando.executeUpdate();
        }
    }

    public void excluirPorAluno(int alunoId) throws ClassNotFoundException, SQLException {
        try (Connection con = getConexao()) {
            PreparedStatement comando = con.prepareStatement("DELETE FROM avaliacao_fisica WHERE aluno_id = ?");
            comando.setInt(1, alunoId);
            comando.executeUpdate();
        }
    }

    private static void decimal(PreparedStatement comando, int posicao, BigDecimal valor) throws SQLException {
        if (valor == null) {
            comando.setNull(posicao, Types.DECIMAL);
        } else {
            comando.setBigDecimal(posicao, valor);
        }
    }
}
