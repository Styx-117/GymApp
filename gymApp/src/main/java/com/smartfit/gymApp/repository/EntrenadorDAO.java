package com.smartfit.gymApp.repository;

import com.smartfit.gymApp.config.ConexionBD;
import com.smartfit.gymApp.model.Entrenador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntrenadorDAO {

    public List<Entrenador> listarEntrenadores() {
        List<Entrenador> lista = new ArrayList<>();
        String sql = "SELECT id_entrenador, nombre, apellido, especialidad, telefono, activo FROM entrenador WHERE activo = true";

        try (Connection conn = ConexionBD.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Entrenador e = new Entrenador(
                    rs.getInt("id_entrenador"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("especialidad"),
                    rs.getString("telefono"),
                    rs.getBoolean("activo")
                );
                lista.add(e);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar entrenadores: " + e.getMessage());
        }
        return lista;
    }

    public List<Entrenador> obtenerEntrenadores() {
        return listarEntrenadores();
    }

    public boolean registrarEntrenador(String nombre, String apellido, String especialidad, String telefono) {
        String sql = "INSERT INTO entrenador (nombre, apellido, especialidad, telefono, activo) VALUES (?, ?, ?, ?, true)";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.setString(2, apellido);
            pstmt.setString(3, especialidad);
            pstmt.setString(4, telefono);

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al registrar entrenador: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarEntrenador(int idEntrenador) {
        String sql = "DELETE FROM entrenador WHERE id_entrenador = ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idEntrenador);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar entrenador: " + e.getMessage());
            return false;
        }
    }
}