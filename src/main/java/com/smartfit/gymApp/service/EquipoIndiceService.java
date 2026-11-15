package com.smartfit.gymApp.service;

import com.smartfit.gymApp.model.Equipo;
import com.smartfit.gymApp.structures.arboles.ABB;


public class EquipoIndiceService {

    private static EquipoIndiceService instancia;

    private final ABB<Equipo> indice = new ABB<>();

    private EquipoIndiceService() {
        indice.setEtiquetador(Equipo::getCodigoEquipo);
    }

    public static synchronized EquipoIndiceService getInstance() {
        if (instancia == null) {
            instancia = new EquipoIndiceService();
        }
        return instancia;
    }

    public void cargar(Iterable<Equipo> equipos) {
        indice.vaciar();
        for (Equipo e : equipos) {
            indice.insertar(e);
        }
    }

    public boolean agregar(Equipo equipo) {
        return indice.insertar(equipo);
    }

    public Equipo buscarPorCodigo(String codigoEquipo) {
        return indice.buscar(new Equipo(codigoEquipo, null, null, null, null, null));
    }

    public boolean eliminar(String codigoEquipo) {
        return indice.eliminar(new Equipo(codigoEquipo, null, null, null, null, null));
    }

    public ABB<Equipo> getArbol() {
        return indice;
    }
}