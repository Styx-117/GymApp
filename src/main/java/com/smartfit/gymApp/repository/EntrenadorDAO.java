package com.smartfit.gymApp.repository;

import com.smartfit.gymApp.config.ConexionBD;
import com.smartfit.gymApp.model.Entrenador;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@org.springframework.stereotype.Repository
public class EntrenadorDAO {
    /** Los selectores de clases siguen recibiendo únicamente entrenadores activos. */
    public List<Entrenador> listarEntrenadores() {
        return buscarEntrenadores("", true);
    }

    public List<Entrenador> obtenerEntrenadores() {
        return listarEntrenadores();
    }

    public List<Entrenador> buscarEntrenadores(String texto, Boolean activo) {
        String sql = "SELECT id_entrenador, nombre, apellido, especialidad, telefono, "
                + "COALESCE(activo, false) AS activo FROM entrenador WHERE "
                + "POSITION(? IN LOWER(CONCAT_WS(' ', id_entrenador::text, nombre, apellido, especialidad, telefono))) > 0"
                + (activo == null ? "" : " AND COALESCE(activo, false) = ?")
                + " ORDER BY id_entrenador";
        List<Entrenador> lista = new ArrayList<>();
        try (Connection conn = conexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setQueryTimeout(15);
            stmt.setString(1, texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT));
            if (activo != null) stmt.setBoolean(2, activo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(new Entrenador(rs.getInt("id_entrenador"),
                        rs.getString("nombre"), rs.getString("apellido"), rs.getString("especialidad"),
                        rs.getString("telefono"), rs.getBoolean("activo")));
            }
            return lista;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo consultar el directorio de entrenadores.", e);
        }
    }

    public boolean registrarEntrenador(String nombre, String apellido, String especialidad, String telefono) {
        String sql = "INSERT INTO entrenador (nombre, apellido, especialidad, telefono, activo) VALUES (?, ?, ?, ?, true)";
        try (Connection conn = conexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, apellido);
            stmt.setString(3, especialidad);
            stmt.setString(4, telefono);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("No se pudo registrar el entrenador. SQLSTATE=" + e.getSQLState());
            return false;
        }
    }

    public boolean actualizarEntrenador(int idEntrenador, String nombre, String apellido,
                                         String especialidad, String telefono) {
        String sql = "UPDATE entrenador SET nombre=?, apellido=?, especialidad=?, telefono=? WHERE id_entrenador=?";
        try (Connection conn = conexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, apellido);
            stmt.setString(3, especialidad);
            stmt.setString(4, telefono);
            stmt.setInt(5, idEntrenador);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("No se pudo actualizar el entrenador. SQLSTATE=" + e.getSQLState());
            return false;
        }
    }

    public boolean cambiarEstado(int idEntrenador, boolean activo) {
        try (Connection conn = conexion(); PreparedStatement stmt = conn.prepareStatement(
                "UPDATE entrenador SET activo=? WHERE id_entrenador=? AND COALESCE(activo, false) <> ?")) {
            stmt.setBoolean(1, activo);
            stmt.setInt(2, idEntrenador);
            stmt.setBoolean(3, activo);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("No se pudo cambiar el estado del entrenador. SQLSTATE=" + e.getSQLState());
            return false;
        }
    }

    public boolean eliminarEntrenador(int idEntrenador) {
        return cambiarEstado(idEntrenador, false);
    }


    public PageResult<Entrenador> buscarPagina(String texto,Boolean activo,int pagina,int tamanio) {
        if(pagina<0||tamanio<1||tamanio>200)throw new IllegalArgumentException("Pagina o cantidad de registros no valida.");
        String cols="id_entrenador,nombre,apellido,especialidad,telefono,activo";
        String sql="WITH filtrados AS (SELECT "+cols+" FROM entrenador WHERE "
            +"POSITION(? IN LOWER(CONCAT_WS(' ',id_entrenador::text,nombre,apellido,especialidad,telefono)))>0"
            +(activo==null?"":" AND COALESCE(activo,false)=?")
            +"), pagina AS (SELECT "+cols+" FROM filtrados ORDER BY id_entrenador LIMIT ? OFFSET ?) "
            +"SELECT p.id_entrenador,p.nombre,p.apellido,p.especialidad,p.telefono,p.activo,(SELECT count(*) FROM filtrados) AS total "
            +"FROM (VALUES(1)) ancla(n) LEFT JOIN pagina p ON true ORDER BY p.id_entrenador";
        try(Connection conn=conexion();PreparedStatement ps=conn.prepareStatement(sql)) {
            ps.setQueryTimeout(15);int i=1;ps.setString(i++,texto==null?"":texto.trim().toLowerCase(Locale.ROOT));if(activo!=null)ps.setBoolean(i++,activo);
            ps.setInt(i++,tamanio);ps.setLong(i,(long)pagina*tamanio);List<Entrenador> registros=new ArrayList<>();long total=0;
            try(ResultSet rs=ps.executeQuery()){while(rs.next()){total=rs.getLong("total");if(rs.getObject("id_entrenador")!=null)registros.add(new Entrenador(rs.getInt("id_entrenador"),rs.getString("nombre"),rs.getString("apellido"),rs.getString("especialidad"),rs.getString("telefono"),rs.getBoolean("activo")));}}
            return new PageResult<>(registros,total,pagina,tamanio);
        }catch(SQLException ex){throw new IllegalStateException("No se pudo consultar la pagina de entrenadores.",ex);}
    }

    private Connection conexion() throws SQLException {
        Connection conn = ConexionBD.conectar();
        if (conn == null) throw new SQLException("Conexión no disponible", "08001");
        return conn;
    }
}
