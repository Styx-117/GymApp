package com.smartfit.gymApp.service;

import com.smartfit.gymApp.model.Incidencia;
import com.smartfit.gymApp.structures.colas.Cola;
import com.smartfit.gymApp.structures.colas.ColaPrioridad;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class IncidenciaService {

    private static IncidenciaService instancia;

    private final ColaPrioridad<Incidencia> pendientes = new ColaPrioridad<>();
    private final Cola<Incidencia> atendidas = new Cola<>();
    private int contador = 0;

    private IncidenciaService() {}

    public static synchronized IncidenciaService getInstance() {
        if (instancia == null) {
            instancia = new IncidenciaService();
        }
        return instancia;
    }

    
    public Incidencia registrar(String codigoEquipo, String descripcion, int prioridad) {
        Incidencia inc = new Incidencia(++contador, codigoEquipo, descripcion, prioridad,
                LocalDate.now(), null, "PENDIENTE");
        pendientes.encolar(inc);
        AuditoriaService.getInstance().registrar("REGISTRAR_INCIDENCIA",
                codigoEquipo + " prioridad " + prioridad);
        return inc;
    }

    
    public Incidencia atenderSiguiente() {
        Incidencia inc = pendientes.desencolar();
        if (inc == null) {
            return null;
        }
        inc.setEstado("ATENDIDA");
        inc.setFechaAtencion(LocalDate.now());
        atendidas.enqueue(inc);
        AuditoriaService.getInstance().registrar("ATENDER_INCIDENCIA",
                "#" + inc.getIdIncidencia() + " equipo " + inc.getCodigoEquipo());
        return inc;
    }

    public boolean actualizarPendiente(int id, String codigoEquipo, String descripcion, int prioridad) {
        if (codigoEquipo == null || codigoEquipo.isBlank() || descripcion == null || descripcion.isBlank()
                || prioridad < 1 || prioridad > 3) {
            return false;
        }
        Incidencia incidencia = quitarPendiente(id);
        if (incidencia == null) {
            return false;
        }
        incidencia.setCodigoEquipo(codigoEquipo);
        incidencia.setDescripcion(descripcion);
        incidencia.setPrioridad(prioridad);
        pendientes.encolar(incidencia);
        AuditoriaService.getInstance().registrar("ACTUALIZAR_INCIDENCIA",
                "#" + id + " equipo " + codigoEquipo + " prioridad " + prioridad);
        return true;
    }

    public boolean eliminarPendiente(int id) {
        Incidencia incidencia = quitarPendiente(id);
        if (incidencia == null) {
            return false;
        }
        AuditoriaService.getInstance().registrar("ELIMINAR_INCIDENCIA", "#" + id);
        return true;
    }

    private Incidencia quitarPendiente(int id) {
        List<Incidencia> incidencias = new ArrayList<>();
        pendientes.recorrer(incidencias::add);
        Incidencia seleccionada = incidencias.stream()
                .filter(i -> i.getIdIncidencia() == id)
                .findFirst()
                .orElse(null);
        if (seleccionada == null) {
            return null;
        }

        pendientes.vaciar();
        for (Incidencia incidencia : incidencias) {
            if (incidencia != seleccionada) {
                pendientes.encolar(incidencia);
            }
        }
        return seleccionada;
    }

    public Incidencia verSiguiente() {
        return pendientes.frente();
    }

    public boolean hayPendientes() {
        return !pendientes.estaVacia();
    }

    public ColaPrioridad<Incidencia> getPendientes() { return pendientes; }

    
    public Cola<Incidencia> getAtendidas() { return atendidas; }
}