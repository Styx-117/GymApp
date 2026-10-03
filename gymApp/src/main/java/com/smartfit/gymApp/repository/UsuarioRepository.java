
package com.smartfit.gymApp.repository;

import com.smartfit.gymApp.config.ConexionBD;
import com.smartfit.gymApp.model.Usuario;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepository {

    public boolean verificarCredenciales(String usuario, String password) {
        // Buscamos solo por nombre_usuario y contrasena
        String sql = "SELECT * FROM usuario WHERE nombre_usuario = ? AND contrasena = ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar credenciales: " + e.getMessage());
            return false;
        }
    }

    public List<Usuario> findAll() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario";

        try (Connection conn = ConexionBD.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getLong("id_usuario"));
                u.setNombre(rs.getString("nombre_usuario"));
                u.setPassword(rs.getString("contrasena"));
                if (hasColumn(rs, "estado")) {
                    u.setEstado(rs.getBoolean("estado"));
                }
                lista.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Error en findAll: " + e.getMessage());
        }
        return lista;
    }

    public Usuario save(Usuario u) {
        String sql = "INSERT INTO usuario (nombre_usuario, contrasena, estado) VALUES (?, ?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, u.getNombre());
            pstmt.setString(2, u.getPassword());
            pstmt.setBoolean(3, u.getEstado() != null ? u.getEstado() : true);
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    u.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en save: " + e.getMessage());
        }
        return u;
    }

    public Optional<Usuario> findById(Long id) {
        String sql = "SELECT * FROM usuario WHERE id_usuario = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getLong("id_usuario"));
                    u.setNombre(rs.getString("nombre_usuario"));
                    u.setPassword(rs.getString("contrasena"));
                    return Optional.of(u);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en findById: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Usuario> findByNombreUsuario(String nombreUsuario) {
        // Buscamos solo por nombre_usuario
        String sql = "SELECT * FROM usuario WHERE nombre_usuario = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreUsuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getLong("id_usuario"));
                    u.setNombre(rs.getString("nombre_usuario"));
                    u.setPassword(rs.getString("contrasena"));
                    return Optional.of(u);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en findByNombreUsuario: " + e.getMessage());
        }
        return Optional.empty();
    }

    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error en deleteById: " + e.getMessage());
        }
    }

    private boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}