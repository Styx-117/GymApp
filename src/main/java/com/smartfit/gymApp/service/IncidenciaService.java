package com.smartfit.gymApp.service;

import com.smartfit.gymApp.model.Incidencia;
import com.smartfit.gymApp.repository.IncidenciaDAO;
import com.smartfit.gymApp.structures.colas.Cola;
import com.smartfit.gymApp.structures.colas.ColaPrioridad;
import com.smartfit.gymApp.structures.listas.ListaEnlazadaSimple;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.smartfit.gymApp.validation.GestionValidator;

public class IncidenciaService {
    private static IncidenciaService instancia;
    private static IncidenciaService instanciaPersistente;
    private final IncidenciaDAO dao;
    private final ColaPrioridad<Incidencia> pendientes=new ColaPrioridad<>();
    private final Cola<Incidencia> atendidas=new Cola<>();
    private final ListaEnlazadaSimple<Incidencia> canceladas=new ListaEnlazadaSimple<>();
    private int contador;
    private IncidenciaService(IncidenciaDAO dao){this.dao=dao;}
    /** Gestor aislado para pruebas de las estructuras, sin servicios externos. */
    public static synchronized IncidenciaService getInstance(){
        if(instancia==null)instancia=new IncidenciaService(null);
        return instancia;
    }
    /** Gestor que utilizan las pantallas conectadas a PostgreSQL. */
    public static synchronized IncidenciaService persistente(){
        if(instanciaPersistente==null)instanciaPersistente=new IncidenciaService(new IncidenciaDAO());
        return instanciaPersistente;
    }
    public void recargar(){
        if(dao==null)return;
        List<Incidencia> datos=dao.listar();
        pendientes.vaciar();atendidas.vaciar();canceladas.vaciar();
        for(Incidencia i:datos){
            contador=Math.max(contador,i.getIdIncidencia());
            if("PENDIENTE".equals(i.getEstado()))pendientes.encolar(i);
            else if("ATENDIDA".equals(i.getEstado()))atendidas.enqueue(i);
            else canceladas.agregarFinal(i);
        }
    }
    public Incidencia registrar(String equipo,String descripcion,int prioridad){
        validar(equipo,descripcion,prioridad);
        equipo=equipo.trim().toUpperCase(java.util.Locale.ROOT);descripcion=descripcion.trim();
        if(dao!=null){Incidencia i=dao.registrar(equipo,descripcion,prioridad,usuario());recargar();return i;}
        Incidencia i=new Incidencia(++contador,equipo,descripcion,prioridad,LocalDate.now(),null,"PENDIENTE");
        pendientes.encolar(i);AuditoriaService.getInstance().registrar("REGISTRAR_INCIDENCIA",equipo+" prioridad "+prioridad);return i;
    }
    public Incidencia atenderSiguiente(){
        if(dao!=null)recargar();
        Incidencia i=pendientes.frente();if(i==null)return null;
        if(dao!=null){
            if(!dao.cambiarEstado(i.getIdIncidencia(),"PENDIENTE","ATENDIDA",usuario())){recargar();return null;}
            i.setEstado("ATENDIDA");i.setFechaAtencion(LocalDate.now());recargar();return i;
        }
        pendientes.desencolar();i.setEstado("ATENDIDA");i.setFechaAtencion(LocalDate.now());atendidas.enqueue(i);
        AuditoriaService.getInstance().registrar("ATENDER_INCIDENCIA","#"+i.getIdIncidencia()+" equipo "+i.getCodigoEquipo());return i;
    }
    public boolean actualizarPendiente(int id,String equipo,String descripcion,int prioridad){
        GestionValidator.id(id,"una incidencia");validar(equipo,descripcion,prioridad);
        equipo=equipo.trim().toUpperCase(java.util.Locale.ROOT);descripcion=descripcion.trim();
        if(dao!=null){boolean ok=dao.actualizar(id,equipo,descripcion,prioridad,usuario());recargar();return ok;}
        Incidencia i=quitarPendiente(id);if(i==null)return false;
        i.setCodigoEquipo(equipo);i.setDescripcion(descripcion);i.setPrioridad(prioridad);pendientes.encolar(i);
        AuditoriaService.getInstance().registrar("ACTUALIZAR_INCIDENCIA","#"+id+" equipo "+equipo+" prioridad "+prioridad);return true;
    }
    /** Compatibilidad del contrato anterior. La pantalla cancela sin borrar la fila. */
    public boolean eliminarPendiente(int id){
        GestionValidator.id(id,"una incidencia");
        if(dao!=null){boolean ok=dao.cambiarEstado(id,"PENDIENTE","CANCELADA",usuario());recargar();return ok;}
        Incidencia i=quitarPendiente(id);if(i==null)return false;
        i.setEstado("CANCELADA");canceladas.agregarFinal(i);AuditoriaService.getInstance().registrar("CANCELAR_INCIDENCIA","#"+id);return true;
    }
    public boolean reactivar(int id){
        GestionValidator.id(id,"una incidencia");
        if(dao!=null){boolean ok=dao.cambiarEstado(id,"CANCELADA","PENDIENTE",usuario());recargar();return ok;}
        Incidencia i=canceladas.buscar(c->c.getIdIncidencia()==id);
        if(i==null)return false;
        canceladas.eliminar(c->c.getIdIncidencia()==id);i.setEstado("PENDIENTE");pendientes.encolar(i);return true;
    }
    private Incidencia quitarPendiente(int id){
        Cola<Incidencia> temporal=new Cola<>();Incidencia elegida=null;
        while(!pendientes.estaVacia()){
            Incidencia i=pendientes.desencolar();if(i.getIdIncidencia()==id)elegida=i;else temporal.enqueue(i);
        }
        while(!temporal.estaVacia())pendientes.encolar(temporal.dequeue());return elegida;
    }
    public List<Incidencia> listar(){
        List<Incidencia> resultado=new ArrayList<>();pendientes.recorrer(resultado::add);atendidas.recorrer(resultado::add);canceladas.recorrer(resultado::add);return resultado;
    }
    public Incidencia verSiguiente(){return pendientes.frente();}
    public boolean hayPendientes(){return !pendientes.estaVacia();}
    public ColaPrioridad<Incidencia> getPendientes(){return pendientes;}
    public Cola<Incidencia> getAtendidas(){return atendidas;}
    private int usuario(){return AuditoriaService.getInstance().getUsuarioActual();}
    private void validar(String equipo,String descripcion,int prioridad){
        GestionValidator.incidencia(equipo,descripcion,prioridad);
    }
}
