package com.smartfit.gymApp.structures.listas;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ListaCircular<T> {
    private NodoCircular<T> ultimo;
    private int tamanio;

    public void agregar(T dato) {
        NodoCircular<T> nuevo = new NodoCircular<>(dato);
        if (ultimo == null) {
            nuevo.setSiguiente(nuevo);
            ultimo = nuevo;
        } else {
            nuevo.setSiguiente(ultimo.getSiguiente());
            ultimo.setSiguiente(nuevo);
            ultimo = nuevo;
        }
        tamanio++;
    }

    public T buscar(Predicate<T> cond) {
        if (ultimo == null) return null;
        NodoCircular<T> inicio = ultimo.getSiguiente();
        NodoCircular<T> a = inicio;
        do {
            if (cond.test(a.getDato())) return a.getDato();
            a = a.getSiguiente();
        } while (a != inicio);
        return null;
    }

    public boolean eliminar(Predicate<T> cond) {
        if (ultimo == null) return false;
        NodoCircular<T> inicio = ultimo.getSiguiente();
        NodoCircular<T> anterior = ultimo;
        NodoCircular<T> actual = inicio;
        do {
            if (cond.test(actual.getDato())) {
                if (actual == ultimo && actual.getSiguiente() == actual) {
                    ultimo = null;
                } else {
                    anterior.setSiguiente(actual.getSiguiente());
                    if (actual == ultimo) ultimo = anterior;
                }
                tamanio--;
                return true;
            }
            anterior = actual;
            actual = actual.getSiguiente();
        } while (actual != inicio);
        return false;
    }

    public void recorrer(Consumer<T> accion) {
        if (ultimo == null) return;
        NodoCircular<T> inicio = ultimo.getSiguiente();
        NodoCircular<T> a = inicio;
        do { accion.accept(a.getDato()); a = a.getSiguiente(); } while (a != inicio);
    }

    public int tamanio() { return tamanio; }
    public boolean estaVacia() { return ultimo == null; }
    public void vaciar() { ultimo = null; tamanio = 0; }
}