package com.smartfit.gymApp.structures.listas;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ListaDoblementeEnlazada<T> {
    private NodoDoble<T> cabeza;
    private NodoDoble<T> cola;
    private int tamanio;

    public void agregarFinal(T dato) {
        NodoDoble<T> nuevo = new NodoDoble<>(dato);
        if (cabeza == null) { cabeza = cola = nuevo; }
        else {
            nuevo.setAnterior(cola);
            cola.setSiguiente(nuevo);
            cola = nuevo;
        }
        tamanio++;
    }

    public void agregarInicio(T dato) {
        NodoDoble<T> nuevo = new NodoDoble<>(dato);
        if (cabeza == null) { cabeza = cola = nuevo; }
        else {
            nuevo.setSiguiente(cabeza);
            cabeza.setAnterior(nuevo);
            cabeza = nuevo;
        }
        tamanio++;
    }

    public T buscar(Predicate<T> cond) {
        for (NodoDoble<T> a = cabeza; a != null; a = a.getSiguiente())
            if (cond.test(a.getDato())) return a.getDato();
        return null;
    }

    public boolean eliminar(Predicate<T> cond) {
        NodoDoble<T> a = cabeza;
        while (a != null) {
            if (cond.test(a.getDato())) {
                if (a.getAnterior() != null) a.getAnterior().setSiguiente(a.getSiguiente());
                else cabeza = a.getSiguiente();
                if (a.getSiguiente() != null) a.getSiguiente().setAnterior(a.getAnterior());
                else cola = a.getAnterior();
                tamanio--;
                return true;
            }
            a = a.getSiguiente();
        }
        return false;
    }

    public void recorrerAdelante(Consumer<T> accion) {
        for (NodoDoble<T> a = cabeza; a != null; a = a.getSiguiente()) accion.accept(a.getDato());
    }

    public void recorrerAtras(Consumer<T> accion) {
        for (NodoDoble<T> a = cola; a != null; a = a.getAnterior()) accion.accept(a.getDato());
    }

    public boolean estaVacia() { return cabeza == null; }
    public int tamanio()      { return tamanio; }
    public void vaciar()      { cabeza = cola = null; tamanio = 0; }
}