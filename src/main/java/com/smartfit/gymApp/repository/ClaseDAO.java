package com.smartfit.gymApp.repository;

import com.smartfit.gymApp.config.ConexionBD;
import com.smartfit.gymApp.model.Clase;
import java.sql.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.smartfit.gymApp.validation.GestionValidator;

public class ClaseDAO {
    public record Registro(Clase clase, String entrenador, boolean activo) {
        @Override public String toString() {
            return clase.getIdClase()+" · "+clase.getNombre()+" · "+clase.getDiaSemana()+" "+clase.getHoraInicio()
                    +(activo?"":" · Inactiva");
        }
    }
    public record EntrenadorOpcion(int id, String nombre) {
        @Override public String toString() { return id+" · "+nombre; }
    }
    public List<Registro> listarRegistros() {
        String sql="SELECT c.id_clase,c.nombre,c.id_entrenador,c.dia_semana,c.hora_inicio,c.hora_fin,c.cupo_maximo,c.cupo_actual,c.activo, concat_ws(' ',e.nombre,e.apellido) AS entrenador FROM clase c "
                +"LEFT JOIN entrenador e ON e.id_entrenador=c.id_entrenador ORDER BY c.id_clase";
        try (Connection conn=conexion(); PreparedStatement ps=conn.prepareStatement(sql); ResultSet rs=ps.executeQuery()) {
            List<Registro> lista=new ArrayList<>();
            while(rs.next()) lista.add(new Registro(mapear(rs),rs.getString("entrenador"),rs.getBoolean("activo")));
            return lista;
        } catch(SQLException ex) { throw fallo("cargar las clases",ex); }
    }
    /** Conserva las nueve columnas anteriores y agrega activo al final. */
    public List<Object[]> listarClases() {
        List<Object[]> filas=new ArrayList<>();
        for(Registro r:listarRegistros()) {
            Clase c=r.clase();
            filas.add(new Object[]{c.getIdClase(),c.getNombre(),r.entrenador(),c.getDiaSemana(),c.getHoraInicio(),
                    c.getHoraFin(),c.getCupoMaximo(),c.getCupoActual(),c.getIdEntrenador(),r.activo()});
        }
        return filas;
    }
    public List<EntrenadorOpcion> entrenadoresActivos() {
        try(Connection conn=conexion(); PreparedStatement ps=conn.prepareStatement(
                "SELECT id_entrenador,nombre,apellido FROM entrenador WHERE activo=true ORDER BY nombre,apellido");
                ResultSet rs=ps.executeQuery()) {
            List<EntrenadorOpcion> lista=new ArrayList<>();
            while(rs.next()) lista.add(new EntrenadorOpcion(rs.getInt(1),rs.getString(2)+" "+rs.getString(3)));
            return lista;
        } catch(SQLException ex) { throw fallo("cargar los entrenadores",ex); }
    }
    public boolean registrarClase(String nombre,int entrenador,String dia,Time inicio,Time fin,int cupo) {
        validar(nombre,entrenador,dia,inicio,fin,cupo);
        String sql="INSERT INTO clase(nombre,id_entrenador,dia_semana,hora_inicio,hora_fin,cupo_maximo,cupo_actual,activo) "
                +"SELECT ?,id_entrenador,?,?,?,?,0,true FROM entrenador WHERE id_entrenador=? AND activo=true";
        try(Connection conn=conexion(); PreparedStatement ps=conn.prepareStatement(sql)) {
            ps.setString(1,nombre.trim()); ps.setString(2,normalizarDia(dia)); ps.setTime(3,inicio);
            ps.setTime(4,fin); ps.setInt(5,cupo); ps.setInt(6,entrenador);
            return ps.executeUpdate()==1;
        } catch(SQLException ex) { throw fallo("registrar la clase",ex); }
    }
    public boolean actualizarClase(int id,String nombre,int entrenador,String dia,Time inicio,Time fin,int cupo) {
        validar(nombre,entrenador,dia,inicio,fin,cupo);
        // UPDATE bloquea la misma fila de clase usada por las transacciones de reservas.
        String sql="UPDATE clase SET nombre=?,id_entrenador=?,dia_semana=?,hora_inicio=?,hora_fin=?,cupo_maximo=? "
                +"WHERE id_clase=? AND cupo_actual<=? AND EXISTS "
                +"(SELECT 1 FROM entrenador WHERE id_entrenador=? AND activo=true)";
        try(Connection conn=conexion(); PreparedStatement ps=conn.prepareStatement(sql)) {
            ps.setString(1,nombre.trim()); ps.setInt(2,entrenador); ps.setString(3,normalizarDia(dia));
            ps.setTime(4,inicio); ps.setTime(5,fin); ps.setInt(6,cupo); ps.setInt(7,id);
            ps.setInt(8,cupo); ps.setInt(9,entrenador);
            return ps.executeUpdate()==1;
        } catch(SQLException ex) { throw fallo("actualizar la clase",ex); }
    }
    public boolean cambiarEstado(int id,boolean activo) {
        GestionValidator.id(id,"una clase");
        try(Connection conn=conexion(); PreparedStatement ps=conn.prepareStatement(
                "UPDATE clase SET activo=? WHERE id_clase=? AND activo<>?")) {
            ps.setBoolean(1,activo); ps.setInt(2,id); ps.setBoolean(3,activo);
            return ps.executeUpdate()==1;
        } catch(SQLException ex) { throw fallo("cambiar el estado de la clase",ex); }
    }
    /** Baja lógica: conserva la clase y todas sus reservas. */
    public boolean eliminarClase(int id) { return cambiarEstado(id,false); }
    static Clase mapear(ResultSet rs) throws SQLException {
        Time inicio=rs.getTime("hora_inicio"),fin=rs.getTime("hora_fin");
        return new Clase(rs.getInt("id_clase"),rs.getString("nombre"),rs.getInt("id_entrenador"),
                rs.getString("dia_semana"),inicio==null?null:inicio.toLocalTime(),fin==null?null:fin.toLocalTime(),
                rs.getInt("cupo_maximo"),rs.getInt("cupo_actual"));
    }
    static Connection conexion() throws SQLException {
        Connection conn=ConexionBD.conectar();
        if(conn==null) throw new SQLException("Conexión no disponible");
        return conn;
    }
    private static void validar(String nombre,int entrenador,String dia,Time inicio,Time fin,int cupo) {
        GestionValidator.clase(nombre,entrenador,dia,inicio,fin,cupo);
    }
    private static String normalizarDia(String dia) { return GestionValidator.dia(dia); }
    private static IllegalStateException fallo(String accion,SQLException ex) {
        return new IllegalStateException("No se pudo "+accion+". Revisa la conexión e inténtalo de nuevo.",ex);
    }
}
