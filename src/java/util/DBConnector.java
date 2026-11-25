package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Abre conexões com o MySQL; configuração por variável de ambiente, com padrão de desenvolvimento. */
public class DBConnector {

    private static final String URL = config("ACADEMIA_DB_URL", "jdbc:mysql://localhost:3307/academia");
    private static final String USUARIO = config("ACADEMIA_DB_USER", "root");
    private static final String SENHA = config("ACADEMIA_DB_PASSWORD", "sistema_root_dev");

    public static Connection getConexaoMySQL() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    private static String config(String nome, String padrao) {
        String valor = System.getenv(nome);
        return valor == null || valor.isBlank() ? padrao : valor;
    }
}
