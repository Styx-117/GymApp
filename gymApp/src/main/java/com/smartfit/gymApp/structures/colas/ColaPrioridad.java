package com.smartfit.gymApp.structures.colas;

import java.util.function.Consumer;


public class ColaPrioridad<T extends Comparable<T>> {

    private NodoCola<T> frente;
    private int tamanio;

   
    public void encolar(T dato) {
        NodoCola<T> nuevo = new NodoCola<>(dato);
        if (frente == null || dato.compareTo(frente.getDato()) < 0) {
            nuevo.setSiguiente(frente);
            frente = nuevo;
        } else {
            NodoCola<T> actual = frente;
            
            while (actual.getSiguiente() != null
                    && actual.getSiguiente().getDato().compareTo(dato) <= 0) {
                actual = actual.getSiguiente();
            }
            nuevo.setSiguiente(actual.getSiguiente());
            actual.setSiguiente(nuevo);
        }
        tamanio++;
    }

   
    public T desencolar() {
        if (estaVacia()) {
            return null;
        }
        T dato = frente.getDato();
        frente = frente.getSiguiente();
        tamanio--;
        return dato;
    }

    
    public T frente() {
        return estaVacia() ? null : frente.getDato();
    }

    public boolean estaVacia() {
        return frente == null;
    }

    public int tamanio() {
        return tamanio;
    }

    public void vaciar() {
        frente = null;
        tamanio = 0;
    }

    public void recorrer(Consumer<T> accion) {
        NodoCola<T> actual = frente;
        while (actual != null) {
            accion.accept(actual.getDato());
            actual = actual.getSiguiente();
        }
    }

    public String mostrar() {
        if (estaVacia()) {
            return "(cola de prioridad vacia)";
        }
        StringBuilder sb = new StringBuilder();
        NodoCola<T> actual = frente;
        int pos = 1;
        while (actual != null) {
            sb.append(pos == 1 ? "SIGUIENTE -> " : "             ").append(pos).append(". ")
              .append(actual.getDato()).append('\n');
            actual = actual.getSiguiente();
            pos++;
        }
        return sb.toString();
    }
}