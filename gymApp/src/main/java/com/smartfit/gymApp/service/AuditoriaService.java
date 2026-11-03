package com.smartfit.gymApp.service;

import com.smartfit.gymApp.model.Auditoria;
import com.smartfit.gymApp.repository.AuditoriaDAO;
import com.smartfit.gymApp.structures.pilas.Pila;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AuditoriaService {
    private static AuditoriaService instancia;
    private final Pila<Auditoria> historial=new Pila<>();
    private int contador;
    private int usuarioActual;
    private AuditoriaDAO dao;
    private List<AuditoriaDAO.Registro> lecturaCompleta=List.of();
    private AuditoriaService(){}
    AuditoriaService(AuditoriaDAO dao){this.dao=java.util.Objects.requireNonNull(dao);}
    public static synchronized AuditoriaService getInstance(){if(instancia==null)instancia=new AuditoriaService();return instancia;}
    public synchronized void activarPersistencia(){if(dao==null)dao=new AuditoriaDAO();}
    public void setUsuarioActual(int id){usuarioActual=id;}
    public int getUsuarioActual(){return usuarioActual;}
    public synchronized Auditoria registrar(String accion,String detalle){return registrar(usuarioActual,accion,detalle);}
    public synchronized Auditoria registrar(int usuario,String accion,String detalle){
        if(accion==null||accion.isBlank()||accion.length()>100)throw new IllegalArgumentException("La accion debe tener entre 1 y 100 caracteres.");
        if(detalle!=null&&detalle.length()>255)throw new IllegalArgumentException("El detalle admite hasta 255 caracteres.");
        Auditoria a=dao==null?new Auditoria(++contador,usuario,accion,LocalDateTime.now(),detalle):dao.registrar(usuario,accion,detalle);
        historial.push(a);
        if(dao!=null){List<AuditoriaDAO.Registro> copia=new ArrayList<>(lecturaCompleta);copia.add(new AuditoriaDAO.Registro(a,true));lecturaCompleta=List.copyOf(copia);}
        return a;
    }
    public synchronized void refrescar(){refrescarYListar();}
    /** Una lectura sirve tanto a la pila como a la tabla; no vuelve a consultar el mismo historial. */
    public synchronized List<AuditoriaDAO.Registro> refrescarYListar(){
        if(dao==null)return listar(true);
        reconstruir(dao.listar(true));return lecturaCompleta;
    }
    private void reconstruir(List<AuditoriaDAO.Registro> datos){
        historial.vaciar();
        for(AuditoriaDAO.Registro r:datos){if(r.activo())historial.push(r.auditoria());contador=Math.max(contador,r.auditoria().getIdAuditoria());}
        lecturaCompleta=List.copyOf(datos);
    }
    public synchronized List<AuditoriaDAO.Registro> instantanea(){return dao==null?listar(true):lecturaCompleta;}
    public synchronized List<AuditoriaDAO.Registro> listar(boolean incluirArchivados){
        if(dao!=null)return dao.listar(incluirArchivados);
        List<AuditoriaDAO.Registro> datos=new ArrayList<>();historial.recorrer(a->datos.add(new AuditoriaDAO.Registro(a,true)));return datos;
    }
    public synchronized Auditoria archivarUltima(){
        if(dao!=null)refrescarYListar();
        Auditoria a=historial.peek();if(a==null)return null;
        if(dao!=null){
            if(!dao.cambiarActivo(a.getIdAuditoria(),false)){refrescarYListar();return null;}
            List<AuditoriaDAO.Registro> copia=new ArrayList<>(lecturaCompleta.size());
            for(AuditoriaDAO.Registro r:lecturaCompleta)copia.add(r.auditoria().getIdAuditoria()==a.getIdAuditoria()?new AuditoriaDAO.Registro(r.auditoria(),false):r);
            reconstruir(copia);
        } else historial.pop();
        return a;
    }
    public synchronized boolean restaurar(int id){
        if(dao==null)return false;
        if(id<=0)throw new IllegalArgumentException("Selecciona un registro válido.");
        boolean ok=dao.cambiarActivo(id,true);refrescarYListar();return ok;
    }
    /** Sin una operacion inversa, este metodo solo archiva el registro. */
    public synchronized Auditoria revertirUltima(Consumer<Auditoria> deshacer){
        Auditoria a=historial.peek();if(a==null)return null;
        if(deshacer!=null)deshacer.accept(a);return archivarUltima();
    }
    public Auditoria ultima(){return historial.peek();}
    public boolean hayAcciones(){return !historial.estaVacia();}
    public int total(){return historial.tamanio();}
    public Pila<Auditoria> getHistorial(){return historial;}
}
