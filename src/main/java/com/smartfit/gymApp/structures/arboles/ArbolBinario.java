package com.smartfit.gymApp.structures.arboles;

import com.smartfit.gymApp.structures.colas.Cola;

import java.util.function.Function;


public class ArbolBinario<T> {

    protected NodoArbol<T> raiz;
    protected Function<T, String> etiquetador;

    public ArbolBinario() {
    }

  
    public void setEtiquetador(Function<T, String> etiquetador) {
        this.etiquetador = etiquetador;
    }

    protected String etiquetar(T dato) {
        return etiquetador != null ? etiquetador.apply(dato) : String.valueOf(dato);
    }

    
    public boolean insertar(T dato) {
        NodoArbol<T> nuevo = new NodoArbol<>(dato, etiquetar(dato));
        if (raiz == null) {
            raiz = nuevo;
            return true;
        }
        Cola<NodoArbol<T>> cola = new Cola<>();
        cola.enqueue(raiz);
        while (!cola.estaVacia()) {
            NodoArbol<T> n = cola.dequeue();
            if (n.getIzq() == null) {
                n.setIzq(nuevo);
                return true;
            }
            cola.enqueue(n.getIzq());
            if (n.getDer() == null) {
                n.setDer(nuevo);
                return true;
            }
            cola.enqueue(n.getDer());
        }
        return false; 
    }

    public T buscar(T dato) {
        NodoArbol<T> n = buscarNodo(dato);
        return n == null ? null : n.getDato();
    }

    public boolean contiene(T dato) {
        return buscarNodo(dato) != null;
    }

    private NodoArbol<T> buscarNodo(T dato) {
        if (raiz == null) return null;
        Cola<NodoArbol<T>> cola = new Cola<>();
        cola.enqueue(raiz);
        while (!cola.estaVacia()) {
            NodoArbol<T> n = cola.dequeue();
            if (n.getDato().equals(dato)) return n;
            if (n.getIzq() != null) cola.enqueue(n.getIzq());
            if (n.getDer() != null) cola.enqueue(n.getDer());
        }
        return null;
    }

    
    public boolean eliminar(T dato) {
        NodoArbol<T> objetivo = buscarNodo(dato);
        if (objetivo == null) return false;

        NodoArbol<T> ultimo = ultimoNodo();
        if (ultimo == raiz) {          
            raiz = null;
            return true;
        }
        objetivo.setDato(ultimo.getDato(), ultimo.etiqueta());
        NodoArbol<T> padre = padreDe(ultimo);
        if (padre.getDer() == ultimo) padre.setDer(null);
        else padre.setIzq(null);
        return true;
    }

    private NodoArbol<T> ultimoNodo() {
        Cola<NodoArbol<T>> cola = new Cola<>();
        cola.enqueue(raiz);
        NodoArbol<T> ultimo = raiz;
        while (!cola.estaVacia()) {
            ultimo = cola.dequeue();
            if (ultimo.getIzq() != null) cola.enqueue(ultimo.getIzq());
            if (ultimo.getDer() != null) cola.enqueue(ultimo.getDer());
        }
        return ultimo;
    }

    private NodoArbol<T> padreDe(NodoArbol<T> hijo) {
        Cola<NodoArbol<T>> cola = new Cola<>();
        cola.enqueue(raiz);
        while (!cola.estaVacia()) {
            NodoArbol<T> n = cola.dequeue();
            if (n.getIzq() == hijo || n.getDer() == hijo) return n;
            if (n.getIzq() != null) cola.enqueue(n.getIzq());
            if (n.getDer() != null) cola.enqueue(n.getDer());
        }
        return null;
    }

   
    public String preorden()   { return Recorridos.preorden(raiz); }
    public String inorden()    { return Recorridos.inorden(raiz); }
    public String postorden()  { return Recorridos.postorden(raiz); }
    public String porNiveles() { return Recorridos.porNiveles(raiz); }

    
    public boolean estaVacio()  { return raiz == null; }
    public int altura()         { return Recorridos.altura(raiz); }
    public int tamanio()        { return Recorridos.contar(raiz); }
    public void vaciar()        { raiz = null; }

    
    public NodoVisual getRaizVisual() { return raiz; }
}