package com.smartfit.gymApp.structures.arboles;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ABBTest {

    @Test
    void ordenaClavesYRechazaDuplicados() {
        ABB<Integer> arbol = new ABB<>();
        for (int valor : new int[]{40, 20, 60, 10, 30, 50, 70}) {
            assertTrue(arbol.insertar(valor));
        }

        assertFalse(arbol.insertar(40));
        assertEquals(7, arbol.tamanio());
        assertEquals("10 -> 20 -> 30 -> 40 -> 50 -> 60 -> 70", arbol.inorden());
        assertEquals("40 -> 20 -> 10 -> 30 -> 60 -> 50 -> 70", arbol.preorden());
        assertEquals("10 -> 30 -> 20 -> 50 -> 70 -> 60 -> 40", arbol.postorden());
        assertEquals("40 -> 20 -> 60 -> 10 -> 30 -> 50 -> 70", arbol.porNiveles());
        assertEquals(10, arbol.minimo());
        assertEquals(70, arbol.maximo());
        assertEquals(30, arbol.buscar(30));
        assertEquals(3, arbol.getComparaciones());
        assertFalse(arbol.contiene(99));
    }

    @Test
    void eliminaHojaNodoConUnHijoYRaizConDosHijos() {
        ABB<Integer> arbol = new ABB<>();
        for (int valor : new int[]{40, 20, 60, 10, 30, 50, 70, 65}) arbol.insertar(valor);

        assertTrue(arbol.eliminar(10));
        assertTrue(arbol.eliminar(70));
        assertTrue(arbol.eliminar(40));
        assertFalse(arbol.eliminar(40));
        assertEquals("20 -> 30 -> 50 -> 60 -> 65", arbol.inorden());
        assertEquals(5, arbol.tamanio());
        assertEquals(50, arbol.buscar(50));
        assertEquals(65, arbol.buscar(65));
    }

    @Test
    void coincideConUnConjuntoOrdenadoDuranteAltasYBajas() {
        ABB<Integer> arbol = new ABB<>();
        TreeSet<Integer> esperado = new TreeSet<>();
        Random aleatorio = new Random(20261030L);

        for (int paso = 0; paso < 250; paso++) {
            int valor = aleatorio.nextInt(80);
            if (aleatorio.nextBoolean()) {
                assertEquals(esperado.add(valor), arbol.insertar(valor), "alta en paso " + paso);
            } else {
                assertEquals(esperado.remove(valor), arbol.eliminar(valor), "baja en paso " + paso);
            }
            assertEquals(esperado.size(), arbol.tamanio());
            assertEquals(recorrido(esperado), arbol.inorden());
            assertEquals(esperado.contains(valor), arbol.contiene(valor));
        }

        List<Integer> pendientes = new ArrayList<>(esperado);
        Collections.shuffle(pendientes, aleatorio);
        for (Integer valor : pendientes) assertTrue(arbol.eliminar(valor));
        assertTrue(arbol.estaVacio());
        assertNull(arbol.minimo());
        assertNull(arbol.maximo());
    }

    private static String recorrido(TreeSet<Integer> valores) {
        return valores.isEmpty() ? "(arbol vacio)"
                : valores.stream().map(String::valueOf).collect(Collectors.joining(" -> "));
    }
}
