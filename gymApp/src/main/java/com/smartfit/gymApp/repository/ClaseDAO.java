package com.smartfit.gymApp.repository;

import com.smartfit.gymApp.config.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClaseDAO {

    // Método para listar todas las clases con el nombre del entrenador
    public List<Object[]> listarClases() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT c.id_clase, c.nombre, e.nombre AS entrenador, c.dia_semana, c.hora_inicio, c.hora_fin, c.cupo_maximo, c.cupo_actual, c.id_entrenador " +
                    "FROM clase c LEFT JOIN entrenador e ON c.id_entrenador = e.id_entrenador";
        
        try (Connection conn = ConexionBD.conectar();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_clase"),
                    rs.getString("nombre"),
                    rs.getString("entrenador") != null ? rs.getString("entrenador") : "Sin asignar",
                    rs.getString("dia_semana"),
                    rs.getTime("hora_inicio"),
                    rs.getTime("hora_fin"),
                    rs.getInt("cupo_maximo"),
                    rs.getInt("cupo_actual"),
                    rs.getInt("id_entrenador")
                };
                lista.add(fila);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Método para registrar una nueva clase
    public boolean registrarClase(String nombre, int idEntrenador, String diaSemana, Time horaInicio, Time horaFin, int cupoMaximo) {
        String sql = "INSERT INTO clase (nombre, id_entrenador, dia_semana, hora_inicio, hora_fin, cupo_maximo, cupo_actual) VALUES (?, ?, ?, ?, ?, ?, 0)";
        
        try (Connection conn = ConexionBD.conectar();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombre);
            pstmt.setInt(2, idEntrenador);
            pstmt.setString(3, diaSemana);
            pstmt.setTime(4, horaInicio);
            pstmt.setTime(5, horaFin);
            pstmt.setInt(6, cupoMaximo);
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarClase(int idClase, String nombre, int idEntrenador, String diaSemana,
                                Time horaInicio, Time horaFin, int cupoMaximo) {
        String sql = "UPDATE clase SET nombre = ?, id_entrenador = ?, dia_semana = ?, hora_inicio = ?, "
                + "hora_fin = ?, cupo_maximo = ? WHERE id_clase = ? AND cupo_actual <= ?";

        try (Connection conn = ConexionBD.conectar();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.setInt(2, idEntrenador);
            pstmt.setString(3, diaSemana);
            pstmt.setTime(4, horaInicio);
            pstmt.setTime(5, horaFin);
            pstmt.setInt(6, cupoMaximo);
            pstmt.setInt(7, idClase);
            pstmt.setInt(8, cupoMaximo);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar la clase: " + e.getMessage());
            return false;
        }
    }

    // Método para eliminar una clase por su ID
    public boolean eliminarClase(int idClase) {
        String sql = "DELETE FROM clase WHERE id_clase = ?";
        
        try (Connection conn = ConexionBD.conectar();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idClase);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}