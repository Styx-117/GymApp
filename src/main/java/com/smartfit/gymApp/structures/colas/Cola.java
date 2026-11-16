package com.smartfit.gymApp.structures.colas;

import java.util.function.Consumer;
import java.util.function.Predicate;


public class Cola<T> {

    private NodoCola<T> frente;
    private NodoCola<T> fin;
    private int tamanio;

    
    public void enqueue(T dato) {
        NodoCola<T> nuevo = new NodoCola<>(dato);
        if (estaVacia()) {
            frente = nuevo;
        } else {
            fin.setSiguiente(nuevo);
        }
        fin = nuevo;
        tamanio++;
    }

   
    public T dequeue() {
        if (estaVacia()) {
            return null;
        }
        T dato = frente.getDato();
        frente = frente.getSiguiente();
        if (frente == null) {
            fin = null;
        }
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
        fin = null;
        tamanio = 0;
    }

    
    public T buscar(Predicate<T> condicion) {
        NodoCola<T> actual = frente;
        while (actual != null) {
            if (condicion.test(actual.getDato())) {
                return actual.getDato();
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    public T quitarSi(Predicate<T> condicion) {
        NodoCola<T> anterior = null;
        NodoCola<T> actual = frente;
        while (actual != null) {
            if (condicion.test(actual.getDato())) {
                if (anterior == null) {
                    frente = actual.getSiguiente();
                } else {
                    anterior.setSiguiente(actual.getSiguiente());
                }
                if (actual == fin) {
                    fin = anterior;
                }
                tamanio--;
                return actual.getDato();
            }
            anterior = actual;
            actual = actual.getSiguiente();
        }
        return null;
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
            return "(cola vacia)";
        }
        StringBuilder sb = new StringBuilder();
        NodoCola<T> actual = frente;
        int pos = 1;
        while (actual != null) {
            sb.append(pos == 1 ? "FRENTE -> " : "          ").append(pos).append(". ")
              .append(actual.getDato()).append('\n');
            actual = actual.getSiguiente();
            pos++;
        }
        return sb.toString();
    }
}