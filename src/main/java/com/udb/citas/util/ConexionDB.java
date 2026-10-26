package com.udb.citas.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// todos los DAO piden la conexion aqui, si cambian la clave solo se toca esto
public class ConexionDB {

    private static final String URL = "jdbc:mysql://localhost:3306/citas_medicas"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/El_Salvador";
    private static final String USER = "root";
    private static final String PASS = "root";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se encontró el driver de MySQL", e);
        }
    }

    private ConexionDB() { }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
