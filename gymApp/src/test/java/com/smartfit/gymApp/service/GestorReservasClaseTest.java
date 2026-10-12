package com.smartfit.gymApp.service;

import com.smartfit.gymApp.model.Clase;
import com.smartfit.gymApp.model.ReservaClase;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class GestorReservasClaseTest {

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
}
