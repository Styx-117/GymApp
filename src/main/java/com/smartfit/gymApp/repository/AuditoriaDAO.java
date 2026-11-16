package com.smartfit.gymApp.repository;

import com.smartfit.gymApp.config.ConexionBD;
import com.smartfit.gymApp.model.Auditoria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditoriaDAO {
    public record Registro(Auditoria auditoria, boolean activo) {}

    public static Connection conexion() throws SQLException {
        Connection c = ConexionBD.conectar();
        if (c == null) throw new SQLException("Conexion no disponible", "08001");
        return c;
    }

    /** Permite guardar la accion dentro de la misma transaccion del cambio de negocio. */
    public static Auditoria insertar(Connection c, int usuario, String accion, String detalle) throws SQLException {
        String sql = "INSERT INTO auditoria(id_usuario,accion,detalle) VALUES(?,?,?) RETURNING id_auditoria,fecha_hora";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            if (usuario > 0) ps.setInt(1, usuario); else ps.setNull(1, Types.INTEGER);
            ps.setString(2, accion);
            ps.setString(3, detalle == null ? "" : detalle.substring(0, Math.min(detalle.length(),255)));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return new Auditoria(rs.getInt(1),usuario,accion,rs.getTimestamp(2).toLocalDateTime(),detalle);
            }
        }
    }

    public Auditoria registrar(int usuario, String accion, String detalle) {
        try (Connection c = conexion()) { return insertar(c,usuario,accion,detalle); }
        catch (SQLException ex) { throw fallo(ex); }
    }

    public List<Registro> listar(boolean incluirArchivados) {
        List<Registro> registros = new ArrayList<>();
        String sql = "SELECT id_auditoria,id_usuario,accion,fecha_hora,detalle,activo FROM auditoria "
                + (incluirArchivados ? "" : "WHERE activo=TRUE ") + "ORDER BY fecha_hora,id_auditoria";
        try (Connection c=conexion(); PreparedStatement ps=c.prepareStatement(sql); ResultSet rs=ps.executeQuery()) {
            while(rs.next()) registros.add(new Registro(new Auditoria(rs.getInt("id_auditoria"),
                    rs.getInt("id_usuario"),rs.getString("accion"),rs.getTimestamp("fecha_hora").toLocalDateTime(),
                    rs.getString("detalle")),rs.getBoolean("activo")));
            return registros;
        } catch(SQLException ex) { throw fallo(ex); }
    }

    public boolean cambiarActivo(int id, boolean activo) {
        try(Connection c=conexion(); PreparedStatement ps=c.prepareStatement(
                "UPDATE auditoria SET activo=? WHERE id_auditoria=? AND activo<>?")) {
            ps.setBoolean(1,activo);ps.setInt(2,id);ps.setBoolean(3,activo);
            return ps.executeUpdate()==1;
        } catch(SQLException ex) {throw fallo(ex);}
    }

    private IllegalStateException fallo(SQLException ex) {
        return new IllegalStateException("No se pudo guardar o consultar el historial. Revisa la conexion a la base de datos.",ex);
    }
}
