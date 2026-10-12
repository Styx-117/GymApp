package com.smartfit.gymApp.structures.arreglos;

import com.smartfit.gymApp.model.Socio;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Predicate;

public class ArregloSocios {
    private Socio[] datos;
    private int tamanio;

    public ArregloSocios(int capacidad) {
        datos = new Socio[Math.max(1, capacidad)];
    }

    public void agregar(Socio s) {
        if (tamanio == datos.length) datos = Arrays.copyOf(datos, datos.length * 2);
        datos[tamanio++] = s;
    }

    public Socio buscar(Predicate<Socio> cond) {
        for (int i = 0; i < tamanio; i++) if (cond.test(datos[i])) return datos[i];
        return null;
    }

    public boolean eliminar(Predicate<Socio> cond) {
        for (int i = 0; i < tamanio; i++) {
            if (cond.test(datos[i])) {
                System.arraycopy(datos, i + 1, datos, i, tamanio - i - 1);
                datos[--tamanio] = null;
                return true;
            }
        }
        return false;
    }

    public void ordenar(Comparator<Socio> cmp) {
        Arrays.sort(datos, 0, tamanio, cmp);
    }

    /** Búsqueda binaria asumiendo que el arreglo ya está ordenado por el Comparator. */
    public Socio busquedaBinaria(Socio clave, Comparator<Socio> cmp) {
        int lo = 0, hi = tamanio - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            int c = cmp.compare(datos[mid], clave);
            if (c == 0) return datos[mid];
            if (c < 0) lo = mid + 1;
            else hi = mid - 1;
        }
        return null;
    }

    public int tamanio() { return tamanio; }
    public Socio get(int i) { return datos[i]; }
}