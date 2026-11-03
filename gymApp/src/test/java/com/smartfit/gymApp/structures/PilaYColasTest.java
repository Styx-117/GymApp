package com.smartfit.gymApp.structures;

import com.smartfit.gymApp.structures.colas.Cola;
import com.smartfit.gymApp.structures.colas.ColaPrioridad;
import com.smartfit.gymApp.structures.pilas.Pila;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PilaYColasTest {

    @Test
    void pilaRespetaLifoYSuConsultaNoConsumeElTope() {
        Pila<String> pila = new Pila<>();
        assertNull(pila.pop());
        assertNull(pila.peek());
        pila.push("registrar");
        pila.push("actualizar");
        pila.push("eliminar");

        assertEquals("eliminar", pila.peek());
        assertEquals(3, pila.tamanio());
        List<String> recorrido = new ArrayList<>();
        pila.recorrer(recorrido::add);
        assertEquals(List.of("eliminar", "actualizar", "registrar"), recorrido);
        assertEquals("eliminar", pila.pop());
        assertEquals("actualizar", pila.pop());
        assertEquals("registrar", pila.pop());
        assertTrue(pila.estaVacia());
        assertEquals(0, pila.tamanio());
        pila.push("nueva");
        pila.vaciar();
        assertNull(pila.peek());
        assertEquals(0, pila.tamanio());
    }

    @Test
    void colaMantieneFifoAlQuitarFrenteMedioFinalYVolverAEncolar() {
        Cola<Integer> cola = new Cola<>();
        assertNull(cola.dequeue());
        for (int valor : new int[]{1, 2, 3, 4, 5}) cola.enqueue(valor);

        assertEquals(1, cola.quitarSi(v -> v == 1));
        assertEquals(3, cola.quitarSi(v -> v == 3));
        assertEquals(5, cola.quitarSi(v -> v == 5));
        assertNull(cola.quitarSi(v -> v == 99));
        cola.enqueue(6);
        assertEquals(3, cola.tamanio());
        assertEquals(2, cola.frente());
        assertEquals(4, cola.buscar(v -> v == 4));

        List<Integer> recorrido = new ArrayList<>();
        cola.recorrer(recorrido::add);
        assertEquals(List.of(2, 4, 6), recorrido);
        assertEquals(2, cola.dequeue());
        assertEquals(4, cola.dequeue());
        assertEquals(6, cola.quitarSi(v -> true));
        assertTrue(cola.estaVacia());
        assertEquals(0, cola.tamanio());
        cola.enqueue(7);
        assertEquals(7, cola.dequeue());
        assertNull(cola.frente());
    }

    @Test
    void colaVaciadaPuedeRecibirUnaNuevaSecuencia() {
        Cola<Integer> cola = new Cola<>();
        cola.enqueue(1);
        cola.enqueue(2);
        cola.vaciar();
        assertEquals(0, cola.tamanio());
        assertNull(cola.frente());
        cola.enqueue(3);
        cola.enqueue(4);
        assertEquals(3, cola.dequeue());
        assertEquals(4, cola.dequeue());
        assertTrue(cola.estaVacia());
    }

    @Test
    void prioridadAtiendeUrgentesYConservaLlegadaEntreEmpates() {
        ColaPrioridad<Turno> cola = new ColaPrioridad<>();
        Turno baja = new Turno("baja", 3);
        Turno altaPrimera = new Turno("alta-primera", 1);
        Turno media = new Turno("media", 2);
        Turno altaSegunda = new Turno("alta-segunda", 1);
        cola.encolar(baja);
        cola.encolar(altaPrimera);
        cola.encolar(media);
        cola.encolar(altaSegunda);

        List<Turno> recorrido = new ArrayList<>();
        cola.recorrer(recorrido::add);
        assertEquals(List.of(altaPrimera, altaSegunda, media, baja), recorrido);
        assertSame(altaPrimera, cola.frente());
        assertEquals(4, cola.tamanio());
        assertSame(altaPrimera, cola.desencolar());
        assertSame(altaSegunda, cola.desencolar());
        assertSame(media, cola.desencolar());
        assertSame(baja, cola.desencolar());
        assertNull(cola.desencolar());
        assertEquals(0, cola.tamanio());
        cola.encolar(media);
        cola.vaciar();
        assertTrue(cola.estaVacia());
        assertNull(cola.frente());
    }

    private record Turno(String identificador, int prioridad) implements Comparable<Turno> {
        @Override
        public int compareTo(Turno otro) {
            return Integer.compare(prioridad, otro.prioridad);
        }
    }
}
