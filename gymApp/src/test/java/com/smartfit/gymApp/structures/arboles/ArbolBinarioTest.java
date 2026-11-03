package com.smartfit.gymApp.structures.arboles;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArbolBinarioTest {

    @Test
    void insertaPorNivelesYProduceLosCuatroRecorridos() {
        ArbolBinario<Integer> arbol = new ArbolBinario<>();
        for (int valor = 1; valor <= 7; valor++) {
            assertTrue(arbol.insertar(valor));
        }

        assertAll(
                () -> assertEquals("1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 7", arbol.porNiveles()),
                () -> assertEquals("1 -> 2 -> 4 -> 5 -> 3 -> 6 -> 7", arbol.preorden()),
                () -> assertEquals("4 -> 2 -> 5 -> 1 -> 6 -> 3 -> 7", arbol.inorden()),
                () -> assertEquals("4 -> 5 -> 2 -> 6 -> 7 -> 3 -> 1", arbol.postorden()),
                () -> assertEquals(7, arbol.tamanio()),
                () -> assertEquals(3, arbol.altura()),
                () -> assertEquals(6, arbol.buscar(6)),
                () -> assertNull(arbol.buscar(99)));
    }

    @Test
    void eliminaRaizInternoYUltimaHojaSinPerderOtrosDatos() {
        ArbolBinario<Integer> arbol = new ArbolBinario<>();
        for (int valor = 1; valor <= 7; valor++) arbol.insertar(valor);

        assertTrue(arbol.eliminar(1));
        assertEquals("7 -> 2 -> 3 -> 4 -> 5 -> 6", arbol.porNiveles());
        assertTrue(arbol.eliminar(2));
        assertEquals("7 -> 6 -> 3 -> 4 -> 5", arbol.porNiveles());
        assertTrue(arbol.eliminar(5));
        assertEquals("7 -> 6 -> 3 -> 4", arbol.porNiveles());
        assertFalse(arbol.eliminar(99));
        assertEquals(4, arbol.tamanio());
    }

    @Test
    void permiteEliminarElUnicoNodoYReutilizarElArbol() {
        ArbolBinario<String> arbol = new ArbolBinario<>();
        assertFalse(arbol.eliminar("A"));
        assertTrue(arbol.insertar("A"));
        assertTrue(arbol.eliminar("A"));
        assertTrue(arbol.estaVacio());
        assertEquals(0, arbol.altura());
        assertNull(arbol.getRaizVisual());

        arbol.insertar("B");
        arbol.vaciar();
        assertEquals(0, arbol.tamanio());
        assertEquals("(arbol vacio)", arbol.preorden());
        arbol.insertar("C");
        assertEquals("C", arbol.inorden());
    }
}
