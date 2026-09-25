package com.gimnasio.models;

import java.time.LocalDateTime;

/**
 * Representa una accion realizada por el personal en el sistema (Para RF20).
 * Estas instancias son las que se apilan en Pila para poder revertir
 * la accion mas reciente.
 */
public class Auditoria {

    private int idAuditoria;
    private int idUsuario;
    private String accion; // ej: "REGISTRAR_SOCIO", "CANCELAR_RESERVA"
    private LocalDateTime fechaHora;
    private String detalle;


    public Auditoria(int idAuditoria, int idUsuario, String accion,
                      LocalDateTime fechaHora, String detalle) {
        this.idAuditoria = idAuditoria;
        this.idUsuario = idUsuario;
        this.accion = accion;
        this.fechaHora = fechaHora;
        this.detalle = detalle;
    }

    //Getters y Setters
    public int getIdAuditoria() {
        return idAuditoria;
    }

    public void setIdAuditoria(int idAuditoria) {
        this.idAuditoria = idAuditoria;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    @Override
    public String toString() {
        return "Auditoria{" +
                "idAuditoria=" + idAuditoria +
                ", idUsuario=" + idUsuario +
                ", accion='" + accion + '\'' +
                ", fechaHora=" + fechaHora +
                '}';
    }
}
