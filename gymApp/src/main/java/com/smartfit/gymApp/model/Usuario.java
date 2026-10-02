package com.smartfit.gymApp.model;

public class Usuario {
    private Long id;
    private String nombre;
    private String correo;
    private String password;
    private Boolean estado;

    public Usuario() {
    }

    public Usuario(Long id, String nombre, String correo, String password, Boolean estado) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.password = password;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Métodos alias para compatibilidad
    public String getContrasena() {
        return password;
    }

    public void setContrasena(String contrasena) {
        this.password = contrasena;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}