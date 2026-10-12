package com.smartfit.gymApp.service;

import com.smartfit.gymApp.model.Incidencia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IncidenciaServiceTest {

    private final IncidenciaService servicio = IncidenciaService.getInstance();

    @BeforeEach
    void limpiarColas() {
        servicio.getPendientes().vaciar();
        servicio.getAtendidas().vaciar();
    }

    @Test
    void actualizaPrioridadReordenaYPermiteEliminarPendientes() {
        Incidencia baja = servicio.registrar("EQ-01", "Revisar banda", 3);
        Incidencia alta = servicio.registrar("EQ-02", "Revisar cable", 1);

        assertSame(alta, servicio.verSiguiente());
        assertTrue(servicio.actualizarPendiente(baja.getIdIncidencia(),
                "EQ-01", "Revisar banda y motor", 1));
        assertSame(alta, servicio.atenderSiguiente());
        assertSame(baja, servicio.verSiguiente());

        assertTrue(servicio.eliminarPendiente(baja.getIdIncidencia()));
        assertFalse(servicio.eliminarPendiente(baja.getIdIncidencia()));
        assertNull(servicio.verSiguiente());
    }
}
