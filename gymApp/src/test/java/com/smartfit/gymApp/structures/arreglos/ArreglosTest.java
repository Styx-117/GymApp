package com.smartfit.gymApp.structures.arreglos;

import com.smartfit.gymApp.model.Clase;
import com.smartfit.gymApp.model.Socio;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class ArreglosTest {

    @Test
    void arregloCreceOrdenaYBuscaSinIncluirPosicionesLibres() {
        ArregloSocios socios = new ArregloSocios(1);
        Socio tercero = socio("SM-0003");
        Socio primero = socio("SM-0001");
        Socio segundo = socio("SM-0002");
        socios.agregar(tercero);
        socios.agregar(primero);
        socios.agregar(segundo);
        assertEquals(3, socios.tamanio());
        assertSame(segundo, socios.buscar(s -> s.getCodigoSocio().equals("SM-0002")));
        assertNull(socios.buscar(s -> s.getCodigoSocio().equals("SM-0099")));

        Comparator<Socio> porCodigo = Comparator.naturalOrder();
        socios.ordenar(porCodigo);
        assertSame(primero, socios.get(0));
        assertSame(segundo, socios.get(1));
        assertSame(tercero, socios.get(2));
        assertSame(segundo, socios.busquedaBinaria(socio("SM-0002"), porCodigo));
        assertNull(socios.busquedaBinaria(socio("SM-0099"), porCodigo));
        assertTrue(socios.eliminar(s -> s.getCodigoSocio().equals("SM-0002")));
        assertSame(tercero, socios.get(1));
        assertEquals(2, socios.tamanio());
        assertSame(tercero, socios.busquedaBinaria(socio("SM-0003"), porCodigo));
        assertFalse(socios.eliminar(s -> s.getCodigoSocio().equals("SM-0099")));
    }

    @Test
    void arregloDeCapacidadCeroAdmiteAltaYBajaHastaQuedarVacio() {
        ArregloSocios socios = new ArregloSocios(0);
        assertNull(socios.busquedaBinaria(socio("SM-0001"), Comparator.naturalOrder()));
        socios.agregar(socio("SM-0001"));
        assertTrue(socios.eliminar(s -> true));
        assertEquals(0, socios.tamanio());
        assertNull(socios.buscar(s -> true));
        assertFalse(socios.eliminar(s -> true));
    }

    @Test
    void matrizUbicaPorDiaYHoraRechazaColisionYSeLimpia() {
        MatrizHorarios matriz = new MatrizHorarios();
        Clase miercoles = clase(1, "MIÉRCOLES", 7);
        Clase sabado = clase(2, "SÁBADO", 23);
        assertTrue(matriz.asignar(miercoles));
        assertTrue(matriz.asignar(sabado));
        assertSame(miercoles, matriz.obtener(" miercoles ", 7));
        assertSame(sabado, matriz.obtener("sabado", 23));
        assertFalse(matriz.asignar(clase(3, "MIERCOLES", 7)));
        assertFalse(matriz.asignar(clase(4, "FESTIVO", 7)));
        assertNull(matriz.obtener(null, 7));
        assertNull(matriz.obtener("LUNES", -1));
        assertNull(matriz.obtener("LUNES", 24));
        matriz.limpiar();
        assertNull(matriz.obtener("MIERCOLES", 7));
        assertTrue(matriz.asignar(miercoles));
    }

    private static Socio socio(String codigo) {
        return new Socio(codigo, "Socio", "De prueba", "00000000");
    }

    private static Clase clase(int id, String dia, int hora) {
        return new Clase(id, "Clase de prueba", 0, dia, LocalTime.of(hora, 0),
                LocalTime.of(hora, 0).plusHours(1), 10, 0);
    }
}
