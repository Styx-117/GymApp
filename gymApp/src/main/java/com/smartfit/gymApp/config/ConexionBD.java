package com.smartfit.gymApp.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static final String URL = "jdbc:postgresql://dpg-dapf2tjm8hqs739j32tg-a.oregon-postgres.render.com:5432/gym_083s?sslmode=require";
    private static final String USER = "gym_083s_user";
    private static final String PASSWORD = "0uNGxFTkAhZD7q50dRuHFYk85bTtkKBg";

    public static Connection conectar() {
        Connection conexion = null;
        try {
            Class.forName("org.postgresql.Driver");
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("¡Conexión exitosa a la base de datos en Render!");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }
        return conexion;
    }
}