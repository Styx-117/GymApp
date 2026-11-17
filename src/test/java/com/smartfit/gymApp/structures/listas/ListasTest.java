package com.smartfit.gymApp.structures.listas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListasTest {

    @Test
    void simpleBuscaOrdenaYEliminaEnLosExtremosYElInterior() {
        ListaEnlazadaSimple<Integer> lista = new ListaEnlazadaSimple<>();
        assertFalse(lista.eliminar(v -> true));
        lista.agregarFinal(30);
        lista.agregarInicio(40);
        lista.agregarFinal(10);
        lista.agregarInicio(20);
        lista.ordenar(Comparator.naturalOrder());
        assertEquals(List.of(10, 20, 30, 40), lista.aLista());
        assertEquals(30, lista.buscar(v -> v == 30));
        assertNull(lista.buscar(v -> v == 99));

        assertTrue(lista.eliminar(v -> v == 10));
        assertTrue(lista.eliminar(v -> v == 30));
        assertTrue(lista.eliminar(v -> v == 40));
        assertEquals(List.of(20), lista.aLista());
        assertEquals(1, lista.tamanio());
        assertTrue(lista.eliminar(v -> v == 20));
        assertTrue(lista.estaVacia());
        lista.agregarFinal(7);
        lista.vaciar();
        assertEquals(0, lista.tamanio());
    }

    @Test
    void dobleMantieneAmbosSentidosDespuesDeEliminarNodos() {
        ListaDoblementeEnlazada<Integer> lista = new ListaDoblementeEnlazada<>();
        lista.agregarFinal(2);
        lista.agregarInicio(1);
        lista.agregarFinal(3);
        lista.agregarFinal(4);
        lista.agregarFinal(5);
        assertEquals(3, lista.buscar(v -> v == 3));
        assertNull(lista.buscar(v -> v == 99));
        assertTrue(lista.eliminar(v -> v == 1));
        assertTrue(lista.eliminar(v -> v == 3));
        assertTrue(lista.eliminar(v -> v == 5));
        List<Integer> adelante = new ArrayList<>();
        List<Integer> atras = new ArrayList<>();
        lista.recorrerAdelante(adelante::add);
        lista.recorrerAtras(atras::add);
        assertEquals(List.of(2, 4), adelante);
        assertEquals(List.of(4, 2), atras);
        assertEquals(2, lista.tamanio());
        assertTrue(lista.eliminar(v -> v == 2));
        assertTrue(lista.eliminar(v -> v == 4));
        assertTrue(lista.estaVacia());
        assertFalse(lista.eliminar(v -> true));
        lista.agregarInicio(6);
        assertEquals(6, lista.buscar(v -> true));
        lista.vaciar();
        assertEquals(0, lista.tamanio());
    }

    @Test
    @Timeout(2)
    void circularCierraElRecorridoYAdmiteEliminarPrimeroUltimoYUnico() {
        ListaCircular<Integer> lista = new ListaCircular<>();
        assertFalse(lista.eliminar(v -> true));
        for (int valor : new int[]{1, 2, 3, 4}) lista.agregar(valor);
        assertTrue(lista.eliminar(v -> v == 1));
        assertTrue(lista.eliminar(v -> v == 4));
        lista.agregar(5);
        assertNull(lista.buscar(v -> v == 99));
        List<Integer> recorrido = new ArrayList<>();
        lista.recorrer(recorrido::add);
        assertEquals(List.of(2, 3, 5), recorrido);
        assertEquals(3, lista.tamanio());
        assertTrue(lista.eliminar(v -> v == 3));
        assertTrue(lista.eliminar(v -> v == 2));
        assertTrue(lista.eliminar(v -> v == 5));
        assertTrue(lista.estaVacia());
        lista.agregar(6);
        assertEquals(6, lista.buscar(v -> true));
        lista.vaciar();
        assertEquals(0, lista.tamanio());
    }

    @Test
    void ordenamientosCoincidenConElOrdenEsperadoIncluyendoDuplicadosYNegativos() {
        List<Integer> datos = List.of(8, -3, 8, 0, 2, -1);
        List<Integer> esperado = List.of(-3, -1, 0, 2, 8, 8);
        List<Integer> burbuja = new ArrayList<>(datos);
        List<Integer> insercion = new ArrayList<>(datos);
        List<Integer> seleccion = new ArrayList<>(datos);
        Ordenamientos.burbuja(burbuja);
        Ordenamientos.insercion(insercion);
        Ordenamientos.seleccion(seleccion);
        assertEquals(esperado, burbuja);
        assertEquals(esperado, insercion);
        assertEquals(esperado, seleccion);

        Ordenamientos.burbuja(burbuja, Comparator.reverseOrder());
        assertEquals(List.of(8, 8, 2, 0, -1, -3), burbuja);
        List<Integer> vacia = new ArrayList<>();
        Ordenamientos.burbuja(vacia);
        Ordenamientos.insercion(vacia);
        Ordenamientos.seleccion(vacia);
        assertTrue(vacia.isEmpty());
    }

    @Test
    void circularRotaYSeAjustaAlEliminar() {
        ListaCircular<String> lista = new ListaCircular<>();
        assertNull(lista.siguiente());
        lista.agregar("A");
        lista.agregar("B");
        lista.agregar("C");
        assertEquals("A", lista.siguiente());
        assertEquals("B", lista.siguiente());
        assertTrue(lista.eliminar(v -> v.equals("B")));
        assertEquals("C", lista.siguiente());
        assertEquals("A", lista.siguiente());
        lista.vaciar();
        assertNull(lista.siguiente());
    }
}
