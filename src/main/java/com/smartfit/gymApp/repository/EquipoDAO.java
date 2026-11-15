package com.smartfit.gymApp.repository;

import com.smartfit.gymApp.model.Equipo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.smartfit.gymApp.validation.GestionValidator;

public class EquipoDAO {
    public List<Equipo> listar() {
        List<Equipo> equipos=new ArrayList<>();
        try(Connection c=AuditoriaDAO.conexion(); PreparedStatement ps=c.prepareStatement("SELECT codigo_equipo,nombre,tipo,ubicacion,fecha_adquisicion,estado FROM equipo ORDER BY codigo_equipo");ResultSet rs=ps.executeQuery()) {
            while(rs.next()) {
                Date fecha=rs.getDate("fecha_adquisicion");
                equipos.add(new Equipo(rs.getString("codigo_equipo"),rs.getString("nombre"),rs.getString("tipo"),
                        rs.getString("ubicacion"),fecha==null?null:fecha.toLocalDate(),rs.getString("estado")));
            }
            return equipos;
        } catch(SQLException ex){throw fallo(ex);}
    }

    public boolean guardar(Equipo equipo, boolean nuevo, int usuario) {
        GestionValidator.equipo(equipo);
        String sql=nuevo ? "INSERT INTO equipo(codigo_equipo,nombre,tipo,ubicacion,fecha_adquisicion,estado) VALUES(?,?,?,?,?,?)"
                : "UPDATE equipo SET nombre=?,tipo=?,ubicacion=?,fecha_adquisicion=?,estado=? WHERE codigo_equipo=?";
        try(Connection c=AuditoriaDAO.conexion()) {
            c.setAutoCommit(false);
            try(PreparedStatement ps=c.prepareStatement(sql)) {
                if(nuevo){ps.setString(1,equipo.getCodigoEquipo());ps.setString(2,equipo.getNombre());ps.setString(3,equipo.getTipo());
                    ps.setString(4,equipo.getUbicacion());ps.setObject(5,equipo.getFechaAdquisicion());ps.setString(6,equipo.getEstado());}
                else{ps.setString(1,equipo.getNombre());ps.setString(2,equipo.getTipo());ps.setString(3,equipo.getUbicacion());
                    ps.setObject(4,equipo.getFechaAdquisicion());ps.setString(5,equipo.getEstado());ps.setString(6,equipo.getCodigoEquipo());}
                if(ps.executeUpdate()!=1){c.rollback();return false;}
                AuditoriaDAO.insertar(c,usuario,nuevo?"REGISTRAR_EQUIPO":"ACTUALIZAR_EQUIPO",equipo.getCodigoEquipo()+" - "+equipo.getNombre());
                c.commit();return true;
            }catch(SQLException ex){c.rollback();throw ex;}
        }catch(SQLException ex){throw fallo(ex);}
    }

    public boolean cambiarEstado(String codigo,String estado,int usuario) {
        GestionValidator.requerido(codigo,20,"El código");GestionValidator.estadoEquipo(estado);
        try(Connection c=AuditoriaDAO.conexion()) {
            c.setAutoCommit(false);
            try(PreparedStatement ps=c.prepareStatement("UPDATE equipo SET estado=? WHERE codigo_equipo=? AND estado IS DISTINCT FROM ?")){
                ps.setString(1,estado);ps.setString(2,codigo);ps.setString(3,estado);
                if(ps.executeUpdate()!=1){c.rollback();return false;}
                AuditoriaDAO.insertar(c,usuario,"CAMBIAR_ESTADO_EQUIPO",codigo+" - "+estado);
                c.commit();return true;
            }catch(SQLException ex){c.rollback();throw ex;}
        }catch(SQLException ex){throw fallo(ex);}
    }

    private IllegalStateException fallo(SQLException ex){return new IllegalStateException(
            "No se pudo guardar o consultar el equipo. Revisa la conexion y que el codigo no este repetido.",ex);}
}
