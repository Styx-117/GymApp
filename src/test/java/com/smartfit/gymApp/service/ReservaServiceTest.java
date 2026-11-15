package com.smartfit.gymApp.service;

import com.smartfit.gymApp.model.Clase;
import com.smartfit.gymApp.repository.ReservaDAO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReservaServiceTest {
    @Test void delegaValidacionAtomicaYNormalizaElCodigoSinConsultaPrevia() {
        var dao=new ReservaDAO(){
            @Override public Carga cargarClase(int id){throw new AssertionError("No debe validar fuera de la transacción");}
            @Override public com.smartfit.gymApp.model.ReservaClase reservar(int id,String codigo){
                assertEquals(4,id);assertEquals("SM-0001",codigo);
                throw new IllegalArgumentException("El socio ya tiene una reserva vigente en esta clase.");
            }
        };
        var service=new ReservaService(dao);var clase=clase(4);
        assertTrue(assertThrows(IllegalArgumentException.class,()->service.reservar(clase," sm-0001 "))
                .getMessage().contains("reserva vigente"));
    }
    @Test void entradasInvalidasYReasignacionAlMismoSocioNoEscriben(){
        var dao=new ReservaDAO(){
            @Override public com.smartfit.gymApp.model.ReservaClase reservar(int id,String c){throw new AssertionError("No debe escribir");}
            @Override public boolean reasignar(int id,String a,String n){throw new AssertionError("No debe escribir");}
        };
        var service=new ReservaService(dao);var clase=clase(4);
        assertThrows(IllegalArgumentException.class,()->service.reservar(null,"SM-0001"));
        assertThrows(IllegalArgumentException.class,()->service.reservar(clase," "));
        assertThrows(IllegalArgumentException.class,()->service.reservar(clase(0),"SM-0001"));
        assertFalse(service.actualizarCodigoSocio(4," sm-0001 ","SM-0001"));
    }
    private static Clase clase(int id){return new Clase(id,"Yoga",1,"LUNES",null,null,3,0);}
}
