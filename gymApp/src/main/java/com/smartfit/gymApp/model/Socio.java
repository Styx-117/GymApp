package com.gimnasio.models;

import java.time.LocalDate;

/**
 * Representa a un socio/cliente de la sede. El campo codigoSocio es la clave
 * que se utiliza para ubicar al socio dentro del ABB / AVL de busqueda.
 */
public class Socio implements Comparable<Socio> {

    private String codigoSocio; // clave unica (ej: SM-0001)
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private String correo;
    private LocalDate fechaNacimiento;
    private LocalDate fechaRegistro;
    private boolean activo;

    public Socio() {
    }

    public Socio(String codigoSocio, String nombre, String apellido, String dni,
                 String telefono, String correo, LocalDate fechaNacimiento,
                 LocalDate fechaRegistro, boolean activo) {
        this.codigoSocio = codigoSocio;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.telefono = telefono;
        this.correo = correo;
        this.fechaNacimiento = fechaNacimiento;
        this.fechaRegistro = fechaRegistro;
        this.activo = activo;
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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    /**
     * Orden natural por codigoSocio: es la comparacion que usaran
     * el ABB y el AVL para insertar/buscar/eliminar.
     */
    @Override
    public int compareTo(Socio otro) {
        return this.codigoSocio.compareTo(otro.codigoSocio);
    }

    @Override
    public String toString() {
        return "Socio{" +
                "codigoSocio='" + codigoSocio + '\'' +
                ", nombreCompleto='" + getNombreCompleto() + '\'' +
                ", dni='" + dni + '\'' +
                ", activo=" + activo +
                '}';
    }
}
