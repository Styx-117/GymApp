package com.smartfit.gymApp.service;

import com.smartfit.gymApp.model.Clase;
import com.smartfit.gymApp.model.ReservaClase;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GestorReservasClaseTest {

    @Test
    void reconstruyeReservasGuardadasConSuIdentidadYOrdenDeEspera() {
        Clase clase=clase(1);
        clase.setCupoActual(99);
        GestorReservasClase gestor=new GestorReservasClase(clase);
        ReservaClase confirmada=reserva(10,"SM-0001","CONFIRMADA",-1);
        ReservaClase segunda=reserva(12,"SM-0003","LISTA_ESPERA",2);
        ReservaClase primera=reserva(11,"SM-0002","LISTA_ESPERA",1);
        ReservaClase cancelada=reserva(9,"SM-0004","CANCELADA",-1);
        gestor.restaurar(List.of(segunda,cancelada,confirmada,primera));

        assertEquals(1,clase.getCupoActual());
        assertSame(confirmada,gestor.getConfirmadas().frente());
        assertSame(primera,gestor.getListaEspera().frente());
        assertFalse(gestor.estaInscrito("SM-0004"));
        assertNull(gestor.reservar(13,"SM-0001"));
        gestor.cancelar("SM-0001");
        assertSame(primera,gestor.getUltimaPromovida());
        assertEquals(11,primera.getIdReserva());
        assertEquals(1,segunda.getPosicionListaEspera());
    }

    @Test
    void socioInactivoPermaneceVisiblePeroNoAdelantaALosActivosAlPromover() {
        GestorReservasClase gestor=new GestorReservasClase(clase(1));
        gestor.reservar(1,"SM-0001");
        ReservaClase inactivo=gestor.reservar(2,"SM-0002");
        ReservaClase activo=gestor.reservar(3,"SM-0003");
        ReservaClase siguiente=gestor.reservar(4,"SM-0004");

        gestor.cancelar("SM-0001",codigo -> !codigo.equals("SM-0002"));
        assertSame(activo,gestor.getUltimaPromovida());
        assertSame(inactivo,gestor.getListaEspera().frente());
        assertEquals("LISTA_ESPERA",inactivo.getEstado());
        assertEquals(1,inactivo.getPosicionListaEspera());
        assertEquals(2,siguiente.getPosicionListaEspera());
        assertEquals(1,gestor.getClase().getCupoActual());
        assertSame(inactivo,gestor.cancelar("SM-0002",codigo -> true));
        assertEquals("CANCELADA",inactivo.getEstado());
        assertEquals(-1,inactivo.getPosicionListaEspera());
        assertEquals(1,siguiente.getPosicionListaEspera());
    }

    @Test
    void cancelacionSinElegiblesLiberaCupoSinBorrarLaEspera() {
        GestorReservasClase gestor=new GestorReservasClase(clase(1));
        gestor.reservar(1,"SM-0001");
        ReservaClase espera=gestor.reservar(2,"SM-0002");
        gestor.cancelar("SM-0001",codigo -> false);
        assertEquals(0,gestor.getClase().getCupoActual());
        assertNull(gestor.getUltimaPromovida());
        assertSame(espera,gestor.getListaEspera().frente());
        gestor.promoverDisponibles(codigo -> true);
        assertSame(espera,gestor.getConfirmadas().frente());
        assertEquals("CONFIRMADA",espera.getEstado());
        assertEquals(-1,espera.getPosicionListaEspera());
        assertTrue(gestor.getListaEspera().estaVacia());
    }

    @Test
    void ampliarCupoPromueveLaEsperaEnOrdenSinCambiarIds() {
        GestorReservasClase gestor=new GestorReservasClase(clase(1));
        gestor.reservar(1,"SM-0001");
        ReservaClase segunda=gestor.reservar(2,"SM-0002");
        ReservaClase tercera=gestor.reservar(3,"SM-0003");
        gestor.getClase().setCupoMaximo(3);
        gestor.promoverDisponibles(codigo -> true);
        assertEquals(3,gestor.getClase().getCupoActual());
        assertEquals(3,gestor.getConfirmadas().tamanio());
        assertTrue(gestor.getListaEspera().estaVacia());
        gestor.getConfirmadas().dequeue();
        assertSame(segunda,gestor.getConfirmadas().dequeue());
        assertSame(tercera,gestor.getConfirmadas().dequeue());
    }

    @Test
    void actualizaSocioDeLaListaDeEsperaSinRomperElOrdenDePromocion() {
        Clase clase = new Clase(1, "Yoga", 0, "LUNES", LocalTime.of(7, 0),
                LocalTime.of(8, 0), 1, 0);
        GestorReservasClase gestor = new GestorReservasClase(clase);
        gestor.reservar(1, "SM-0001");
        ReservaClase enEspera = gestor.reservar(2, "SM-0002");

        assertEquals("LISTA_ESPERA", enEspera.getEstado());
        assertTrue(gestor.actualizarCodigoSocio("SM-0002", "SM-0003"));
        assertFalse(gestor.actualizarCodigoSocio("SM-0003", "SM-0001"));

        assertEquals("SM-0001", gestor.cancelar("SM-0001").getCodigoSocio());
        assertEquals("SM-0003", gestor.getUltimaPromovida().getCodigoSocio());
        assertEquals("CONFIRMADA", gestor.getUltimaPromovida().getEstado());
    }

    private static Clase clase(int cupo) {
        return new Clase(1,"Yoga",1,"LUNES",LocalTime.of(7,0),LocalTime.of(8,0),cupo,0);
    }
    private static ReservaClase reserva(int id,String socio,String estado,int posicion) {
        return new ReservaClase(id,1,socio,LocalDate.of(2026,10,30),estado,posicion);
    }
}
