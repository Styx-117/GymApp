package com.smartfit.gymApp.service;

import com.smartfit.gymApp.model.Clase;
import com.smartfit.gymApp.model.ReservaClase;
import com.smartfit.gymApp.structures.colas.Cola;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;


public class GestorReservasClase implements Comparable<GestorReservasClase> {

    private final Clase clase;
    private final Cola<ReservaClase> confirmadas = new Cola<>();
    private final Cola<ReservaClase> listaEspera = new Cola<>();
    private ReservaClase ultimaPromovida;

    public GestorReservasClase(Clase clase) {
        this.clase = clase;
    }

    
    static GestorReservasClase clave(int idClase) {
        return new GestorReservasClase(new Clase(idClase, "", 0, "", null, null, 0, 0));
    }

   
    public ReservaClase reservar(int idReserva, String codigoSocio) {
        if (estaInscrito(codigoSocio)) {
            return null;
        }
        ReservaClase r = new ReservaClase(idReserva, clase.getIdClase(), codigoSocio,
                LocalDate.now(), "CONFIRMADA", -1);
        if (clase.tieneCupoDisponible()) {
            confirmadas.enqueue(r);
            clase.setCupoActual(clase.getCupoActual() + 1);
        } else {
            r.setEstado("LISTA_ESPERA");
            r.setPosicionListaEspera(listaEspera.tamanio() + 1);
            listaEspera.enqueue(r);
        }
        return r;
    }

    
    public ReservaClase cancelar(String codigoSocio) {
        return cancelar(codigoSocio, codigo -> true);
    }

    public ReservaClase cancelar(String codigoSocio, Predicate<String> socioElegible) {
        ultimaPromovida = null;
        ReservaClase cancelada = confirmadas.quitarSi(r -> r.getCodigoSocio().equals(codigoSocio));
        if (cancelada != null) {
            cancelada.setEstado("CANCELADA");
            clase.setCupoActual(clase.getCupoActual() - 1);
            ReservaClase promovida = listaEspera.quitarSi(r -> socioElegible.test(r.getCodigoSocio()));
            if (promovida != null) {
                promovida.setEstado("CONFIRMADA");
                promovida.setPosicionListaEspera(-1);
                confirmadas.enqueue(promovida);
                clase.setCupoActual(clase.getCupoActual() + 1);
                ultimaPromovida = promovida;
                renumerarEspera();
            }
            return cancelada;
        }
        cancelada = listaEspera.quitarSi(r -> r.getCodigoSocio().equals(codigoSocio));
        if (cancelada != null) {
            cancelada.setEstado("CANCELADA");
            cancelada.setPosicionListaEspera(-1);
            renumerarEspera();
        }
        return cancelada;
    }

    /** Reconstruye las colas desde registros persistidos sin crear reservas nuevas. */
    public void restaurar(List<ReservaClase> reservas) {
        confirmadas.vaciar();
        listaEspera.vaciar();
        ultimaPromovida = null;
        reservas.stream().filter(r -> "CONFIRMADA".equals(r.getEstado()))
                .sorted(Comparator.comparingInt(ReservaClase::getIdReserva)).forEach(confirmadas::enqueue);
        reservas.stream().filter(r -> "LISTA_ESPERA".equals(r.getEstado()))
                .sorted(Comparator.comparingInt(ReservaClase::getPosicionListaEspera)
                        .thenComparingInt(ReservaClase::getIdReserva)).forEach(listaEspera::enqueue);
        clase.setCupoActual(confirmadas.tamanio());
        renumerarEspera();
    }

    /** Completa cupos con los primeros socios elegibles; los inactivos conservan su historial. */
    public void promoverDisponibles(Predicate<String> socioElegible) {
        while (clase.tieneCupoDisponible()) {
            ReservaClase reserva=listaEspera.quitarSi(r -> socioElegible.test(r.getCodigoSocio()));
            if (reserva==null) break;
            reserva.setEstado("CONFIRMADA");
            reserva.setPosicionListaEspera(-1);
            confirmadas.enqueue(reserva);
            clase.setCupoActual(clase.getCupoActual()+1);
            ultimaPromovida=reserva;
        }
        renumerarEspera();
    }

    public boolean actualizarCodigoSocio(String codigoActual, String codigoNuevo) {
        if (codigoActual == null || codigoActual.isBlank()
                || codigoNuevo == null || codigoNuevo.isBlank()
                || (!codigoActual.equals(codigoNuevo) && estaInscrito(codigoNuevo))) {
            return false;
        }
        ReservaClase reserva = confirmadas.buscar(r -> r.getCodigoSocio().equals(codigoActual));
        if (reserva == null) {
            reserva = listaEspera.buscar(r -> r.getCodigoSocio().equals(codigoActual));
        }
        if (reserva == null) {
            return false;
        }
        reserva.setCodigoSocio(codigoNuevo);
        return true;
    }

    private void renumerarEspera() {
        int[] pos = {1};
        listaEspera.recorrer(r -> r.setPosicionListaEspera(pos[0]++));
    }

    public boolean estaInscrito(String codigoSocio) {
        return confirmadas.buscar(r -> r.getCodigoSocio().equals(codigoSocio)) != null
                || listaEspera.buscar(r -> r.getCodigoSocio().equals(codigoSocio)) != null;
    }

    public Clase getClase() { return clase; }
    public Cola<ReservaClase> getConfirmadas() { return confirmadas; }
    public Cola<ReservaClase> getListaEspera() { return listaEspera; }
    public ReservaClase getUltimaPromovida() { return ultimaPromovida; }

    @Override
    public int compareTo(GestorReservasClase otro) {
        return Integer.compare(this.clase.getIdClase(), otro.clase.getIdClase());
    }
}
