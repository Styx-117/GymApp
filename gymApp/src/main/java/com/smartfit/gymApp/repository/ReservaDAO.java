package com.smartfit.gymApp.repository;

import com.smartfit.gymApp.model.Clase;
import com.smartfit.gymApp.model.ReservaClase;
import com.smartfit.gymApp.service.GestorReservasClase;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.smartfit.gymApp.validation.GestionValidator;

/** Cada escritura bloquea su clase y confirma estado, posiciones y cupo en una sola transacción. */
public class ReservaDAO {
    public record SocioOpcion(String codigo,String nombre,boolean activo) {
        @Override public String toString() { return codigo+" · "+nombre+(activo?"":" · Inactivo"); }
    }
    public record Fila(ReservaClase reserva,String socio,boolean socioActivo) {}
    public record Carga(Clase clase,boolean activa,List<Fila> filas,GestorReservasClase gestor) {}
    public record Cancelacion(ReservaClase cancelada,ReservaClase promovida) {}

    public List<SocioOpcion> sociosActivos() {
        try(Connection conn=ClaseDAO.conexion(); PreparedStatement ps=conn.prepareStatement(
                "SELECT codigo_socio,nombre,apellido,activo FROM socio WHERE activo=true ORDER BY nombre,apellido");
                ResultSet rs=ps.executeQuery()) {
            List<SocioOpcion> socios=new ArrayList<>();
            while(rs.next()) socios.add(new SocioOpcion(rs.getString(1),rs.getString(2)+" "+rs.getString(3),rs.getBoolean(4)));
            return socios;
        } catch(SQLException ex) { throw fallo(ex); }
    }

    public Carga cargarClase(int idClase) {
        GestionValidator.id(idClase,"una clase");
        try(Connection conn=ClaseDAO.conexion()) {
            conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            conn.setReadOnly(true);
            conn.setAutoCommit(false);
            try {
                Carga carga=cargar(conn,idClase,false);
                conn.commit();
                return carga;
            } catch(SQLException|RuntimeException ex) { conn.rollback(); throw ex; }
        } catch(SQLException ex) { throw fallo(ex); }
    }

    public ReservaClase reservar(int idClase,String codigo) {
        if(codigo==null || codigo.isBlank()) throw new IllegalArgumentException("Selecciona un socio activo.");
        return transaccion(idClase,List.of(codigo),(conn,carga,activos) -> {
            exigirActiva(carga);
            if(!activos.contains(codigo)) throw new IllegalArgumentException("El socio está inactivo o ya no existe.");
            GestorReservasClase g=carga.gestor();
            if(g.estaInscrito(codigo)) throw new IllegalArgumentException("El socio ya tiene una reserva vigente en esta clase.");
            // Un recién llegado nunca adelanta a un socio activo que ya está en espera.
            g.promoverDisponibles(activos::contains);
            ReservaClase reserva=g.reservar(0,codigo);
            try(PreparedStatement ps=conn.prepareStatement("INSERT INTO reserva_clase "
                    +"(id_clase,codigo_socio,fecha_reserva,estado,posicion_lista_espera) VALUES (?,?,?,?,?) RETURNING id_reserva")) {
                ps.setInt(1,idClase); ps.setString(2,codigo); ps.setDate(3,Date.valueOf(reserva.getFechaReserva()));
                ps.setString(4,reserva.getEstado()); ps.setInt(5,reserva.getPosicionListaEspera());
                try(ResultSet rs=ps.executeQuery()) { rs.next(); reserva.setIdReserva(rs.getInt(1)); }
            }
            persistir(conn,g);
            return reserva;
        });
    }

    public Cancelacion cancelar(int idClase,String codigo) {
        GestionValidator.codigoSocio(codigo);
        return transaccion(idClase,(conn,carga,activos) -> {
            GestorReservasClase g=carga.gestor();
            ReservaClase cancelada=g.cancelar(codigo,c -> carga.activa() && activos.contains(c));
            if(cancelada==null) return new Cancelacion(null,null);
            cancelada.setPosicionListaEspera(-1);
            guardarReserva(conn,cancelada);
            if(carga.activa()) g.promoverDisponibles(activos::contains);
            persistir(conn,g);
            return new Cancelacion(cancelada,g.getUltimaPromovida());
        });
    }

    public boolean reasignar(int idClase,String codigoActual,String codigoNuevo) {
        GestionValidator.codigoSocio(codigoActual);GestionValidator.codigoSocio(codigoNuevo);
        return transaccion(idClase,List.of(codigoNuevo),(conn,carga,activos) -> {
            exigirActiva(carga);
            if(!activos.contains(codigoNuevo)) throw new IllegalArgumentException("Selecciona un socio activo para reasignar.");
            if(!carga.gestor().actualizarCodigoSocio(codigoActual,codigoNuevo)) return false;
            persistir(conn,carga.gestor());
            return true;
        });
    }

    public int confirmarDisponibles(int idClase) {
        return transaccion(idClase,(conn,carga,activos) -> {
            exigirActiva(carga);
            int anteriores=carga.gestor().getConfirmadas().tamanio();
            carga.gestor().promoverDisponibles(activos::contains);
            persistir(conn,carga.gestor());
            return carga.gestor().getConfirmadas().tamanio()-anteriores;
        });
    }

