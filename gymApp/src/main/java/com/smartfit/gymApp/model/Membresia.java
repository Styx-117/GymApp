package com.gimnasio.models;

import java.time.LocalDate;

/**
 * Representa la membresia contratada por un socio: que plan tiene
 * y en que rango de fechas esta vigente. Soporta RF08 (socios
 * proximos a vencer o vencidos).
 */

public class Membresia {

    private int idMembresia;
    private String codigoSocio;
    private int idPlan;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado; // ACTIVA, VENCIDA, POR_VENCER


    public Membresia(int idMembresia, String codigoSocio, int idPlan,
                      LocalDate fechaInicio, LocalDate fechaFin, String estado) {
        this.idMembresia = idMembresia;
        this.codigoSocio = codigoSocio;
        this.idPlan = idPlan;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
    }

    public int getIdMembresia() {
        return idMembresia;
    }

    public void setIdMembresia(int idMembresia) {
        this.idMembresia = idMembresia;
    }

    public String getCodigoSocio() {
        return codigoSocio;
    }

    public void setCodigoSocio(String codigoSocio) {
        this.codigoSocio = codigoSocio;
    }

    public int getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(int idPlan) {
        this.idPlan = idPlan;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    /** RF08: indica si la membresia ya vencio respecto a la fecha actual. */
    public boolean estaVencida() {
        return fechaFin != null && LocalDate.now().isAfter(fechaFin);
    }

    /** RF08: indica si la membresia vence dentro de los proximos "dias" dias. */
    public boolean estaProximaAVencer(int dias) {
        if (fechaFin == null) {
            return false;
        }
        LocalDate limite = LocalDate.now().plusDays(dias);
        return !estaVencida() && !fechaFin.isAfter(limite);
    }

    @Override
    public String toString() {
        return "Membresia{" +
                "idMembresia=" + idMembresia +
                ", codigoSocio='" + codigoSocio + '\'' +
                ", idPlan=" + idPlan +
                ", fechaFin=" + fechaFin +
                ", estado='" + estado + '\'' +
                '}';
    }
}
