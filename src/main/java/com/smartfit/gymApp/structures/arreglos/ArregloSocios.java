package com.smartfit.gymApp.structures.arreglos;

import com.smartfit.gymApp.model.Socio;
import java.util.Comparator;
import java.util.function.Predicate;

public class ArregloSocios {
    private Socio[] datos;
    private int tamanio;

    public ArregloSocios(int capacidad) {
        datos = new Socio[Math.max(1, capacidad)];
    }

    public void agregar(Socio s) {
        if (tamanio == datos.length) {
            Socio[] ampliado = new Socio[datos.length * 2];
            for (int i = 0; i < tamanio; i++) ampliado[i] = datos[i];
            datos = ampliado;
        }
        datos[tamanio++] = s;
    }

    public Socio buscar(Predicate<Socio> cond) {
        for (int i = 0; i < tamanio; i++) if (cond.test(datos[i])) return datos[i];
        return null;
    }

    public boolean eliminar(Predicate<Socio> cond) {
        for (int i = 0; i < tamanio; i++) {
            if (cond.test(datos[i])) {
                for (int j = i; j < tamanio - 1; j++) datos[j] = datos[j + 1];
                datos[--tamanio] = null;
                return true;
            }
        }
        return false;
    }

    public void ordenar(Comparator<Socio> cmp) {
        // Merge sort propio: O(n log n), estable y sin delegar el algoritmo a colecciones.
        Socio[] auxiliar = new Socio[tamanio];
        ordenar(0, tamanio, cmp, auxiliar);
    }

    private void ordenar(int inicio, int fin, Comparator<Socio> cmp, Socio[] auxiliar) {
        if (fin - inicio < 2) return;
        int medio = (inicio + fin) >>> 1;
        ordenar(inicio, medio, cmp, auxiliar); ordenar(medio, fin, cmp, auxiliar);
        int i = inicio, j = medio;
        for (int k = inicio; k < fin; k++) {
            auxiliar[k] = j >= fin || (i < medio && cmp.compare(datos[i], datos[j]) <= 0) ? datos[i++] : datos[j++];
        }
        for (int k = inicio; k < fin; k++) datos[k] = auxiliar[k];
    }

    public ArregloSocios copiar() {
        ArregloSocios copia = new ArregloSocios(tamanio);
        for (int i = 0; i < tamanio; i++) copia.agregar(datos[i]);
        return copia;
    }

    public boolean actualizar(Socio socio) {
        for (int i = 0; i < tamanio; i++) if (datos[i].getCodigoSocio().equals(socio.getCodigoSocio())) {
            datos[i] = socio; return true;
        }
        return false;
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
