package com.smartfit.gymApp.repository;

import com.smartfit.gymApp.config.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SocioDAO {

    public List<String[]> obtenerClientes() {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT id_cliente, nombre, apellido, dni, telefono, correo FROM cliente ORDER BY id_cliente ASC";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String[] socio = {
                    String.valueOf(rs.getInt("id_cliente")),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("dni"),
                    rs.getString("telefono"),
                    rs.getString("correo")
                };
                lista.add(socio);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener los socios: " + e.getMessage());
        }
        return lista;
    }

    public boolean registrarCliente(String nombre, String apellido, String dni, String telefono, String correo) {
        String sql = "INSERT INTO cliente (nombre, apellido, dni, telefono, correo) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.setString(2, apellido);
            pstmt.setString(3, dni);
            pstmt.setString(4, telefono);
            pstmt.setString(5, correo);
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al registrar el socio: " + e.getMessage());
            return false;
        }
    }
    public boolean eliminarCliente(int idCliente) {
        String sql = "DELETE FROM cliente WHERE id_cliente = ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCliente);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar el socio: " + e.getMessage());
            return false;
        }
    }
}