package com.smartfit.gymApp.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {

    private static final String URL;
    private static final String USUARIO;
    private static final String PASSWORD;

    static {
        Properties props = new Properties();
        try (InputStream is = ConexionBD.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (is != null) props.load(is);
        } catch (IOException e) {
            System.err.println("No se pudo leer application.properties: " + e.getMessage());
        }

        URL      = primeroNoVacio(System.getenv("GYMAPP_DB_URL"),
                                   props.getProperty("spring.datasource.url"));
        USUARIO  = primeroNoVacio(System.getenv("GYMAPP_DB_USER"),
                                   props.getProperty("spring.datasource.username"));
        PASSWORD = primeroNoVacio(System.getenv("GYMAPP_DB_PASSWORD"),
                                   props.getProperty("spring.datasource.password"));

        System.out.println("[ConexionBD] URL configurada: " + (URL != null ? "sí" : "NO"));
    }

    private static String primeroNoVacio(String... valores) {
        for (String v : valores) if (v != null && !v.isBlank()) return v;
        return null;
    }

    public static Connection conectar() {
        try {
            if (URL == null || USUARIO == null || PASSWORD == null) {
                System.err.println("Faltan credenciales de BD. Configure GYMAPP_DB_* o application.properties.");
                return null;
            }
            Class.forName("org.postgresql.Driver");
            Connection conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("✔ Conexión exitosa a la base de datos.");
            return conexion;
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("✘ Error al conectar: " + e.getMessage());
            return null;
        }
    }
}