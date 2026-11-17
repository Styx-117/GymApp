package com.smartfit.gymApp.views;

import com.smartfit.gymApp.model.ReservaClase;
import com.smartfit.gymApp.repository.ClaseDAO;
import com.smartfit.gymApp.repository.ReservaDAO;
import com.smartfit.gymApp.service.ReservaService;
import com.smartfit.gymApp.utils.UI;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ReservaView extends JPanel {
    private final ReservaService servicio=ReservaService.getInstance();
    private final JComboBox<ClaseDAO.Registro> clases=new JComboBox<>();
    private final JComboBox<ReservaDAO.SocioOpcion> socios=new JComboBox<>();
    private final JComboBox<String> estadoClase=new JComboBox<>(new String[]{"Clases activas","Clases inactivas","Todas las clases"});
    private final JComboBox<String> estadoReserva=new JComboBox<>(new String[]{"Todas","Confirmadas","Lista de espera","Canceladas"});
    private final JTextField buscar=UI.campo(22);
    private final JLabel resumen=UI.etiqueta("Selecciona una clase para ver las reservas guardadas.");
    private final DefaultTableModel modelo=new DefaultTableModel(new String[]{
            "Reserva","Código socio","Socio","Estado del socio","Reserva / estado","Espera","Fecha"},0) {
        @Override public boolean isCellEditable(int r,int c) { return false; }
        @Override public Class<?> getColumnClass(int c) { return c==0 || c==5?Integer.class:String.class; }
    };
    private final JTable tabla=new JTable(modelo);
    private final TableRowSorter<DefaultTableModel> orden=new TableRowSorter<>(modelo);
    private final JButton reservar=UI.boton("Reservar cupo"),cancelar=UI.boton("Cancelar seleccionada"),
            reasignar=UI.boton("Reasignar al socio elegido"),confirmar=UI.boton("Confirmar espera por orden"),
            recargar=UI.boton("Actualizar"),verColas=UI.boton("Ver colas");
    private List<ClaseDAO.Registro> registros=new ArrayList<>();
    private ReservaDAO.Carga carga;
    private boolean ocupado;
    private int versionCarga;
    private SwingWorker<?,Void> cargaWorker;

    public ReservaView() {
        setLayout(new BorderLayout(12,12)); setBackground(UI.FONDO);
        setBorder(BorderFactory.createEmptyBorder(18,20,18,20));
        JPanel norte=new JPanel(new BorderLayout(0,10)); norte.setOpaque(false);
        norte.add(UI.titulo("Reservas de clases"),BorderLayout.NORTH);
        JPanel selectores=new JPanel(new GridLayout(2,1,0,8)); selectores.setOpaque(false);
        JPanel filaClase=new JPanel(new BorderLayout(10,0)); filaClase.setOpaque(false);
        filaClase.add(estadoClase,BorderLayout.WEST); filaClase.add(clases,BorderLayout.CENTER); filaClase.add(recargar,BorderLayout.EAST);
        JPanel filaSocio=new JPanel(new BorderLayout(10,0)); filaSocio.setOpaque(false);
        filaSocio.add(UI.etiqueta("Socio activo:"),BorderLayout.WEST); filaSocio.add(socios,BorderLayout.CENTER);
        filaSocio.add(reservar,BorderLayout.EAST); selectores.add(filaClase); selectores.add(filaSocio);
        norte.add(selectores,BorderLayout.CENTER); norte.add(resumen,BorderLayout.SOUTH); add(norte,BorderLayout.NORTH);
        JPanel centro=new JPanel(new BorderLayout(0,8)); centro.setOpaque(false);
        JPanel filtros=UI.fila(); filtros.add(UI.etiqueta("Buscar socio o reserva:")); filtros.add(buscar);
        filtros.add(estadoReserva); centro.add(filtros,BorderLayout.NORTH);
        tabla.setRowSorter(orden); tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(31); tabla.setBackground(UI.CAMPO); tabla.setForeground(Color.WHITE);
        tabla.setSelectionBackground(UI.ACENTO); tabla.setSelectionForeground(Color.BLACK);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setBackground(UI.PANEL); tabla.getTableHeader().setForeground(Color.WHITE);
        centro.add(UI.scroll(tabla,"Reservas e historial"),BorderLayout.CENTER); add(centro,BorderLayout.CENTER);
        JPanel sur=new JPanel(new BorderLayout(0,6)); sur.setOpaque(false);
        JPanel acciones=UI.fila(); acciones.add(cancelar); acciones.add(reasignar); acciones.add(confirmar); acciones.add(verColas);
        sur.add(acciones,BorderLayout.NORTH);
        sur.add(UI.etiqueta("Cancelar conserva el historial. Una nueva reserva entra al final de la espera si no hay cupo."),BorderLayout.SOUTH);
        add(sur,BorderLayout.SOUTH);
        for(JComboBox<?> combo:new JComboBox<?>[]{clases,socios,estadoClase,estadoReserva}) {
            combo.setBackground(UI.CAMPO); combo.setForeground(Color.WHITE);
        }
        clases.addActionListener(e -> { if(!ocupado) cargarSeleccion(); });
        estadoClase.addActionListener(e -> { if(!ocupado) { poblarClases(null); cargarSeleccion(); } });
        socios.addActionListener(e -> botones());
        tabla.getSelectionModel().addListSelectionListener(e -> botones());
        estadoReserva.addActionListener(e -> filtrar());
        buscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });
        recargar.addActionListener(e -> refrescar()); reservar.addActionListener(e -> reservar());
        cancelar.addActionListener(e -> cancelar()); reasignar.addActionListener(e -> reasignar());
        confirmar.addActionListener(e -> confirmar());
        verColas.addActionListener(e -> verColas());
        addHierarchyListener(e -> { if((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED)!=0) { if(isShowing()) refrescar(); else cancelarCarga(); } });
        botones();
    }

    public void refrescar() {
        if(ocupado) return;
        ClaseDAO.Registro seleccion=(ClaseDAO.Registro)clases.getSelectedItem();
        Integer id=seleccion==null?null:seleccion.clase().getIdClase();
        ocupado(true); resumen.setText("Cargando clases y socios…");
        int version=++versionCarga;
        SwingWorker<Datos,Void> worker=new SwingWorker<Datos,Void>() {
            protected Datos doInBackground() { var catalogo=servicio.listarClases();return isCancelled()?new Datos(List.of(),List.of()):new Datos(catalogo,servicio.sociosActivos()); }
            protected void done() {
                if(isCancelled()||version!=versionCarga)return;
                if(cargaWorker==this)cargaWorker=null;
                try {
                    Datos datos=get(); registros=datos.clases(); socios.removeAllItems();
                    datos.socios().forEach(socios::addItem); poblarClases(id);
                    ocupado(false); cargarSeleccion();
                } catch(Exception ex) {
                    carga=null; modelo.setRowCount(0); resumen.setText("No se pudieron actualizar los datos.");
                    ocupado(false); error(ex);
                }
            }
        };
        cargaWorker=worker;worker.execute();
    }
    private void cancelarCarga() {
        SwingWorker<?,Void> anterior=cargaWorker;
        if(anterior!=null) {
            ++versionCarga;cargaWorker=null;
            if(!anterior.isDone())anterior.cancel(true);
            ocupado(false);
        }
    }

    private void poblarClases(Integer id) {
        boolean anterior=ocupado; ocupado=true; clases.removeAllItems();
        int opcion=estadoClase.getSelectedIndex();
        for(ClaseDAO.Registro r:registros) {
            if(opcion==2 || (opcion==0)==r.activo()) clases.addItem(r);
        }
        if(id!=null) for(int i=0;i<clases.getItemCount();i++)
            if(clases.getItemAt(i).clase().getIdClase()==id) clases.setSelectedIndex(i);
        ocupado=anterior;
    }
    private void cargarSeleccion() {
        ClaseDAO.Registro r=(ClaseDAO.Registro)clases.getSelectedItem();
        if(r==null) {
            carga=null; modelo.setRowCount(0); resumen.setText("No hay clases con este estado."); botones(); return;
        }
        ocupado(true); resumen.setText("Cargando reservas de "+r.clase().getNombre()+"…");
        int version=++versionCarga;
        if(cargaWorker!=null&&!cargaWorker.isDone())cargaWorker.cancel(true);
        SwingWorker<ReservaDAO.Carga,Void> worker=new SwingWorker<ReservaDAO.Carga,Void>() {
            protected ReservaDAO.Carga doInBackground() { return servicio.cargarClase(r.clase().getIdClase()); }
            protected void done() {
                if(isCancelled()||version!=versionCarga)return;
                if(cargaWorker==this)cargaWorker=null;
                try { carga=get(); mostrar(); }
                catch(Exception ex) { carga=null; modelo.setRowCount(0); resumen.setText("No se pudieron cargar las reservas."); error(ex); }
                finally { ocupado(false); }
            }
        };
        cargaWorker=worker;worker.execute();
    }
    private void mostrar() {
        tabla.clearSelection(); modelo.setRowCount(0);
        for(ReservaDAO.Fila fila:carga.filas()) {
            ReservaClase r=fila.reserva(); modelo.addRow(new Object[]{r.getIdReserva(),r.getCodigoSocio(),fila.socio(),
                    fila.socioActivo()?"Activo":"Inactivo",estadoTexto(r.getEstado()),
                    "LISTA_ESPERA".equals(r.getEstado())?r.getPosicionListaEspera():null,
                    r.getFechaReserva()==null?"Sin fecha":r.getFechaReserva().toString()});
        }
        resumen.setText((carga.activa()?"Clase activa":"Clase inactiva")+" · Cupos: "+carga.clase().getCupoActual()+" / "
                +carga.clase().getCupoMaximo()+" · En espera: "+carga.gestor().getListaEspera().tamanio()
                +" · Historial: "+carga.filas().size()); filtrar();
    }
    private void filtrar() {
        String texto=buscar.getText().trim().toLowerCase(Locale.ROOT);
        int estado=estadoReserva.getSelectedIndex();
        String esperado=switch(estado) { case 1 -> "Confirmada"; case 2 -> "En espera"; case 3 -> "Cancelada"; default -> ""; };
        orden.setRowFilter(new RowFilter<>() {
            public boolean include(Entry<? extends DefaultTableModel,? extends Integer> fila) {
                String datos=fila.getStringValue(0)+" "+fila.getStringValue(1)+" "+fila.getStringValue(2);
                return (esperado.isEmpty() || esperado.equals(fila.getStringValue(4))) && datos.toLowerCase(Locale.ROOT).contains(texto);
            }
        });
    }
    private ReservaClase seleccionada() {
        int fila=tabla.getSelectedRow(); if(fila<0 || carga==null) return null;
        int id=(Integer)modelo.getValueAt(tabla.convertRowIndexToModel(fila),0);
        return carga.filas().stream().map(ReservaDAO.Fila::reserva).filter(r -> r.getIdReserva()==id).findFirst().orElse(null);
    }
   private void reservar() {
        if(ocupado)return;
        ReservaDAO.SocioOpcion socio=(ReservaDAO.SocioOpcion)socios.getSelectedItem();
        if(carga==null || !carga.activa() || socio==null) return;

        // Validar si el socio ya tiene una reserva vigente (Confirmada o En espera) en esta clase
        boolean yaInscrito = tieneReserva(socio.codigo());

        if (yaInscrito) {
            UI.aviso(this, "El socio seleccionado ya cuenta con una reserva activa o en espera para esta clase.");
            return;
        }

        var clase=carga.clase();
        tarea(() -> {
            ReservaClase r=servicio.reservar(clase,socio.codigo());
            return "CONFIRMADA".equals(r.getEstado())?"Reserva confirmada.":"Reserva en espera, posición "+r.getPosicionListaEspera()+".";
        });
    }
    private void cancelar() {
        if(ocupado)return;
        ReservaClase reserva=seleccionada(); if(reserva==null) return;
        if(JOptionPane.showConfirmDialog(this,"¿Cancelar la reserva #"+reserva.getIdReserva()+" de "+reserva.getCodigoSocio()+"?\n"
                +"Se conserva el registro y, si la clase está activa, se libera el cupo para la espera.","Cancelar reserva",
                JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION) return;
        tarea(() -> servicio.cancelar(reserva.getIdClase(),reserva.getCodigoSocio())==null
                ?"La reserva ya no está vigente. Se actualizará el historial.":"Reserva cancelada; cupos y espera actualizados.");
    }
    private void reasignar() {
        if(ocupado)return;
        ReservaClase reserva=seleccionada(); ReservaDAO.SocioOpcion socio=(ReservaDAO.SocioOpcion)socios.getSelectedItem();
        if(reserva==null || socio==null) return;
        if(socio.codigo().equals(reserva.getCodigoSocio()) || tieneReserva(socio.codigo())) {
            UI.aviso(this,"El socio elegido ya tiene una reserva vigente en esta clase.");return;
        }
        if(JOptionPane.showConfirmDialog(this,"¿Reasignar la reserva #"+reserva.getIdReserva()+" a "+socio+"?\n"
                +"Conservará su estado y posición.","Reasignar reserva",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION) return;
        tarea(() -> servicio.actualizarCodigoSocio(reserva.getIdClase(),reserva.getCodigoSocio(),socio.codigo())
                ?"Reserva reasignada.":"No se reasignó: reserva inexistente o socio ya inscrito.");
    }
    private void confirmar() {
        if(ocupado || carga==null) return;
        int id=carga.clase().getIdClase();
        tarea(() -> {
            int cantidad=servicio.confirmarDisponibles(id);
            return cantidad==0?"No hay cupos libres o socios activos pendientes.":cantidad+" reserva(s) confirmada(s) por orden de espera.";
        });
    }
    private void tarea(java.util.concurrent.Callable<String> accion) {
        if(ocupado)return;
        ocupado(true);
        new SwingWorker<String,Void>() {
            protected String doInBackground() throws Exception { return accion.call(); }
            protected void done() {
                try { UI.info(ReservaView.this,get()); }
                catch(Exception ex) { error(ex); }
                finally { ocupado(false); if(isShowing())cargarSeleccion(); }
            }
        }.execute();
    }
    private void ocupado(boolean valor) {
        ocupado=valor; clases.setEnabled(!valor); estadoClase.setEnabled(!valor); socios.setEnabled(!valor);
        recargar.setEnabled(!valor); botones();
    }
    private void botones() {
        ReservaClase seleccion=seleccionada(); boolean vigente=seleccion!=null && !"CANCELADA".equals(seleccion.getEstado());
        boolean activa=!ocupado && carga!=null && carga.activa();
        ReservaDAO.SocioOpcion socio=(ReservaDAO.SocioOpcion)socios.getSelectedItem();
        boolean disponible=socio!=null && !tieneReserva(socio.codigo());
        reservar.setEnabled(activa && disponible);
        reservar.setToolTipText(socio!=null && !disponible ? "Este socio ya tiene una reserva confirmada o en espera." : null);
        cancelar.setEnabled(!ocupado && vigente);
        reasignar.setEnabled(activa && vigente && disponible);
        confirmar.setEnabled(activa && !carga.gestor().getListaEspera().estaVacia()
                && carga.clase().tieneCupoDisponible());
    }
    private void verColas() {
        if(carga==null) { UI.aviso(this,"Selecciona una clase para ver sus colas."); return; }
        UI.info(this,"CONFIRMADAS\n"+carga.gestor().getConfirmadas().mostrar()+"\nLISTA DE ESPERA\n"+carga.gestor().getListaEspera().mostrar());
    }
    private boolean tieneReserva(String codigo) {
        return carga!=null && carga.filas().stream().map(ReservaDAO.Fila::reserva)
                .anyMatch(r -> codigo.equals(r.getCodigoSocio()) && !"CANCELADA".equals(r.getEstado()));
    }
    private void error(Exception ex) {
        Throwable causa=ex instanceof ExecutionException?ex.getCause():ex;
        UI.aviso(this,causa==null?"No se pudo completar la operación.":causa.getMessage());
    }
    private static String estadoTexto(String estado) {
        return switch(estado) { case "CONFIRMADA" -> "Confirmada"; case "LISTA_ESPERA" -> "En espera"; case "CANCELADA" -> "Cancelada"; default -> estado; };
    }
    private record Datos(List<ClaseDAO.Registro> clases,List<ReservaDAO.SocioOpcion> socios) {}
}
