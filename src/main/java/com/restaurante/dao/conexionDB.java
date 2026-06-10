package com.restaurante.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conexionDB {

    private static final String URL      = "jdbc:postgresql://localhost:5432/restaurante_db";
    private static final String USER     = "postgres";
    private static final String PASSWORD = "1234"; // ← cambia por tu contraseña

    public static Connection getConexion() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver no encontrado: " + e.getMessage());
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}