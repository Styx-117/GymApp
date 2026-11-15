package com.smartfit.gymApp.structures.avl;

import com.smartfit.gymApp.structures.arboles.NodoVisual;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ArbolAVLTest {

    @ParameterizedTest(name = "Rotacion {3}: {0}, {1}, {2}")
    @CsvSource({"30,20,10,LL,1", "10,20,30,RR,1", "30,10,20,LR,2", "10,30,20,RL,2"})
    void realizaLosCuatroTiposDeRotacion(int primero, int segundo, int tercero,
                                        String tipo, int rotaciones) {
        ArbolAVL<Integer> arbol = new ArbolAVL<>();
        for (int valor : new int[]{primero, segundo, tercero}) assertTrue(arbol.insertar(valor));

        assertEquals("20", arbol.getRaizVisual().etiqueta());
        assertEquals("10 -> 20 -> 30", arbol.inorden());
        assertEquals("20 -> 10 -> 30", arbol.preorden());
        assertEquals("10 -> 30 -> 20", arbol.postorden());
        assertEquals("20 -> 10 -> 30", arbol.porNiveles());
        assertEquals(rotaciones, arbol.getTotalRotaciones());
        assertTrue(arbol.getBitacora().contains("(" + tipo + ")"));
        assertEquals(2, verificarBalance(arbol.getRaizVisual(), null, null));
    }

    @Test
    void rebalanceaAlEliminarYConservaLasClavesRestantes() {
        ArbolAVL<Integer> arbol = new ArbolAVL<>();
        for (int valor : new int[]{9, 5, 10, 0, 6, 11, -1, 1, 2}) arbol.insertar(valor);
        int rotacionesAntes = arbol.getTotalRotaciones();

        assertTrue(arbol.eliminar(10));
        assertEquals("-1 -> 0 -> 1 -> 2 -> 5 -> 6 -> 9 -> 11", arbol.inorden());
        assertEquals("1", arbol.getRaizVisual().etiqueta());
        assertTrue(arbol.getTotalRotaciones() > rotacionesAntes);
        assertEquals(arbol.altura(), verificarBalance(arbol.getRaizVisual(), null, null));
    }

    @Test
    void duplicadoYClaveInexistenteNoAlteranContenidoNiRotaciones() {
        ArbolAVL<Integer> arbol = new ArbolAVL<>();
        arbol.insertar(20);
        arbol.insertar(10);
        arbol.insertar(30);
        String bitacora = arbol.getBitacora();

        assertFalse(arbol.insertar(20));
        assertFalse(arbol.eliminar(99));
        assertEquals(3, arbol.tamanio());
        assertEquals(bitacora, arbol.getBitacora());
        assertEquals(0, arbol.getTotalRotaciones());
        assertEquals(10, arbol.minimo());
        assertEquals(30, arbol.maximo());
        assertEquals(30, arbol.buscar(30));
        assertEquals(2, arbol.getComparaciones());
        assertNull(arbol.buscar(99));
    }

    @Test
    void mantieneOrdenAlturasYBalanceDuranteAltasYBajasReproducibles() {
        ArbolAVL<Integer> arbol = new ArbolAVL<>();
        TreeSet<Integer> esperado = new TreeSet<>();
        Random aleatorio = new Random(20261030L);

        for (int paso = 0; paso < 400; paso++) {
            int valor = aleatorio.nextInt(120);
            if (aleatorio.nextBoolean()) {
                assertEquals(esperado.add(valor), arbol.insertar(valor), "alta en paso " + paso);
            } else {
                assertEquals(esperado.remove(valor), arbol.eliminar(valor), "baja en paso " + paso);
            }
            verificarContenido(arbol, esperado);
            assertEquals(esperado.contains(valor), arbol.contiene(valor));
        }

        List<Integer> pendientes = new ArrayList<>(esperado);
        Collections.shuffle(pendientes, aleatorio);
        for (Integer valor : pendientes) {
            assertTrue(arbol.eliminar(valor));
            esperado.remove(valor);
            verificarContenido(arbol, esperado);
        }
        assertTrue(arbol.estaVacio());
        assertNull(arbol.minimo());
        assertNull(arbol.maximo());
    }

    @Test
    void vaciarReiniciaContenidoBitacoraYRotaciones() {
        ArbolAVL<Integer> arbol = new ArbolAVL<>();
        for (int valor : new int[]{1, 2, 3}) arbol.insertar(valor);
        assertTrue(arbol.getTotalRotaciones() > 0);

        arbol.vaciar();
        assertEquals(0, arbol.tamanio());
        assertEquals(0, arbol.altura());
        assertEquals(0, arbol.getTotalRotaciones());
        assertEquals("(sin eventos)", arbol.getBitacora());
        assertTrue(arbol.insertar(7));
        assertEquals("7", arbol.inorden());
    }

    private static void verificarContenido(ArbolAVL<Integer> arbol, TreeSet<Integer> esperado) {
        String recorrido = esperado.isEmpty() ? "(arbol vacio)"
                : esperado.stream().map(String::valueOf).collect(Collectors.joining(" -> "));
        assertEquals(recorrido, arbol.inorden());
        assertEquals(esperado.size(), arbol.tamanio());
        assertEquals(arbol.altura(), verificarBalance(arbol.getRaizVisual(), null, null));
    }

    // La altura esperada se calcula desde la forma real, sin confiar en la altura almacenada.
    private static int verificarBalance(NodoVisual nodo, Integer minimo, Integer maximo) {
        if (nodo == null) return 0;
        int valor = Integer.parseInt(nodo.etiqueta());
        if (minimo != null) assertTrue(valor > minimo, "orden izquierdo/derecho en " + valor);
        if (maximo != null) assertTrue(valor < maximo, "orden izquierdo/derecho en " + valor);
        int izquierda = verificarBalance(nodo.izquierdo(), minimo, valor);
        int derecha = verificarBalance(nodo.derecho(), valor, maximo);
        assertTrue(Math.abs(izquierda - derecha) <= 1, "desbalance en " + valor);
        int altura = 1 + Math.max(izquierda, derecha);
        NodoAVL<?> nodoAVL = (NodoAVL<?>) nodo;
        assertEquals(altura, nodoAVL.getAltura(), "altura almacenada en " + valor);
        assertEquals(izquierda - derecha, nodoAVL.factorEquilibrio(), "factor en " + valor);
        return altura;
    }
}
