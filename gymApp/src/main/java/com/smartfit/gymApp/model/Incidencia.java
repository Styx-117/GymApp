package com.gimnasio.models;

import java.time.LocalDate;

/**
 * Representa una incidencia de mantenimiento sobre un equipo (RF17-RF19).
 * Implementa Comparable por prioridad para que ColaPrioridad pueda
 * atender primero las incidencias mas urgentes.
 */
public class Incidencia implements Comparable<Incidencia> {

    private int idIncidencia;
    private String codigoEquipo;
    private String descripcion;
    private int prioridad; // 1 = alta, 2 = media, 3 = baja (menor numero = mas urgente)
    private LocalDate fechaReporte;
    private LocalDate fechaAtencion;
    private String estado; // PENDIENTE, ATENDIDA

    public Incidencia(int idIncidencia, String codigoEquipo, String descripcion, int prioridad,
                       LocalDate fechaReporte, LocalDate fechaAtencion, String estado) {
        this.idIncidencia = idIncidencia;
        this.codigoEquipo = codigoEquipo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.fechaReporte = fechaReporte;
        this.fechaAtencion = fechaAtencion;
        this.estado = estado;
    }

    public int getIdIncidencia() {
        return idIncidencia;
    }

    public void setIdIncidencia(int idIncidencia) {
        this.idIncidencia = idIncidencia;
    }

    public String getCodigoEquipo() {
        return codigoEquipo;
    }

    public void setCodigoEquipo(String codigoEquipo) {
        this.codigoEquipo = codigoEquipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public LocalDate getFechaReporte() {
        return fechaReporte;
    }

    public void setFechaReporte(LocalDate fechaReporte) {
        this.fechaReporte = fechaReporte;
    }

    public LocalDate getFechaAtencion() {
        return fechaAtencion;
    }

    public void setFechaAtencion(LocalDate fechaAtencion) {
        this.fechaAtencion = fechaAtencion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    /** RF18: menor numero de prioridad se atiende primero en la ColaPrioridad. */
    @Override
    public int compareTo(Incidencia otra) {
        return Integer.compare(this.prioridad, otra.prioridad);
    }

    @Override
    public String toString() {
        return "Incidencia{" +
                "idIncidencia=" + idIncidencia +
                ", codigoEquipo='" + codigoEquipo + '\'' +
                ", prioridad=" + prioridad +
                ", estado='" + estado + '\'' +
                '}';
    }
}
