package com.gimnasio.models;

import java.time.LocalDate;

/**
 * Representa una maquina o equipo de la sede.
 * El campo de codigoEquipo es clave para dos cosas, una para la base de datos,
 * y la otra para la busqueda binaria/arbol
 */

public class Equipo implements Comparable<Equipo> {

    private String codigoEquipo; // clave unica (algo asi como: EQ-0001)
    private String nombre;
    private String tipo; // CARDIO, MUSCULACION, etc.
    private String ubicacion;
    private LocalDate fechaAdquisicion;
    private String estado; // OPERATIVO, EN_MANTENIMIENTO, FUERA_DE_SERVICIO

    public Equipo(String codigoEquipo, String nombre, String tipo, String ubicacion,
                  LocalDate fechaAdquisicion, String estado) {
        this.codigoEquipo = codigoEquipo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.ubicacion = ubicacion;
        this.fechaAdquisicion = fechaAdquisicion;
        this.estado = estado;
    }

    public String getCodigoEquipo() {
        return codigoEquipo;
    }

    public void setCodigoEquipo(String codigoEquipo) {
        this.codigoEquipo = codigoEquipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public LocalDate getFechaAdquisicion() {
        return fechaAdquisicion;
    }

    public void setFechaAdquisicion(LocalDate fechaAdquisicion) {
        this.fechaAdquisicion = fechaAdquisicion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public int compareTo(Equipo otro) {
        return this.codigoEquipo.compareTo(otro.codigoEquipo);
    }

    @Override
    public String toString() {
        return "Equipo{" +
                "codigoEquipo='" + codigoEquipo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", tipo='" + tipo + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
