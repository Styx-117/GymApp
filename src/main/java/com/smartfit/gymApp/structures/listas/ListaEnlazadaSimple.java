package com.smartfit.gymApp.structures.listas;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class ListaEnlazadaSimple<T> implements com.smartfit.gymApp.structures.tad.TADLista<T> {
    public void agregar(T dato) { agregarInicio(dato); }
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

    /** Merge sort sobre enlaces: O(n log n), estable y sin colecciones auxiliares. */
    public void ordenar(Comparator<T> cmp) {
        cabeza = ordenarNodos(cabeza, cmp);
    }

    private Nodo<T> ordenarNodos(Nodo<T> inicio, Comparator<T> cmp) {
        if (inicio == null || inicio.getSiguiente() == null) return inicio;
        Nodo<T> lento=inicio, rapido=inicio.getSiguiente();
        while(rapido!=null && rapido.getSiguiente()!=null){lento=lento.getSiguiente();rapido=rapido.getSiguiente().getSiguiente();}
        Nodo<T> derecha=lento.getSiguiente(); lento.setSiguiente(null);
        Nodo<T> a=ordenarNodos(inicio,cmp), b=ordenarNodos(derecha,cmp);
        Nodo<T> ancla=new Nodo<>(null), cola=ancla;
        while(a!=null && b!=null){
            if(cmp.compare(a.getDato(),b.getDato())<=0){cola.setSiguiente(a);a=a.getSiguiente();}
            else {cola.setSiguiente(b);b=b.getSiguiente();}
            cola=cola.getSiguiente();
        }
        cola.setSiguiente(a==null?b:a);return ancla.getSiguiente();
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
