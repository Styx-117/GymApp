package com.smartfit.gymApp.structures.pilas;

import java.util.function.Consumer;


public class Pila<T> {

    private NodoPila<T> tope;
    private int tamanio;

    
    public void push(T dato) {
        NodoPila<T> nuevo = new NodoPila<>(dato);
        nuevo.setAbajo(tope);
        tope = nuevo;
        tamanio++;
    }

 
    public T pop() {
        if (estaVacia()) {
            return null;
        }
        T dato = tope.getDato();
        tope = tope.getAbajo();
        tamanio--;
        return dato;
    }

   
    public T peek() {
        return estaVacia() ? null : tope.getDato();
    }

    public boolean estaVacia() {
        return tope == null;
    }

    public int tamanio() {
        return tamanio;
    }

    public void vaciar() {
        tope = null;
        tamanio = 0;
    }

    
    public void recorrer(Consumer<T> accion) {
        NodoPila<T> actual = tope;
        while (actual != null) {
            accion.accept(actual.getDato());
            actual = actual.getAbajo();
        }
    }

   
    public String mostrar() {
        if (estaVacia()) {
            return "(pila vacia)";
        }
        StringBuilder sb = new StringBuilder();
        NodoPila<T> actual = tope;
        int pos = 1;
        while (actual != null) {
            sb.append(pos == 1 ? "TOPE -> " : "        ").append(pos).append(". ")
              .append(actual.getDato()).append('\n');
            actual = actual.getAbajo();
            pos++;
        }
        return sb.toString();
    }
}