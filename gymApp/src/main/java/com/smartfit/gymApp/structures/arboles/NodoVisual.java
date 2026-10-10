package com.smartfit.gymApp.structures.arboles;


public interface NodoVisual {

    
    String etiqueta();

    NodoVisual izquierdo();

    NodoVisual derecho();

    
    default String detalle() {
        return "";
    }
}