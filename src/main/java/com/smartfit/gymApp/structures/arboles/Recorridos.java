package com.smartfit.gymApp.structures.arboles;

import com.smartfit.gymApp.structures.colas.Cola;


public final class Recorridos {

    private static final String SEP = " -> ";

    private Recorridos() {}

    
    public static String preorden(NodoVisual raiz) {
        StringBuilder sb = new StringBuilder();
        preorden(raiz, sb);
        return resultado(sb);
    }

    private static void preorden(NodoVisual n, StringBuilder sb) {
        if (n == null) return;
        agregar(sb, n.etiqueta());
        preorden(n.izquierdo(), sb);
        preorden(n.derecho(), sb);
    }

   
    public static String inorden(NodoVisual raiz) {
        StringBuilder sb = new StringBuilder();
        inorden(raiz, sb);
        return resultado(sb);
    }

    private static void inorden(NodoVisual n, StringBuilder sb) {
        if (n == null) return;
        inorden(n.izquierdo(), sb);
        agregar(sb, n.etiqueta());
        inorden(n.derecho(), sb);
    }

    
    public static String postorden(NodoVisual raiz) {
        StringBuilder sb = new StringBuilder();
        postorden(raiz, sb);
        return resultado(sb);
    }

    private static void postorden(NodoVisual n, StringBuilder sb) {
        if (n == null) return;
        postorden(n.izquierdo(), sb);
        postorden(n.derecho(), sb);
        agregar(sb, n.etiqueta());
    }

    
    public static String porNiveles(NodoVisual raiz) {
        StringBuilder sb = new StringBuilder();
        if (raiz != null) {
            Cola<NodoVisual> cola = new Cola<>();
            cola.enqueue(raiz);
            while (!cola.estaVacia()) {
                NodoVisual n = cola.dequeue();
                agregar(sb, n.etiqueta());
                if (n.izquierdo() != null) cola.enqueue(n.izquierdo());
                if (n.derecho() != null) cola.enqueue(n.derecho());
            }
        }
        return resultado(sb);
    }

   
    public static int altura(NodoVisual n) {
        if (n == null) return 0;
        return 1 + Math.max(altura(n.izquierdo()), altura(n.derecho()));
    }

    public static int contar(NodoVisual n) {
        if (n == null) return 0;
        return 1 + contar(n.izquierdo()) + contar(n.derecho());
    }

    private static void agregar(StringBuilder sb, String etiqueta) {
        if (sb.length() > 0) sb.append(SEP);
        sb.append(etiqueta);
    }

    private static String resultado(StringBuilder sb) {
        return sb.length() == 0 ? "(arbol vacio)" : sb.toString();
    }
}