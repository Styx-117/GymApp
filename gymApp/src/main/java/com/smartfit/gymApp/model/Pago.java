package com.gimnasio.models;

import java.time.LocalDate;

// Representa el pago de una membresia

public class Pago {

    private int idPago;
    private int idMembresia;
    private double monto;
    private LocalDate fechaPago;
    private String metodoPago; // EFECTIVO, TARJETA, TRANSFERENCIA.


    public Pago(int idPago, int idMembresia, double monto, LocalDate fechaPago, String metodoPago) {
        this.idPago = idPago;
        this.idMembresia = idMembresia;
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.metodoPago = metodoPago;
    }

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public int getIdMembresia() {
        return idMembresia;
    }

    public void setIdMembresia(int idMembresia) {
        this.idMembresia = idMembresia;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    @Override
    public String toString() {
        return "Pago{" +
                "idPago=" + idPago +
                ", idMembresia=" + idMembresia +
                ", monto=" + monto +
                ", fechaPago=" + fechaPago +
                ", metodoPago='" + metodoPago + '\'' +
                '}';
    }
}
