package com.smartfit.gymApp.service;

import com.smartfit.gymApp.model.Auditoria;
import com.smartfit.gymApp.model.Equipo;
import com.smartfit.gymApp.repository.AuditoriaDAO;
import com.smartfit.gymApp.repository.ClaseDAO;
import com.smartfit.gymApp.repository.EquipoDAO;
import org.junit.jupiter.api.Test;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class GestionServicesTest {
    @Test void claseInvalidaNoLlegaAlDao() {
        ClaseFake dao=new ClaseFake();ClaseService servicio=new ClaseService(dao);
        assertThrows(IllegalArgumentException.class,()->servicio.registrar("Yoga",1,"LUNES",Time.valueOf("09:00:00"),Time.valueOf("08:00:00"),5));
        assertThrows(IllegalArgumentException.class,()->servicio.registrar("Yoga",1,"LUNES",Time.valueOf("07:00:00"),Time.valueOf("08:00:00"),0));
        assertThrows(IllegalArgumentException.class,()->servicio.cambiarEstado(0,false));
        assertEquals(0,dao.llamadas);
    }
    @Test void claseNormalizaElDiaYNombreEnElServicio() {
        ClaseFake dao=new ClaseFake();ClaseService servicio=new ClaseService(dao);
        assertTrue(servicio.registrar("  Yoga  ",1," miércoles ",Time.valueOf("07:00:00"),Time.valueOf("08:00:00"),5));
        assertEquals("Yoga",dao.nombre);assertEquals("MIERCOLES",dao.dia);assertEquals(1,dao.llamadas);
    }
    @Test void equipoInvalidoNoLlegaAlDao() {
        EquipoFake dao=new EquipoFake();EquipoService servicio=new EquipoService(dao);
        assertThrows(IllegalArgumentException.class,()->servicio.guardar(new Equipo("EQ-1","Banca","","",null,"DESCONOCIDO"),true));
        assertThrows(IllegalArgumentException.class,()->servicio.guardar(new Equipo("EQ-1","Banca","x".repeat(51),"",null,"OPERATIVO"),true));
        assertThrows(IllegalArgumentException.class,()->servicio.cambiarEstado(null,"OPERATIVO"));
        assertEquals(0,dao.llamadas);
    }
    @Test void equipoNormalizaSinModificarElObjetoDeLaPantalla() {
        EquipoFake dao=new EquipoFake();EquipoService servicio=new EquipoService(dao);
        Equipo original=new Equipo(" eq-1 "," Banca "," Fuerza "," Sala ",null,"OPERATIVO");
        assertTrue(servicio.guardar(original,true));
        assertEquals("EQ-1",dao.guardado.getCodigoEquipo());assertEquals("Banca",dao.guardado.getNombre());
        assertEquals(" eq-1 ",original.getCodigoEquipo());assertEquals(1,dao.llamadas);
    }
    @Test void auditoriaComparteLecturaCompletaConLaPilaSinConsultarAlMostrar() {
        AuditFake dao=new AuditFake();AuditoriaService servicio=new AuditoriaService(dao);
        var datos=servicio.refrescarYListar();
        assertEquals(2,datos.size());assertEquals(1,servicio.total());assertEquals(1,dao.lecturas);
        assertEquals(1,servicio.archivarUltima().getIdAuditoria());
        assertEquals(0,servicio.total());assertEquals(2,dao.lecturas);
        assertEquals(2,servicio.instantanea().size());assertTrue(servicio.instantanea().stream().noneMatch(AuditoriaDAO.Registro::activo));
        assertEquals(2,dao.lecturas);
        assertTrue(servicio.restaurar(1));assertEquals(1,servicio.ultima().getIdAuditoria());assertEquals(3,dao.lecturas);
    }
    private static class ClaseFake extends ClaseDAO {
        int llamadas;String nombre,dia;
        @Override public boolean registrarClase(String n,int e,String d,Time i,Time f,int c){llamadas++;nombre=n;dia=d;return true;}
        @Override public boolean cambiarEstado(int id,boolean activo){llamadas++;return true;}
    }
    private static class EquipoFake extends EquipoDAO {
        int llamadas;Equipo guardado;
        @Override public boolean guardar(Equipo e,boolean nuevo,int usuario){llamadas++;guardado=e;return true;}
        @Override public boolean cambiarEstado(String codigo,String estado,int usuario){llamadas++;return true;}
    }
    private static class AuditFake extends AuditoriaDAO {
        int lecturas;List<Registro> datos=new ArrayList<>(List.of(
            new Registro(new Auditoria(1,1,"ALTA",LocalDateTime.of(2026,10,30,8,0),"Uno"),true),
            new Registro(new Auditoria(2,1,"NOTA",LocalDateTime.of(2026,10,30,9,0),"Dos"),false)));
        @Override public List<Registro> listar(boolean archivados){lecturas++;return List.copyOf(datos);}
        @Override public boolean cambiarActivo(int id,boolean activo){
            for(int n=0;n<datos.size();n++)if(datos.get(n).auditoria().getIdAuditoria()==id){datos.set(n,new Registro(datos.get(n).auditoria(),activo));return true;}
            return false;
        }
    }
}
