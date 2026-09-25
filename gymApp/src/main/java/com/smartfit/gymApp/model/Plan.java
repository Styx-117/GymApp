package com.gimnasio.models;

//Representa un plan de membresia ofrecido por la sede (ej: mensual, trimestral, anual), con su precio y duracion.
public class Plan {

    private int idPlan;
    private String nombre;
    private String descripcion;
    private int duracionMeses;
    private double precio;

    public Plan(int idPlan, String nombre, String descripcion, int duracionMeses, double precio) {
        this.idPlan = idPlan;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracionMeses = duracionMeses;
        this.precio = precio;
    }

    public int getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(int idPlan) {
        this.idPlan = idPlan;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getDuracionMeses() {
        return duracionMeses;
    }

    public void setDuracionMeses(int duracionMeses) {
        this.duracionMeses = duracionMeses;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "Plan{" +
                "idPlan=" + idPlan +
                ", nombre='" + nombre + '\'' +
                ", duracionMeses=" + duracionMeses +
                ", precio=" + precio +
                '}';
    }
}
