package com.smartfit.gymApp.repository;

import com.smartfit.gymApp.config.ConexionBD;
import java.math.BigDecimal;
import java.sql.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Repository
public class DashboardDAO {
    public record Resumen(long[] cifras, BigDecimal cobros, List<Object[]> agenda, List<Object[]> mantenimiento) {}

    public Resumen cargar() throws SQLException {
        LocalDate hoy=LocalDate.now(ZoneId.of("America/Lima"));
        String dia=new String[]{"LUNES","MARTES","MIERCOLES","JUEVES","VIERNES","SABADO","DOMINGO"}[hoy.getDayOfWeek().getValue()-1];
        try(Connection c=ConexionBD.conectar()) {
            if(c==null)throw new SQLException("No se pudo conectar con la base de datos.");
            String sql="SELECT s.activos,s.inactivos,e.activos,e.inactivos,c.hoy,c.activas,"
                +"r.confirmadas,r.espera,i.pendientes,i.atendidas,m.proximas,m.vencidas,a.hoy,a.en_sala,p.total "
                +"FROM (SELECT count(*) FILTER(WHERE activo) activos,count(*) FILTER(WHERE NOT activo) inactivos FROM socio) s "
                +"CROSS JOIN (SELECT count(*) FILTER(WHERE activo) activos,count(*) FILTER(WHERE NOT activo) inactivos FROM entrenador) e "
                +"CROSS JOIN (SELECT count(*) FILTER(WHERE activo AND upper(translate(dia_semana,'ÁÉÍÓÚáéíóú','AEIOUaeiou'))=?) hoy,"
                +"count(*) FILTER(WHERE activo) activas FROM clase) c "
                +"CROSS JOIN (SELECT count(*) FILTER(WHERE estado='CONFIRMADA') confirmadas,count(*) FILTER(WHERE estado='LISTA_ESPERA') espera FROM reserva_clase) r "
                +"CROSS JOIN (SELECT count(*) FILTER(WHERE estado='PENDIENTE') pendientes,count(*) FILTER(WHERE estado='ATENDIDA') atendidas FROM incidencia) i "
                +"CROSS JOIN (SELECT count(*) FILTER(WHERE fecha_fin BETWEEN ? AND ?) proximas,count(*) FILTER(WHERE fecha_fin<?) vencidas FROM membresia) m "
                +"CROSS JOIN (SELECT count(*) FILTER(WHERE fecha=?) hoy,count(*) FILTER(WHERE fecha=? AND hora_entrada IS NOT NULL AND hora_salida IS NULL) en_sala FROM asistencia) a "
                +"CROSS JOIN (SELECT COALESCE(sum(monto) FILTER(WHERE fecha_pago BETWEEN ? AND ?),0) total FROM pago) p";
            long[] n=new long[14];BigDecimal cobros;
            try(PreparedStatement s=c.prepareStatement(sql)) {
                s.setQueryTimeout(15);s.setString(1,dia);s.setObject(2,hoy);s.setObject(3,hoy.plusDays(7));s.setObject(4,hoy);
                s.setObject(5,hoy);s.setObject(6,hoy);s.setObject(7,hoy.withDayOfMonth(1));s.setObject(8,hoy);
                try(ResultSet r=s.executeQuery()){r.next();for(int i=0;i<14;i++)n[i]=r.getLong(i+1);cobros=r.getBigDecimal(15);}
            }
            List<Object[]> agenda=new ArrayList<>(),mantenimiento=new ArrayList<>();
            try(PreparedStatement s=c.prepareStatement("SELECT c.nombre,c.dia_semana,to_char(c.hora_inicio,'HH24:MI'),"
                +"COALESCE(e.nombre||' '||e.apellido,'Sin asignar'),c.cupo_actual||' / '||c.cupo_maximo "
                +"FROM clase c LEFT JOIN entrenador e USING(id_entrenador) WHERE c.activo "
                +"ORDER BY CASE WHEN upper(translate(c.dia_semana,'ÁÉÍÓÚáéíóú','AEIOUaeiou'))=? THEN 0 ELSE 1 END,c.id_clase LIMIT 12")){
                s.setQueryTimeout(15);s.setString(1,dia);try(ResultSet r=s.executeQuery()){while(r.next())agenda.add(new Object[]{r.getString(1),r.getString(2),r.getString(3),r.getString(4),r.getString(5)});}
            }
            try(Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT e.nombre,i.descripcion,"
                +"CASE i.prioridad WHEN 1 THEN 'Alta' WHEN 2 THEN 'Media' ELSE 'Baja' END "
                +"FROM incidencia i LEFT JOIN equipo e USING(codigo_equipo) WHERE i.estado='PENDIENTE' ORDER BY i.prioridad,i.id_incidencia LIMIT 8")){
                while(r.next())mantenimiento.add(new Object[]{r.getString(1),r.getString(2),r.getString(3)});
            }
            return new Resumen(n,cobros,agenda,mantenimiento);
        }
    }
}
