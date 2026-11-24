package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author alunocmc
 */
public class DBConnector {
    
    public static Connection getConexaoMySQL() throws ClassNotFoundException, SQLException {
        //MySQL
        // O método forName carrega e inicia o driver passado por parâmetro
        Class.forName("com.mysql.cj.jdbc.Driver");
        String URL = "jdbc:mysql://localhost:3307/academia";
        String USER = "root";
        String PASSWORD = "sistema_root_dev";
        // Estabelecendo a conexão
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
