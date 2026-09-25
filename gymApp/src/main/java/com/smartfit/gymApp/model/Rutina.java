package com.gimnasio.models;

import java.time.LocalDate;

/**
 * Representa la rutina asignada a un socio. La secuencia de
 * ejercicios en si se administraria mediante una lista enlazada netamente de ejercicios;
 */

//Nota: LocalDate se usaria para almacenar fechas
public class Rutina {

    private int idRutina;
    private String codigoSocio;
    private String nombre;
    private LocalDate fechaAsignacion;
    private int idEntrenador;

    public Rutina(int idRutina, String codigoSocio, String nombre,
                   LocalDate fechaAsignacion, int idEntrenador) {
        this.idRutina = idRutina;
        this.codigoSocio = codigoSocio;
        this.nombre = nombre;
        this.fechaAsignacion = fechaAsignacion;
        this.idEntrenador = idEntrenador;
    }

    public int getIdRutina() {
        return idRutina;
    }

    public void setIdRutina(int idRutina) {
        this.idRutina = idRutina;
    }

    public String getCodigoSocio() {
        return codigoSocio;
    }

    public void setCodigoSocio(String codigoSocio) {
        this.codigoSocio = codigoSocio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDate fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public int getIdEntrenador() {
        return idEntrenador;
    }

    public void setIdEntrenador(int idEntrenador) {
        this.idEntrenador = idEntrenador;
    }

    @Override
    public String toString() {
        return "Rutina{" +
                "idRutina=" + idRutina +
                ", codigoSocio='" + codigoSocio + '\'' +
                ", nombre='" + nombre + '\'' +
                ", fechaAsignacion=" + fechaAsignacion +
                '}';
    }
}
