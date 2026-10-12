package com.smartfit.gymApp.service;

import com.smartfit.gymApp.model.Clase;
import com.smartfit.gymApp.model.ReservaClase;
import com.smartfit.gymApp.structures.arboles.ABB;


public class ReservaService {

    private static ReservaService instancia;

    private final ABB<GestorReservasClase> gestores = new ABB<>();
    private int contadorReservas = 0;

    private ReservaService() {
        gestores.setEtiquetador(g -> "C" + g.getClase().getIdClase());
    }

    public static synchronized ReservaService getInstance() {
        if (instancia == null) {
            instancia = new ReservaService();
        }
        return instancia;
    }

   
    public GestorReservasClase registrarClase(Clase clase) {
        GestorReservasClase existente = gestores.buscar(GestorReservasClase.clave(clase.getIdClase()));
        if (existente != null) {
            return existente;
        }
        GestorReservasClase nuevo = new GestorReservasClase(clase);
        gestores.insertar(nuevo);
        return nuevo;
    }

    public GestorReservasClase gestorDe(int idClase) {
        return gestores.buscar(GestorReservasClase.clave(idClase));
    }

    
    public ReservaClase reservar(Clase clase, String codigoSocio) {
        GestorReservasClase g = registrarClase(clase);
        ReservaClase r = g.reservar(++contadorReservas, codigoSocio);
        if (r == null) {
            contadorReservas--;
            return null;
        }
        AuditoriaService.getInstance().registrar("RESERVAR_CLASE",
                codigoSocio + " -> clase " + clase.getIdClase() + " (" + r.getEstado() + ")");
        return r;
    }

    
    public ReservaClase cancelar(int idClase, String codigoSocio) {
        GestorReservasClase g = gestorDe(idClase);
        if (g == null) {
            return null;
        }
        ReservaClase r = g.cancelar(codigoSocio);
        if (r != null) {
            AuditoriaService.getInstance().registrar("CANCELAR_RESERVA",
                    codigoSocio + " de clase " + idClase
                            + (g.getUltimaPromovida() != null
                            ? " (asciende " + g.getUltimaPromovida().getCodigoSocio() + ")" : ""));
        }
        return r;
    }

    public boolean actualizarCodigoSocio(int idClase, String codigoActual, String codigoNuevo) {
        GestorReservasClase gestor = gestorDe(idClase);
        if (gestor == null || !gestor.actualizarCodigoSocio(codigoActual, codigoNuevo)) {
            return false;
        }
        AuditoriaService.getInstance().registrar("ACTUALIZAR_RESERVA",
                codigoActual + " -> " + codigoNuevo + " en clase " + idClase);
        return true;
    }

    public ABB<GestorReservasClase> getGestores() {
        return gestores;
    }
}