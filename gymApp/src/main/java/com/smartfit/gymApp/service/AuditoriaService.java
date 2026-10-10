package com.smartfit.gymApp.service;

import com.smartfit.gymApp.model.Auditoria;
import com.smartfit.gymApp.structures.pilas.Pila;

import java.time.LocalDateTime;
import java.util.function.Consumer;


public class AuditoriaService {

    private static AuditoriaService instancia;

    private final Pila<Auditoria> historial = new Pila<>();
    private int contador = 0;
    private int usuarioActual = 0;

    private AuditoriaService() {}

    public static synchronized AuditoriaService getInstance() {
        if (instancia == null) {
            instancia = new AuditoriaService();
        }
        return instancia;
    }

    
    public void setUsuarioActual(int idUsuario) {
        this.usuarioActual = idUsuario;
    }

    public Auditoria registrar(String accion, String detalle) {
        return registrar(usuarioActual, accion, detalle);
    }

    public Auditoria registrar(int idUsuario, String accion, String detalle) {
        Auditoria a = new Auditoria(++contador, idUsuario, accion, LocalDateTime.now(), detalle);
        historial.push(a);
        return a;
    }

   
    public Auditoria ultima() {
        return historial.peek();
    }

    
    public Auditoria revertirUltima(Consumer<Auditoria> deshacer) {
        Auditoria a = historial.pop();
        if (a != null && deshacer != null) {
            deshacer.accept(a);
        }
        return a;
    }

    public boolean hayAcciones() {
        return !historial.estaVacia();
    }

    public int total() {
        return historial.tamanio();
    }

    public Pila<Auditoria> getHistorial() {
        return historial;
    }
}