package com.smartfit.gymApp.repository;

import com.smartfit.gymApp.model.Incidencia;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.smartfit.gymApp.validation.GestionValidator;

public class IncidenciaDAO {
    public List<Incidencia> listar() {
        List<Incidencia> resultado=new ArrayList<>();
        // La cola propia aplica la prioridad. Este orden conserva la llegada en los empates.
        try(Connection c=AuditoriaDAO.conexion(); PreparedStatement ps=c.prepareStatement(
                "SELECT id_incidencia,codigo_equipo,descripcion,prioridad,fecha_reporte,fecha_atencion,estado FROM incidencia ORDER BY CASE WHEN estado='ATENDIDA' THEN fecha_atencion ELSE fecha_reporte END,id_incidencia"); ResultSet rs=ps.executeQuery()){
            while(rs.next())resultado.add(leer(rs));
            return resultado;
        }catch(SQLException ex){throw fallo(ex);}
    }

    public Incidencia registrar(String equipo,String descripcion,int prioridad,int usuario) {
        GestionValidator.incidencia(equipo,descripcion,prioridad);
        try(Connection c=AuditoriaDAO.conexion()){
            c.setAutoCommit(false);
            try {
                validarEquipo(c,equipo,false);
                Incidencia creada;
                try(PreparedStatement ps=c.prepareStatement("INSERT INTO incidencia(codigo_equipo,descripcion,prioridad,fecha_reporte,estado) VALUES(?,?,?,CURRENT_DATE,'PENDIENTE') RETURNING id_incidencia,codigo_equipo,descripcion,prioridad,fecha_reporte,fecha_atencion,estado")){
                    ps.setString(1,equipo);ps.setString(2,descripcion);ps.setInt(3,prioridad);
                    try(ResultSet rs=ps.executeQuery()){rs.next();creada=leer(rs);}
                }
                AuditoriaDAO.insertar(c,usuario,"REGISTRAR_INCIDENCIA","#"+creada.getIdIncidencia()+" - "+equipo+" - prioridad "+prioridad);
                c.commit();return creada;
            }catch(SQLException|RuntimeException ex){c.rollback();throw ex;}
        }catch(SQLException ex){throw fallo(ex);}
    }

    public boolean actualizar(int id,String equipo,String descripcion,int prioridad,int usuario) {
        GestionValidator.id(id,"una incidencia");GestionValidator.incidencia(equipo,descripcion,prioridad);
        try(Connection c=AuditoriaDAO.conexion()){
            c.setAutoCommit(false);
            try {
                String equipoActual;
                try(PreparedStatement actual=c.prepareStatement("SELECT codigo_equipo FROM incidencia WHERE id_incidencia=? AND estado='PENDIENTE' FOR UPDATE")){
                    actual.setInt(1,id);
                    try(ResultSet rs=actual.executeQuery()){
                        if(!rs.next()){c.rollback();return false;}
                        equipoActual=rs.getString(1);
                    }
                }
                validarEquipo(c,equipo,equipo.equals(equipoActual));
                try(PreparedStatement ps=c.prepareStatement("UPDATE incidencia SET codigo_equipo=?,descripcion=?,prioridad=? WHERE id_incidencia=? AND estado='PENDIENTE'")){
                    ps.setString(1,equipo);ps.setString(2,descripcion);ps.setInt(3,prioridad);ps.setInt(4,id);
                    if(ps.executeUpdate()!=1){c.rollback();return false;}
                }
                AuditoriaDAO.insertar(c,usuario,"ACTUALIZAR_INCIDENCIA","#"+id+" - "+equipo+" - prioridad "+prioridad);
                c.commit();return true;
            }catch(SQLException|RuntimeException ex){c.rollback();throw ex;}
        }catch(SQLException ex){throw fallo(ex);}
    }

    public boolean cambiarEstado(int id,String origen,String destino,int usuario) {
        GestionValidator.id(id,"una incidencia");
        if(!List.of("PENDIENTE","ATENDIDA","CANCELADA").contains(destino))throw new IllegalArgumentException("Estado no valido.");
        try(Connection c=AuditoriaDAO.conexion()){
            c.setAutoCommit(false);
            try(PreparedStatement ps=c.prepareStatement("UPDATE incidencia SET estado=?,fecha_atencion=CASE WHEN ?='ATENDIDA' THEN CURRENT_DATE ELSE NULL END WHERE id_incidencia=? AND estado=?")){
                ps.setString(1,destino);ps.setString(2,destino);ps.setInt(3,id);ps.setString(4,origen);
                if(ps.executeUpdate()!=1){c.rollback();return false;}
                AuditoriaDAO.insertar(c,usuario,"CAMBIAR_ESTADO_INCIDENCIA","#"+id+" - "+origen+" a "+destino);
                c.commit();return true;
            }catch(SQLException ex){c.rollback();throw ex;}
        }catch(SQLException ex){throw fallo(ex);}
    }

    private void validarEquipo(Connection c,String codigo,boolean conservarEquipoActual)throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("SELECT estado FROM equipo WHERE codigo_equipo=? FOR SHARE")){
            ps.setString(1,codigo);
            try(ResultSet rs=ps.executeQuery()){
                if(!rs.next())throw new IllegalArgumentException("El equipo no existe. Selecciona un equipo registrado.");
                String estado=rs.getString(1);
                if(!conservarEquipoActual&&!List.of("OPERATIVO","EN_MANTENIMIENTO").contains(estado))
                    throw new IllegalArgumentException("El equipo esta fuera de servicio. Selecciona un equipo operativo o en mantenimiento.");
            }
        }
    }

    private Incidencia leer(ResultSet rs)throws SQLException {
        Date reporte=rs.getDate("fecha_reporte"),atencion=rs.getDate("fecha_atencion");
        return new Incidencia(rs.getInt("id_incidencia"),rs.getString("codigo_equipo"),rs.getString("descripcion"),
                rs.getInt("prioridad"),reporte==null?null:reporte.toLocalDate(),atencion==null?null:atencion.toLocalDate(),rs.getString("estado"));
    }

    private IllegalStateException fallo(SQLException ex){return new IllegalStateException(
            "No se pudo guardar o consultar la incidencia. Revisa la conexion a la base de datos.",ex);}
}
