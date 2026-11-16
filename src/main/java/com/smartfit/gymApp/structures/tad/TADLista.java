package com.smartfit.gymApp.structures.tad;

/**
 * TAD Lista genérica. Define el contrato mínimo para cualquier implementación
 * (enlazada simple, doble o circular).
 */
public interface TADLista<T> {
    void agregar(T dato);
    boolean eliminar(java.util.function.Predicate<T> condicion);
    T buscar(java.util.function.Predicate<T> condicion);
    void recorrer(java.util.function.Consumer<T> accion);
    int tamanio();
    boolean estaVacia();
    void vaciar();
}