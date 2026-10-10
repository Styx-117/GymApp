package com.smartfit.gymApp.structures.arboles;

public class NodoArbol<T> implements NodoVisual {

    private T dato;
    private String etiqueta;
    private NodoArbol<T> izq;
    private NodoArbol<T> der;

    public NodoArbol(T dato, String etiqueta) {
        this.dato = dato;
        this.etiqueta = etiqueta;
    }

    public T getDato() { return dato; }

    public void setDato(T dato, String etiqueta) {
        this.dato = dato;
        this.etiqueta = etiqueta;
    }

    public NodoArbol<T> getIzq() { return izq; }
    public void setIzq(NodoArbol<T> izq) { this.izq = izq; }
    public NodoArbol<T> getDer() { return der; }
    public void setDer(NodoArbol<T> der) { this.der = der; }

    public boolean esHoja() {
        return izq == null && der == null;
    }

    @Override public String etiqueta() { return etiqueta; }
    @Override public NodoVisual izquierdo() { return izq; }
    @Override public NodoVisual derecho() { return der; }
}