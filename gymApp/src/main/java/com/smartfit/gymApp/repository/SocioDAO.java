package com.smartfit.gymApp.repository;

import com.smartfit.gymApp.config.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SocioDAO {

    /**
     * Lista todos los socios ordenados por id. Devuelve un arreglo de String
     * que coincide con las columnas mostradas en la tabla del Swing:
     * [id, nombre, apellido, dni, telefono, correo]
     */
    public List<String[]> obtenerClientes() {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT id_socio, nombre, apellido, dni, telefono, correo " +
                     "FROM socio ORDER BY id_socio ASC";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new String[]{
                        String.valueOf(rs.getInt("id_socio")),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("dni"),
                        rs.getString("telefono"),
                        rs.getString("correo")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener socios: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Registra un socio nuevo.
     * IMPORTANTE: incluye "activo" y "fecha_registro" explícitamente porque
     * la tabla en Render fue creada por Hibernate con NOT NULL en esas columnas
     * y SIN DEFAULT. Enviar estos valores evita el error:
     *   "null value in column activo violates not-null constraint"
     */
    public boolean registrarCliente(String nombre, String apellido, String dni,
                                    String telefono, String correo) {

        String codigo = generarCodigoSocio();

        String sql = "INSERT INTO socio " +
                     "(codigo_socio, nombre, apellido, dni, telefono, correo, activo, fecha_registro) " +
                     "VALUES (?, ?, ?, ?, ?, ?, TRUE, CURRENT_TIMESTAMP)";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codigo);
            pstmt.setString(2, nombre);
            pstmt.setString(3, apellido);
            pstmt.setString(4, dni);
            pstmt.setString(5, telefono);
            pstmt.setString(6, correo);

            int filas = pstmt.executeUpdate();
            if (filas > 0) {
                System.out.println("✔ Socio registrado: " + codigo + " - " + nombre + " " + apellido);
            }
            return filas > 0;

        } catch (SQLException e) {
            System.err.println("Error al registrar socio: " + e.getMessage());
            return false;
        }
    }

    /**
     * Genera el siguiente código de socio: SM-0001, SM-0002, ...
     * Usa el MAX(id_socio)+1 para no chocar con UNIQUE.
     */
    private String generarCodigoSocio() {
        String sql = "SELECT COALESCE(MAX(id_socio), 0) + 1 AS next FROM socio";
        try (Connection conn = ConexionBD.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return String.format("SM-%04d", rs.getInt("next"));
            }
        } catch (SQLException e) {
            System.err.println("Error al generar código: " + e.getMessage());
        }
        return "SM-0001";
    }

    /**
     * Actualiza los datos personales de un socio (no toca activo ni fechas).
     */
    public boolean actualizarCliente(int idSocio, String nombre, String apellido,
                                     String dni, String telefono, String correo) {

        String sql = "UPDATE socio SET nombre=?, apellido=?, dni=?, telefono=?, correo=? " +
                     "WHERE id_socio=?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.setString(2, apellido);
            pstmt.setString(3, dni);
            pstmt.setString(4, telefono);
            pstmt.setString(5, correo);
            pstmt.setInt(6, idSocio);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar socio: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina físicamente un socio por su id.
     */
    public boolean eliminarCliente(int idSocio) {
        String sql = "DELETE FROM socio WHERE id_socio = ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idSocio);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar socio: " + e.getMessage());
            return false;
        }
    }
}