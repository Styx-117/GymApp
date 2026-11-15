package com.smartfit.gymApp.service;

import com.smartfit.gymApp.model.Equipo;
import com.smartfit.gymApp.repository.EquipoDAO;
import com.smartfit.gymApp.validation.GestionValidator;
import java.util.List;
import java.util.Locale;

public class EquipoService {
    private final EquipoDAO dao;
    public EquipoService(){this(new EquipoDAO());}
    public EquipoService(EquipoDAO dao){this.dao=java.util.Objects.requireNonNull(dao);}
    public List<Equipo> listar(){return dao.listar();}
    public boolean guardar(Equipo equipo,boolean nuevo){
        GestionValidator.equipo(equipo);
        Equipo normal=new Equipo(equipo.getCodigoEquipo().trim().toUpperCase(Locale.ROOT),equipo.getNombre().trim(),
            equipo.getTipo()==null?null:equipo.getTipo().trim(),equipo.getUbicacion()==null?null:equipo.getUbicacion().trim(),
            equipo.getFechaAdquisicion(),equipo.getEstado());
        return dao.guardar(normal,nuevo,AuditoriaService.getInstance().getUsuarioActual());
    }
    public boolean cambiarEstado(String codigo,String estado){
        GestionValidator.requerido(codigo,20,"El código");GestionValidator.estadoEquipo(estado);
        return dao.cambiarEstado(codigo.trim().toUpperCase(Locale.ROOT),estado,AuditoriaService.getInstance().getUsuarioActual());
    }
}
