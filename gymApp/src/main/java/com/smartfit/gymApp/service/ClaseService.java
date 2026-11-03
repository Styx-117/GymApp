package com.smartfit.gymApp.service;

import com.smartfit.gymApp.repository.ClaseDAO;
import com.smartfit.gymApp.validation.GestionValidator;
import java.sql.Time;
import java.util.List;

/** Una única entrada de negocio para pantallas y futuros controladores. */
public class ClaseService {
    private final ClaseDAO dao;
    public ClaseService(){this(new ClaseDAO());}
    public ClaseService(ClaseDAO dao){this.dao=java.util.Objects.requireNonNull(dao);}
    public List<ClaseDAO.Registro> listar(){return dao.listarRegistros();}
    public List<ClaseDAO.EntrenadorOpcion> entrenadoresActivos(){return dao.entrenadoresActivos();}
    public boolean registrar(String nombre,int entrenador,String dia,Time inicio,Time fin,int cupo){
        GestionValidator.clase(nombre,entrenador,dia,inicio,fin,cupo);
        return dao.registrarClase(nombre.trim(),entrenador,GestionValidator.dia(dia),inicio,fin,cupo);
    }
    public boolean actualizar(int id,String nombre,int entrenador,String dia,Time inicio,Time fin,int cupo){
        GestionValidator.id(id,"una clase");GestionValidator.clase(nombre,entrenador,dia,inicio,fin,cupo);
        return dao.actualizarClase(id,nombre.trim(),entrenador,GestionValidator.dia(dia),inicio,fin,cupo);
    }
    public boolean cambiarEstado(int id,boolean activo){GestionValidator.id(id,"una clase");return dao.cambiarEstado(id,activo);}
}
