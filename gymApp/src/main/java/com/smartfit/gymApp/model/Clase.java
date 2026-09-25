package com.gimnasio.models;

import java.time.LocalTime;

/**
 * Representa una clase grupal disponible en la sede (Para RF09), con su
 * cupo maximo y cupo actualmente ocupado. La MatrizHorarios (arreglo
 * bidimensional) organiza estas clases por dia/hora (RF12).
 */
public class Clase {

    private int idClase;
    private String nombre;
    private int idEntrenador;
    private String diaSemana; // LUNES, MARTES, ...
    private timestamp horaInicio;
    private LocalTime horaFin;
    private int cupoMaximo;
    private int cupoActual;



    public Clase(int idClase, String nombre, int idEntrenador, String diaSemana,
                 LocalTime horaInicio, LocalTime horaFin, int cupoMaximo, int cupoActual) {
        this.idClase = idClase;
        this.nombre = nombre;
        this.idEntrenador = idEntrenador;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.cupoMaximo = cupoMaximo;
        this.cupoActual = cupoActual;
    }

    public int getIdClase() {
        return idClase;
    }

    public void setIdClase(int idClase) {
        this.idClase = idClase;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdEntrenador() {
        return idEntrenador;
    }

    public void setIdEntrenador(int idEntrenador) {
        this.idEntrenador = idEntrenador;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(int cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }

    public int getCupoActual() {
        return cupoActual;
    }

    public void setCupoActual(int cupoActual) {
        this.cupoActual = cupoActual;
    }

    //si no hay cupo, la reserva debe ir a la cola de espera. Usar una cola
    public boolean tieneCupoDisponible() {
        return cupoActual < cupoMaximo;
    }

    @Override
    public String toString() {
        return "Clase{" +
                "idClase=" + idClase +
                ", nombre='" + nombre + '\'' +
                ", diaSemana='" + diaSemana + '\'' +
                ", horaInicio=" + horaInicio +
                ", cupoActual=" + cupoActual +
                "/" + cupoMaximo +
                '}';
    }
}
