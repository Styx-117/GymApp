package com.smartfit.gymApp.structures.listas;

import java.util.List;
import java.util.Comparator;

public final class Ordenamientos {

    private Ordenamientos() {}

    public static <T extends Comparable<T>> void burbuja(List<T> lista) {
        for (int i = 0; i < lista.size() - 1; i++)
            for (int j = 0; j < lista.size() - i - 1; j++)
                if (lista.get(j).compareTo(lista.get(j + 1)) > 0) {
                    T tmp = lista.get(j);
                    lista.set(j, lista.get(j + 1));
                    lista.set(j + 1, tmp);
                }
    }

    public static <T> void burbuja(List<T> lista, Comparator<T> cmp) {
        for (int i = 0; i < lista.size() - 1; i++)
            for (int j = 0; j < lista.size() - i - 1; j++)
                if (cmp.compare(lista.get(j), lista.get(j + 1)) > 0) {
                    T tmp = lista.get(j);
                    lista.set(j, lista.get(j + 1));
                    lista.set(j + 1, tmp);
                }
    }

    public static <T extends Comparable<T>> void insercion(List<T> lista) {
        for (int i = 1; i < lista.size(); i++) {
            T clave = lista.get(i);
            int j = i - 1;
            while (j >= 0 && lista.get(j).compareTo(clave) > 0) {
                lista.set(j + 1, lista.get(j));
                j--;
            }
            lista.set(j + 1, clave);
        }
    }

    public static <T extends Comparable<T>> void seleccion(List<T> lista) {
        for (int i = 0; i < lista.size() - 1; i++) {
            int min = i;
            for (int j = i + 1; j < lista.size(); j++)
                if (lista.get(j).compareTo(lista.get(min)) < 0) min = j;
            if (min != i) {
                T tmp = lista.get(i);
                lista.set(i, lista.get(min));
                lista.set(min, tmp);
            }
        }
    }
}