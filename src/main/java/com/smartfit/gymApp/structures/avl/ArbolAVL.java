package com.smartfit.gymApp.structures.avl;

import com.smartfit.gymApp.structures.arboles.NodoVisual;
import com.smartfit.gymApp.structures.arboles.Recorridos;

import java.util.function.Function;


public class ArbolAVL<T extends Comparable<T>> {

    private NodoAVL<T> raiz;
    private Function<T, String> etiquetador;
    private boolean cambio;
    private int comparaciones;
    private int totalRotaciones;
    private final StringBuilder bitacora = new StringBuilder();

    public void setEtiquetador(Function<T, String> etiquetador) {
        this.etiquetador = etiquetador;
    }

    private String etiquetar(T dato) {
        return etiquetador != null ? etiquetador.apply(dato) : String.valueOf(dato);
    }

   
    private int altura(NodoAVL<T> n) {
        return n == null ? 0 : n.getAltura();
    }

    private void actualizarAltura(NodoAVL<T> n) {
        n.setAltura(1 + Math.max(altura(n.getIzq()), altura(n.getDer())));
    }

    private int factor(NodoAVL<T> n) {
        return n == null ? 0 : n.factorEquilibrio();
    }

    
    private NodoAVL<T> rotarDerecha(NodoAVL<T> y) {
        NodoAVL<T> x = y.getIzq();
        NodoAVL<T> t2 = x.getDer();
        x.setDer(y);
        y.setIzq(t2);
        actualizarAltura(y);
        actualizarAltura(x);
        totalRotaciones++;
        return x;
    }

    
    private NodoAVL<T> rotarIzquierda(NodoAVL<T> x) {
        NodoAVL<T> y = x.getDer();
        NodoAVL<T> t2 = y.getIzq();
        y.setIzq(x);
        x.setDer(t2);
        actualizarAltura(x);
        actualizarAltura(y);
        totalRotaciones++;
        return y;
    }

    
    private NodoAVL<T> balancear(NodoAVL<T> n) {
        actualizarAltura(n);
        int fe = factor(n);

        if (fe > 1) {                                   
            if (factor(n.getIzq()) < 0) {               
                log("Desbalance en " + n.etiqueta() + " (FE=" + fe + "): rotacion DOBLE izquierda-derecha (LR)");
                n.setIzq(rotarIzquierda(n.getIzq()));
            } else {                                    
                log("Desbalance en " + n.etiqueta() + " (FE=" + fe + "): rotacion SIMPLE a la derecha (LL)");
            }
            return rotarDerecha(n);
        }
        if (fe < -1) {                                  
            if (factor(n.getDer()) > 0) {               
                log("Desbalance en " + n.etiqueta() + " (FE=" + fe + "): rotacion DOBLE derecha-izquierda (RL)");
                n.setDer(rotarDerecha(n.getDer()));
            } else {                                    
                log("Desbalance en " + n.etiqueta() + " (FE=" + fe + "): rotacion SIMPLE a la izquierda (RR)");
            }
            return rotarIzquierda(n);
        }
        return n;
    }

    
    public boolean insertar(T dato) {
        cambio = false;
        int posicion = bitacora.length();  
        raiz = insertar(raiz, dato);
        if (cambio) {
            bitacora.insert(posicion, "Insertado " + etiquetar(dato) + "\n");
        }
        return cambio;
    }

    private NodoAVL<T> insertar(NodoAVL<T> n, T dato) {
        if (n == null) {
            cambio = true;
            return new NodoAVL<>(dato, etiquetar(dato));
        }
        int c = dato.compareTo(n.getDato());
        if (c < 0) n.setIzq(insertar(n.getIzq(), dato));
        else if (c > 0) n.setDer(insertar(n.getDer(), dato));
        else return n; // duplicado
        return balancear(n);
    }

    
    public T buscar(T clave) {
        comparaciones = 0;
        NodoAVL<T> actual = raiz;
        while (actual != null) {
            comparaciones++;
            int c = clave.compareTo(actual.getDato());
            if (c == 0) return actual.getDato();
            actual = c < 0 ? actual.getIzq() : actual.getDer();
        }
        return null;
    }

    public boolean contiene(T clave) {
        return buscar(clave) != null;
    }

    public T minimo() {
        NodoAVL<T> n = minNodo(raiz);
        return n == null ? null : n.getDato();
    }

    public T maximo() {
        NodoAVL<T> n = raiz;
        if (n == null) return null;
        while (n.getDer() != null) n = n.getDer();
        return n.getDato();
    }

    private NodoAVL<T> minNodo(NodoAVL<T> n) {
        if (n == null) return null;
        while (n.getIzq() != null) n = n.getIzq();
        return n;
    }

    
    public boolean eliminar(T clave) {
        cambio = false;
        int posicion = bitacora.length();
        raiz = eliminar(raiz, clave);
        if (cambio) {
            bitacora.insert(posicion, "Eliminado " + etiquetar(clave) + "\n");
        }
        return cambio;
    }

    private NodoAVL<T> eliminar(NodoAVL<T> n, T clave) {
        if (n == null) return null;
        int c = clave.compareTo(n.getDato());
        if (c < 0) {
            n.setIzq(eliminar(n.getIzq(), clave));
        } else if (c > 0) {
            n.setDer(eliminar(n.getDer(), clave));
        } else {
            cambio = true;
            if (n.getIzq() == null) return n.getDer();
            if (n.getDer() == null) return n.getIzq();
            NodoAVL<T> sucesor = minNodo(n.getDer());
            n.setDato(sucesor.getDato(), sucesor.etiqueta());
            n.setDer(eliminar(n.getDer(), sucesor.getDato()));
        }
        return balancear(n);
    }

    
    public String preorden()   { return Recorridos.preorden(raiz); }
    public String inorden()    { return Recorridos.inorden(raiz); }
    public String postorden()  { return Recorridos.postorden(raiz); }
    public String porNiveles() { return Recorridos.porNiveles(raiz); }

    
    public boolean estaVacio()     { return raiz == null; }
    public int altura()            { return altura(raiz); }
    public int tamanio()           { return Recorridos.contar(raiz); }
    public int getComparaciones()  { return comparaciones; }
    public int getTotalRotaciones(){ return totalRotaciones; }
    public NodoVisual getRaizVisual() { return raiz; }

    public void vaciar() {
        raiz = null;
        totalRotaciones = 0;
        limpiarBitacora();
    }

    
    private void log(String mensaje) {
        bitacora.append(mensaje).append('\n');
    }

    public String getBitacora() {
        return bitacora.length() == 0 ? "(sin eventos)" : bitacora.toString();
    }

    public void limpiarBitacora() {
        bitacora.setLength(0);
    }
}