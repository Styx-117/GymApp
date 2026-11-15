package com.smartfit.gymApp.service;

import com.smartfit.gymApp.repository.EntrenadorDAO;
import com.smartfit.gymApp.repository.PageResult;
import com.smartfit.gymApp.model.Entrenador;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class EntrenadorService {
    private final EntrenadorDAO dao;
    public EntrenadorService(){this(new EntrenadorDAO());}
    @Autowired public EntrenadorService(EntrenadorDAO dao){this.dao=dao;}
    public void validarDatos(String n,String a,String e,String t){ValidacionPersona.entrenador(n,a,e,t);}
    public PageResult<Entrenador> buscarPagina(String texto,Boolean activo,int pagina,int tamanio){return dao.buscarPagina(texto,activo,pagina,tamanio);}
    public boolean registrar(String n,String a,String e,String t){validarDatos(n,a,e,t);return dao.registrarEntrenador(n.trim(),a.trim(),e.trim(),ValidacionPersona.telefono(t));}
    public boolean actualizar(int id,String n,String a,String e,String t){if(id<=0)throw new IllegalArgumentException("Selecciona un entrenador.");validarDatos(n,a,e,t);return dao.actualizarEntrenador(id,n.trim(),a.trim(),e.trim(),ValidacionPersona.telefono(t));}
    public boolean cambiarEstado(int id,boolean activo){if(id<=0)throw new IllegalArgumentException("Selecciona un entrenador.");return dao.cambiarEstado(id,activo);}
}
