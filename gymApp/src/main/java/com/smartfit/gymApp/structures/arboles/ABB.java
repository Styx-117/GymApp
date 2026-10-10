package com.smartfit.gymApp.structures.arboles;

public class ABB<T extends Comparable<T>> extends ArbolBinario<T> {

    private boolean cambio;          
    private int comparaciones;       

    
    @Override
    public boolean insertar(T dato) {
        cambio = false;
        raiz = insertar(raiz, dato);
        return cambio;
    }

    private NodoArbol<T> insertar(NodoArbol<T> n, T dato) {
        if (n == null) {
            cambio = true;
            return new NodoArbol<>(dato, etiquetar(dato));
        }
        int c = dato.compareTo(n.getDato());
        if (c < 0) n.setIzq(insertar(n.getIzq(), dato));
        else if (c > 0) n.setDer(insertar(n.getDer(), dato));
        return n; 
    }

   
    @Override
    public T buscar(T clave) {
        comparaciones = 0;
        NodoArbol<T> actual = raiz;
        while (actual != null) {
            comparaciones++;
            int c = clave.compareTo(actual.getDato());
            if (c == 0) return actual.getDato();
            actual = c < 0 ? actual.getIzq() : actual.getDer();
        }
        return null;
    }

    @Override
    public boolean contiene(T clave) {
        return buscar(clave) != null;
    }

    public int getComparaciones() {
        return comparaciones;
    }

    public T minimo() {
        NodoArbol<T> n = minNodo(raiz);
        return n == null ? null : n.getDato();
    }

    public T maximo() {
        NodoArbol<T> n = raiz;
        if (n == null) return null;
        while (n.getDer() != null) n = n.getDer();
        return n.getDato();
    }

    private NodoArbol<T> minNodo(NodoArbol<T> n) {
        if (n == null) return null;
        while (n.getIzq() != null) n = n.getIzq();
        return n;
    }

   
    
    @Override
    public boolean eliminar(T clave) {
        cambio = false;
        raiz = eliminar(raiz, clave);
        return cambio;
    }

    private NodoArbol<T> eliminar(NodoArbol<T> n, T clave) {
        if (n == null) return null;
        int c = clave.compareTo(n.getDato());
        if (c < 0) {
            n.setIzq(eliminar(n.getIzq(), clave));
        } else if (c > 0) {
            n.setDer(eliminar(n.getDer(), clave));
        } else {
            cambio = true;
            if (n.getIzq() == null) return n.getDer();   
            if (n.getDer() == null) return n.getIzq();   
            NodoArbol<T> sucesor = minNodo(n.getDer());  
            n.setDato(sucesor.getDato(), sucesor.etiqueta());
            n.setDer(eliminar(n.getDer(), sucesor.getDato()));
        }
        return n;
    }
}