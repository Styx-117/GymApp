package com.smartfit.gymApp.service;

import com.smartfit.gymApp.model.Socio;
import com.smartfit.gymApp.structures.avl.ArbolAVL;


public class SocioIndiceService {

    private static SocioIndiceService instancia;

    private final ArbolAVL<Socio> indice = new ArbolAVL<>();

    private SocioIndiceService() {
        indice.setEtiquetador(Socio::getCodigoSocio);
    }

    public static synchronized SocioIndiceService getInstance() {
        if (instancia == null) {
            instancia = new SocioIndiceService();
        }
        return instancia;
    }

   
    public void cargar(Iterable<Socio> socios) {
        indice.vaciar();
        for (Socio s : socios) {
            indice.insertar(s);
        }
    }

    public boolean agregar(Socio socio) {
        return indice.insertar(socio);
    }

    
    public Socio buscarPorCodigo(String codigoSocio) {
        return indice.buscar(new Socio(codigoSocio, "", "", ""));
    }

    public boolean eliminar(String codigoSocio) {
        return indice.eliminar(new Socio(codigoSocio, "", "", ""));
    }

    public ArbolAVL<Socio> getArbol() {
        return indice;
    }
}