package com.smartfit.gymApp.service;

import com.smartfit.gymApp.model.Clase;
import com.smartfit.gymApp.model.ReservaClase;
import com.smartfit.gymApp.repository.ReservaDAO;
import com.smartfit.gymApp.repository.ClaseDAO;
import com.smartfit.gymApp.validation.GestionValidator;
import java.util.List;
import com.smartfit.gymApp.structures.arboles.ABB;

/** La BD confirma cada operación; el ABB/las colas se reconstruyen desde los registros guardados. */
public class ReservaService {
    private static ReservaService instancia;
    private final ABB<GestorReservasClase> gestores=new ABB<>();
    private final ReservaDAO dao;
    private final ClaseService clases=new ClaseService();
    private ReservaService() { this(new ReservaDAO()); }
    ReservaService(ReservaDAO dao) { this.dao=java.util.Objects.requireNonNull(dao); gestores.setEtiquetador(g -> "C"+g.getClase().getIdClase()); }
    public static synchronized ReservaService getInstance() {
        if(instancia==null) instancia=new ReservaService();
        return instancia;
    }
    public List<ClaseDAO.Registro> listarClases(){return clases.listar();}
    public List<ReservaDAO.SocioOpcion> sociosActivos(){return dao.sociosActivos();}
    public synchronized ReservaDAO.Carga cargarClase(int idClase) {
        GestionValidator.id(idClase,"una clase");
        ReservaDAO.Carga carga=dao.cargarClase(idClase);
        gestores.eliminar(GestorReservasClase.clave(idClase));
        gestores.insertar(carga.gestor());
        return carga;
    }
    /** Compatibilidad para quien necesita registrar una clase en el índice, sin crearla en BD. */
    public synchronized GestorReservasClase registrarClase(Clase clase) {
        GestorReservasClase existente=gestorDe(clase.getIdClase());
        if(existente!=null) return existente;
        GestorReservasClase gestor=new GestorReservasClase(clase);
        gestores.insertar(gestor);
        return gestor;
    }
    public synchronized GestorReservasClase gestorDe(int idClase) {
        return gestores.buscar(GestorReservasClase.clave(idClase));
    }
   public ReservaClase reservar(Clase clase,String codigoSocio) {
        if(clase==null)throw new IllegalArgumentException("Selecciona una clase.");
        GestionValidator.id(clase.getIdClase(),"una clase");
        codigoSocio=normalizarCodigo(codigoSocio);
        // Validación e inserción bajo el mismo bloqueo de clase en el DAO.
        // Una consulta previa separada no protege contra dos solicitudes simultáneas.
        ReservaClase reserva=dao.reservar(clase.getIdClase(),codigoSocio);
        registrarAuditoria("RESERVAR_CLASE",codigoSocio+" → clase "+clase.getIdClase()+" ("+reserva.getEstado()+")");
        return reserva;
    }
    public ReservaClase cancelar(int idClase,String codigoSocio) {
        GestionValidator.id(idClase,"una clase");codigoSocio=normalizarCodigo(codigoSocio);
        ReservaDAO.Cancelacion resultado=dao.cancelar(idClase,codigoSocio);
        if(resultado.cancelada()!=null) registrarAuditoria("CANCELAR_RESERVA",codigoSocio+" de clase "+idClase
                +(resultado.promovida()==null?"":"; asciende "+resultado.promovida().getCodigoSocio()));
        return resultado.cancelada();
    }
    public boolean actualizarCodigoSocio(int idClase,String actual,String nuevo) {
        GestionValidator.id(idClase,"una clase");actual=normalizarCodigo(actual);nuevo=normalizarCodigo(nuevo);
        if(actual.equals(nuevo))return false;
        boolean cambio=dao.reasignar(idClase,actual,nuevo);
        if(cambio) registrarAuditoria("ACTUALIZAR_RESERVA",actual+" → "+nuevo+" en clase "+idClase);
        return cambio;
    }
    public int confirmarDisponibles(int idClase) {
        GestionValidator.id(idClase,"una clase");
        int cantidad=dao.confirmarDisponibles(idClase);
        if(cantidad>0) registrarAuditoria("CONFIRMAR_RESERVAS",cantidad+" reservas de clase "+idClase);
        return cantidad;
    }
    private void registrarAuditoria(String accion,String detalle) {
        // La reserva ya está confirmada: un fallo del historial no debe simular un fallo de la reserva.
        try { AuditoriaService.getInstance().registrar(accion,detalle); }
        catch(RuntimeException ex) { System.err.println("La reserva se guardó; el historial no estuvo disponible."); }
    }
    private static String normalizarCodigo(String codigo) {
        GestionValidator.codigoSocio(codigo);
        return codigo.trim().toUpperCase(java.util.Locale.ROOT);
    }
    public ABB<GestorReservasClase> getGestores() { return gestores; }
}
