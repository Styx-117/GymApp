package com.smartfit.gymApp.structures.colas;


public class NodoCola<T> {

    private T dato;
    private NodoCola<T> siguiente;

    public NodoCola(T dato) {
        this.dato = dato;
    }

    public T getDato() { return dato; }
    public void setDato(T dato) { this.dato = dato; }
    public NodoCola<T> getSiguiente() { return siguiente; }
    public void setSiguiente(NodoCola<T> siguiente) { this.siguiente = siguiente; }
}