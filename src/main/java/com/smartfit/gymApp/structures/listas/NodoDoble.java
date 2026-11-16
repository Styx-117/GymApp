package com.smartfit.gymApp.structures.listas;

public class NodoDoble<T> {
    private T dato;
    private NodoDoble<T> anterior;
    private NodoDoble<T> siguiente;

    public NodoDoble(T dato) { this.dato = dato; }
    public T getDato() { return dato; }
    public void setDato(T dato) { this.dato = dato; }
    public NodoDoble<T> getAnterior() { return anterior; }
    public void setAnterior(NodoDoble<T> anterior) { this.anterior = anterior; }
    public NodoDoble<T> getSiguiente() { return siguiente; }
    public void setSiguiente(NodoDoble<T> siguiente) { this.siguiente = siguiente; }
}