    private interface Operacion<T> { T ejecutar(Connection conn,Carga carga,Set<String> activos) throws SQLException; }
    private <T> T transaccion(int idClase,Operacion<T> operacion) {
        return transaccion(idClase,List.of(),operacion);
    }
    private <T> T transaccion(int idClase,List<String> destinos,Operacion<T> operacion) {
        GestionValidator.id(idClase,"una clase");
        try(Connection conn=ClaseDAO.conexion()) {
            conn.setAutoCommit(false);
            try {
                Carga carga=cargar(conn,idClase,true);
                // Solo bloquea los socios relevantes; no toda la tabla de socios.
                Set<String> activos=new HashSet<>();
                try(PreparedStatement ps=conn.prepareStatement("SELECT codigo_socio,activo FROM socio WHERE "
                        +"codigo_socio=ANY(?) OR codigo_socio IN (SELECT codigo_socio FROM reserva_clase "
                        +"WHERE id_clase=? AND estado IN ('CONFIRMADA','LISTA_ESPERA')) ORDER BY codigo_socio FOR SHARE")) {
                    Array codigos=conn.createArrayOf("varchar",destinos.toArray(String[]::new));
                    try {
                        ps.setArray(1,codigos);ps.setInt(2,idClase);
                        try(ResultSet rs=ps.executeQuery()){while(rs.next())if(rs.getBoolean(2))activos.add(rs.getString(1));}
                    } finally {codigos.free();}
                }
                T resultado=operacion.ejecutar(conn,carga,activos);
                conn.commit();
                return resultado;
            } catch(SQLException|RuntimeException ex) { conn.rollback(); throw ex; }
        } catch(SQLException ex) { throw fallo(ex); }
    }

    private Carga cargar(Connection conn,int idClase,boolean bloquear) throws SQLException {
        Clase clase;
        boolean activa;
        try(PreparedStatement ps=conn.prepareStatement("SELECT id_clase,nombre,id_entrenador,dia_semana,hora_inicio,hora_fin,cupo_maximo,cupo_actual,activo FROM clase WHERE id_clase=?"+(bloquear?" FOR UPDATE":""))) {
            ps.setInt(1,idClase);
            try(ResultSet rs=ps.executeQuery()) {
                if(!rs.next()) throw new IllegalArgumentException("La clase ya no existe. Actualiza los datos.");
                clase=ClaseDAO.mapear(rs); activa=rs.getBoolean("activo");
            }
        }
        List<Fila> filas=new ArrayList<>();
        try(PreparedStatement ps=conn.prepareStatement("SELECT r.id_reserva,r.id_clase,r.codigo_socio,r.fecha_reserva,r.estado,r.posicion_lista_espera,concat_ws(' ',s.nombre,s.apellido) AS socio,"
                +"s.activo AS socio_activo FROM reserva_clase r LEFT JOIN socio s ON s.codigo_socio=r.codigo_socio "
                +"WHERE r.id_clase=? ORDER BY r.id_reserva")) {
            ps.setInt(1,idClase);
            try(ResultSet rs=ps.executeQuery()) {
                while(rs.next()) {
                    Date fecha=rs.getDate("fecha_reserva");
                    ReservaClase r=new ReservaClase(rs.getInt("id_reserva"),idClase,rs.getString("codigo_socio"),
                            fecha==null?null:fecha.toLocalDate(),rs.getString("estado"),rs.getInt("posicion_lista_espera"));
                    filas.add(new Fila(r,rs.getString("socio"),rs.getBoolean("socio_activo")));
                }
            }
        }
        GestorReservasClase gestor=new GestorReservasClase(clase);
        gestor.restaurar(filas.stream().map(Fila::reserva).toList());
        return new Carga(clase,activa,List.copyOf(filas),gestor);
    }

    private void persistir(Connection conn,GestorReservasClase gestor) throws SQLException {
        List<ReservaClase> vigentes=new ArrayList<>();
        gestor.getConfirmadas().recorrer(vigentes::add);
        gestor.getListaEspera().recorrer(vigentes::add);
        for(ReservaClase r:vigentes) guardarReserva(conn,r);
        try(PreparedStatement ps=conn.prepareStatement("UPDATE clase SET cupo_actual=? WHERE id_clase=?")) {
            ps.setInt(1,gestor.getConfirmadas().tamanio()); ps.setInt(2,gestor.getClase().getIdClase()); ps.executeUpdate();
        }
    }
    private void guardarReserva(Connection conn,ReservaClase reserva) throws SQLException {
        try(PreparedStatement ps=conn.prepareStatement("UPDATE reserva_clase SET codigo_socio=?,estado=?,posicion_lista_espera=? "
                +"WHERE id_reserva=? AND id_clase=?")) {
            ps.setString(1,reserva.getCodigoSocio()); ps.setString(2,reserva.getEstado());
            ps.setInt(3,reserva.getPosicionListaEspera()); ps.setInt(4,reserva.getIdReserva()); ps.setInt(5,reserva.getIdClase());
            if(ps.executeUpdate()!=1) throw new SQLException("La reserva cambió durante la operación");
        }
    }
    private static void exigirActiva(Carga carga) {
        if(!carga.activa()) throw new IllegalArgumentException("La clase está inactiva. Sus reservas se conservan y pueden cancelarse.");
    }
    private static IllegalStateException fallo(SQLException ex) {
        return new IllegalStateException("No se pudo completar la operación de reservas. No se guardaron cambios parciales.",ex);
    }
}
