package com.gimnasio.models;

import java.time.LocalDate;

/**
 * Representa la reserva de un socio para una clase grupal (RF10, RF11).
 * Estas instancias son las que se encolan en Cola (cupo confirmado) o
 * en la lista de espera cuando la clase esta llena.
 */
public class ReservaClase {

    private int idReserva;
    private int idClase;
    private String codigoSocio;
    private LocalDate fechaReserva;
    private String estado; // CONFIRMADA, LISTA_ESPERA, CANCELADA
    private int posicionListaEspera; // -1 si no aplica

    public ReservaClase() {
    }

    public ReservaClase(int idReserva, int idClase, String codigoSocio, LocalDate fechaReserva,
                         String estado, int posicionListaEspera) {
        this.idReserva = idReserva;
        this.idClase = idClase;
        this.codigoSocio = codigoSocio;
        this.fechaReserva = fechaReserva;
        this.estado = estado;
        this.posicionListaEspera = posicionListaEspera;
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public int getIdClase() {
        return idClase;
    }

    public void setIdClase(int idClase) {
        this.idClase = idClase;
    }

    public String getCodigoSocio() {
        return codigoSocio;
    }

    public void setCodigoSocio(String codigoSocio) {
        this.codigoSocio = codigoSocio;
    }

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getPosicionListaEspera() {
        return posicionListaEspera;
    }

    public void setPosicionListaEspera(int posicionListaEspera) {
        this.posicionListaEspera = posicionListaEspera;
    }

    @Override
    public String toString() {
        return "ReservaClase{" +
                "idReserva=" + idReserva +
                ", idClase=" + idClase +
                ", codigoSocio='" + codigoSocio + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
