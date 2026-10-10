package com.smartfit.gymApp.structures.pilas;

public class NodoPila<T> {

    private T dato;
    private NodoPila<T> abajo;

    public NodoPila(T dato) {
        this.dato = dato;
    }

    public T getDato() { return dato; }
    public void setDato(T dato) { this.dato = dato; }
    public NodoPila<T> getAbajo() { return abajo; }
    public void setAbajo(NodoPila<T> abajo) { this.abajo = abajo; }
}