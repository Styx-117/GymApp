package com.smartfit.gymApp.structures.avl;

import com.smartfit.gymApp.structures.arboles.NodoVisual;


public class NodoAVL<T> implements NodoVisual {

    private T dato;
    private String etiqueta;
    private NodoAVL<T> izq;
    private NodoAVL<T> der;
    private int altura = 1;

    public NodoAVL(T dato, String etiqueta) {
        this.dato = dato;
        this.etiqueta = etiqueta;
    }

    public T getDato() { return dato; }

    public void setDato(T dato, String etiqueta) {
        this.dato = dato;
        this.etiqueta = etiqueta;
    }

    public NodoAVL<T> getIzq() { return izq; }
    public void setIzq(NodoAVL<T> izq) { this.izq = izq; }
    public NodoAVL<T> getDer() { return der; }
    public void setDer(NodoAVL<T> der) { this.der = der; }
    public int getAltura() { return altura; }
    public void setAltura(int altura) { this.altura = altura; }

   
    public int factorEquilibrio() {
        int ai = izq == null ? 0 : izq.altura;
        int ad = der == null ? 0 : der.altura;
        return ai - ad;
    }

    @Override public String etiqueta() { return etiqueta; }
    @Override public NodoVisual izquierdo() { return izq; }
    @Override public NodoVisual derecho() { return der; }

    @Override
    public String detalle() {
        int fe = factorEquilibrio();
        return "h=" + altura + " FE=" + (fe > 0 ? "+" : "") + fe;
    }
}