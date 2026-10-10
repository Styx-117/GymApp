package com.smartfit.gymApp.service;

import com.smartfit.gymApp.model.Incidencia;
import com.smartfit.gymApp.structures.colas.Cola;
import com.smartfit.gymApp.structures.colas.ColaPrioridad;

import java.time.LocalDate;


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

    public Incidencia verSiguiente() {
        return pendientes.frente();
    }

    public boolean hayPendientes() {
        return !pendientes.estaVacia();
    }

    public ColaPrioridad<Incidencia> getPendientes() { return pendientes; }

    
    public Cola<Incidencia> getAtendidas() { return atendidas; }
}