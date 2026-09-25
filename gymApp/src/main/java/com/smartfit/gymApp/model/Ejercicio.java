package com.gimnasio.models;

/**
 * Esta clase representa un ejercico dentro de una rutina. Será un elemento
 * clave para agregar ejercicios dentro de una Linked list o una lista doble
 * y asi armar la rutina del cliente (Para RF16)
 */
public class Ejercicio {

    private int idEjercicio;
    private String nombre;
    private int series;
    private int repeticiones;
    private int descansoSegundos;
    private String grupoMuscular;


    public Ejercicio(int idEjercicio, String nombre, int series, int repeticiones,
                      int descansoSegundos, String grupoMuscular) {
        this.idEjercicio = idEjercicio;
        this.nombre = nombre;
        this.series = series;
        this.repeticiones = repeticiones;
        this.descansoSegundos = descansoSegundos;
        this.grupoMuscular = grupoMuscular;
    }

    public int getIdEjercicio() {
        return idEjercicio;
    }

    public void setIdEjercicio(int idEjercicio) {
        this.idEjercicio = idEjercicio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public int getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(int repeticiones) {
        this.repeticiones = repeticiones;
    }

    public int getDescansoSegundos() {
        return descansoSegundos;
    }

    public void setDescansoSegundos(int descansoSegundos) {
        this.descansoSegundos = descansoSegundos;
    }

    public String getGrupoMuscular() {
        return grupoMuscular;
    }

    public void setGrupoMuscular(String grupoMuscular) {
        this.grupoMuscular = grupoMuscular;
    }

    @Override
    public String toString() {
        return "Ejercicio{" +
                "idEjercicio=" + idEjercicio +
                ", nombre='" + nombre + '\'' +
                ", series=" + series +
                ", repeticiones=" + repeticiones +
                ", grupoMuscular='" + grupoMuscular + '\'' +
                '}';
    }
}
