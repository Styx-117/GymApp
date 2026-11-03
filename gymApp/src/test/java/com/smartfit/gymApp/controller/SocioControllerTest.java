package com.smartfit.gymApp.controller;

import com.smartfit.gymApp.model.Socio;
import com.smartfit.gymApp.repository.SocioDAO;
import com.smartfit.gymApp.service.SocioService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SocioControllerTest {
    private static class ServicioFalso extends SocioService {
        int escrituras;
        Socio recibido;
        ServicioFalso() { super(new SocioDAO(), null); }
        @Override public Socio guardar(Socio socio) {
            escrituras++;
            recibido = socio;
            return socio;
        }
    }

    private SocioController controlador(ServicioFalso servicio) throws Exception {
        SocioController controller = new SocioController();
        var field = SocioController.class.getDeclaredField("socioService");
        field.setAccessible(true);
        field.set(controller, servicio);
        return controller;
    }

    @Test void postRechazaIdentificadorAntesDeGuardar() throws Exception {
        ServicioFalso servicio = new ServicioFalso();
        Socio existente = new Socio(null, "Ana", "Quispe", "12345678");
        existente.setIdSocio(4L);
        assertThrows(IllegalArgumentException.class, () -> controlador(servicio).crear(existente));
        assertEquals(0, servicio.escrituras);
    }

    @Test void postSinIdentificadorSigueElFlujoDeAlta() throws Exception {
        ServicioFalso servicio = new ServicioFalso();
        Socio nuevo = new Socio(null, "Ana", "Quispe", "12345678");
        var response = controlador(servicio).crear(nuevo);
        assertEquals(200, response.getStatusCode().value());
        assertSame(nuevo, response.getBody());
        assertEquals(1, servicio.escrituras);
        assertNull(servicio.recibido.getIdSocio());
    }

    @Test void putUsaElIdentificadorDeLaRutaYConservaElContenido() throws Exception {
        ServicioFalso servicio = new ServicioFalso();
        Socio editado = new Socio("SM-0004", "Ana", "Quispe", "12345678");
        editado.setDireccion("Ica");
        editado.setActivo(false);
        var response = controlador(servicio).actualizar(4L, editado);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(4L, servicio.recibido.getIdSocio());
        assertEquals("Ica", servicio.recibido.getDireccion());
        assertFalse(servicio.recibido.getActivo());
        assertEquals(1, servicio.escrituras);
    }
}
