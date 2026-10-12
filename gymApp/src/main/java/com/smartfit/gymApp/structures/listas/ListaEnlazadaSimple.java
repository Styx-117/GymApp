package com.smartfit.gymApp.structures.listas;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class ListaEnlazadaSimple<T> {
    private Nodo<T> cabeza;
    private int tamanio;

    public void agregarInicio(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.setSiguiente(cabeza);
        cabeza = nuevo;
        tamanio++;
    }

    public void agregarFinal(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cabeza == null) { cabeza = nuevo; }
        else {
            Nodo<T> actual = cabeza;
            while (actual.getSiguiente() != null) actual = actual.getSiguiente();
            actual.setSiguiente(nuevo);
        }
        tamanio++;
    }

    public T buscar(java.util.function.Predicate<T> cond) {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (cond.test(actual.getDato())) return actual.getDato();
            actual = actual.getSiguiente();
        }
        return null;
    }

    public boolean eliminar(java.util.function.Predicate<T> cond) {
        if (cabeza == null) return false;
        if (cond.test(cabeza.getDato())) { cabeza = cabeza.getSiguiente(); tamanio--; return true; }
        Nodo<T> actual = cabeza;
        while (actual.getSiguiente() != null) {
            if (cond.test(actual.getSiguiente().getDato())) {
                actual.setSiguiente(actual.getSiguiente().getSiguiente());
                tamanio--;
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    public void recorrer(Consumer<T> accion) {
        Nodo<T> actual = cabeza;
        while (actual != null) { accion.accept(actual.getDato()); actual = actual.getSiguiente(); }
    }

    /** Ordena la lista según el Comparator (usa burbuja sobre los nodos). */
    public void ordenar(Comparator<T> cmp) {
        if (cabeza == null || cabeza.getSiguiente() == null) return;
        boolean cambio;
        do {
            cambio = false;
            Nodo<T> actual = cabeza;
            while (actual.getSiguiente() != null) {
                if (cmp.compare(actual.getDato(), actual.getSiguiente().getDato()) > 0) {
                    T tmp = actual.getDato();
                    actual.setDato(actual.getSiguiente().getDato());
                    actual.getSiguiente().setDato(tmp);
                    cambio = true;
                }
                actual = actual.getSiguiente();
            }
        } while (cambio);
    }

    public List<T> aLista() {
        List<T> out = new ArrayList<>();
        recorrer(out::add);
        return out;
    }

    public boolean estaVacia() { return cabeza == null; }
    public int tamanio()      { return tamanio; }
    public void vaciar()      { cabeza = null; tamanio = 0; }
}