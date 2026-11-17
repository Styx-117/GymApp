package com.smartfit.gymApp.views;

import com.smartfit.gymApp.model.Clase;
import com.smartfit.gymApp.repository.ClaseDAO;
import com.smartfit.gymApp.service.ClaseService;
import com.smartfit.gymApp.structures.listas.ListaCircular;
import com.smartfit.gymApp.utils.UI;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ClaseView extends JPanel {
    private final ClaseService servicio=new ClaseService();
    private final ListaCircular<ClaseDAO.EntrenadorOpcion> turnos=new ListaCircular<>();
    private final JTextField buscar=UI.campo(24), nombre=UI.campo(20), inicio=UI.campo(8),fin=UI.campo(8),cupo=UI.campo(6);
    private final JComboBox<String> estado=new JComboBox<>(new String[]{"Activas","Inactivas","Todas"});
    private final JComboBox<String> dia=new JComboBox<>(new String[]{"LUNES","MARTES","MIERCOLES","JUEVES","VIERNES","SABADO","DOMINGO"});
    private final JComboBox<ClaseDAO.EntrenadorOpcion> entrenador=new JComboBox<>();
    private final DefaultTableModel modelo=new DefaultTableModel(
            new String[]{"ID","Clase","Entrenador","Día","Inicio","Fin","Cupo","Reservados","Estado"},0) {
        @Override public boolean isCellEditable(int r,int c) { return false; }
        @Override public Class<?> getColumnClass(int c) { return c==0 || c==6 || c==7?Integer.class:String.class; }
    };
    private final JTable tabla=new JTable(modelo);
    private final TableRowSorter<DefaultTableModel> orden=new TableRowSorter<>(modelo);
    private final JLabel resumen=UI.etiqueta("Carga las clases para empezar.");
    private final JButton nuevo=UI.boton("Registrar clase"),guardar=UI.boton("Guardar cambios"),
            cambiar=UI.boton("Desactivar / reactivar"),limpiar=UI.boton("Nuevo formulario"),recargar=UI.boton("Actualizar"),siguiente=UI.boton("Siguiente entrenador");
    private List<ClaseDAO.Registro> registros=new ArrayList<>();
    private boolean ocupado;
    private int versionCarga;
    private SwingWorker<Datos,Void> cargaWorker;

    public ClaseView() {
        setLayout(new BorderLayout(12,12)); setBackground(UI.FONDO);
        setBorder(BorderFactory.createEmptyBorder(18,20,18,20));
        
        // Aplicar el filtro numérico al campo de cupo máximo (máximo 3 dígitos)
        ((javax.swing.text.AbstractDocument) cupo.getDocument()).setDocumentFilter(new com.smartfit.gymApp.utils.NumericDocumentFilter(3));

        JPanel norte=new JPanel(new BorderLayout(0,12)); norte.setOpaque(false);
        norte.add(UI.titulo("Clases y horarios"),BorderLayout.NORTH);
        JPanel filtros=UI.fila(); filtros.add(UI.etiqueta("Buscar:")); filtros.add(buscar);
        buscar.setToolTipText("ID, nombre de clase, entrenador o día");
        filtros.add(UI.etiqueta("Estado:")); filtros.add(estado); filtros.add(recargar);
        norte.add(filtros,BorderLayout.CENTER); norte.add(resumen,BorderLayout.SOUTH); add(norte,BorderLayout.NORTH);
        tabla.setRowSorter(orden); tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(31); tabla.setBackground(UI.CAMPO); tabla.setForeground(Color.WHITE);
        tabla.setSelectionBackground(UI.ACENTO); tabla.setSelectionForeground(Color.BLACK);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setBackground(UI.PANEL); tabla.getTableHeader().setForeground(Color.WHITE);
        add(UI.scroll(tabla,"Clases registradas"),BorderLayout.CENTER);
        JPanel sur=new JPanel(new BorderLayout(0,12)); sur.setOpaque(false);
        JPanel campos=new JPanel(new GridLayout(2,3,14,12)); campos.setOpaque(false);
        campos.add(campo("Nombre",nombre)); campos.add(campo("Entrenador activo",entrenador)); campos.add(campo("Día",dia));
        campos.add(campo("Inicio (HH:mm)",inicio)); campos.add(campo("Fin (HH:mm)",fin)); campos.add(campo("Cupo máximo",cupo));
        sur.add(campos,BorderLayout.CENTER);
        JPanel botones=UI.fila(); botones.add(nuevo); botones.add(guardar); botones.add(cambiar); botones.add(limpiar); botones.add(siguiente);
        sur.add(botones,BorderLayout.SOUTH); add(sur,BorderLayout.SOUTH);
        for(JComboBox<?> combo:new JComboBox<?>[]{estado,dia,entrenador}) { combo.setBackground(UI.CAMPO); combo.setForeground(Color.WHITE); }
        recargar.addActionListener(e -> refrescar());
        estado.addActionListener(e -> filtrar());
        buscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });
        tabla.getSelectionModel().addListSelectionListener(e -> { if(!e.getValueIsAdjusting()) seleccionar(); });
        siguiente.addActionListener(e -> siguienteEntrenador());
        limpiar.addActionListener(e -> limpiar()); nuevo.addActionListener(e -> guardar(false)); guardar.addActionListener(e -> guardar(true));
        cambiar.addActionListener(e -> cambiarEstado());
        addHierarchyListener(e -> { if((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED)!=0) { if(isShowing()) refrescar(); else cancelarCarga(); } });
    }
    private JPanel campo(String etiqueta,JComponent componente) {
        JPanel panel=new JPanel(new BorderLayout(0,5)); panel.setOpaque(false);
        panel.add(UI.etiqueta(etiqueta),BorderLayout.NORTH); panel.add(componente,BorderLayout.CENTER); return panel;
    }
    public void refrescar() {
        if(ocupado) return;
        ocupado(true); resumen.setText("Cargando clases…");
        int version=++versionCarga;
        cargaWorker=new SwingWorker<Datos,Void>() {
            protected Datos doInBackground() { var clases=servicio.listar();return isCancelled()?new Datos(List.of(),List.of()):new Datos(clases,servicio.entrenadoresActivos()); }
            protected void done() {
                if(isCancelled()||version!=versionCarga)return;
                if(cargaWorker==this)cargaWorker=null;
                try {
                    Datos datos=get(); registros=datos.clases();
                    tabla.clearSelection(); modelo.setRowCount(0); entrenador.removeAllItems();
                    datos.entrenadores().forEach(entrenador::addItem);
                    turnos.vaciar(); datos.entrenadores().forEach(turnos::agregar);
                    for(ClaseDAO.Registro r:registros) {
                        Clase c=r.clase(); modelo.addRow(new Object[]{c.getIdClase(),c.getNombre(),r.entrenador(),c.getDiaSemana(),
                                texto(c.getHoraInicio()),texto(c.getHoraFin()),c.getCupoMaximo(),c.getCupoActual(),r.activo()?"Activa":"Inactiva"});
                    }
                    limpiar(); filtrar();
                } catch(Exception ex) { resumen.setText("No se pudieron cargar las clases. Pulsa Actualizar."); error(ex); }
                finally { ocupado(false); }
            }
        };
        cargaWorker.execute();
    }
    private void cancelarCarga() {
        SwingWorker<?,Void> anterior=cargaWorker;
        if(anterior!=null) {
            ++versionCarga;cargaWorker=null;
            if(!anterior.isDone())anterior.cancel(true);
            ocupado(false);
        }
    }
    private void filtrar() {
        String texto=buscar.getText().trim().toLowerCase(Locale.ROOT);
        String est=String.valueOf(estado.getSelectedItem());
        orden.setRowFilter(new RowFilter<>() {
            public boolean include(Entry<? extends DefaultTableModel,? extends Integer> fila) {
                boolean coincideEstado=est.equals("Todas") || fila.getStringValue(8).equals(est.equals("Activas")?"Activa":"Inactiva");
                String datos=fila.getStringValue(0)+" "+fila.getStringValue(1)+" "+fila.getStringValue(2)+" "+fila.getStringValue(3);
                return coincideEstado && datos.toLowerCase(Locale.ROOT).contains(texto);
            }
        });
        resumen.setText(tabla.getRowCount()+" de "+modelo.getRowCount()+" clases · Desactivar conserva las reservas y el historial.");
    }
    private ClaseDAO.Registro seleccionada() {
        int fila=tabla.getSelectedRow();
        if(fila<0) return null;
        int id=(Integer)modelo.getValueAt(tabla.convertRowIndexToModel(fila),0);
        return registros.stream().filter(r -> r.clase().getIdClase()==id).findFirst().orElse(null);
    }
    private void seleccionar() {
        ClaseDAO.Registro r=seleccionada(); if(r==null) return;
        Clase c=r.clase(); nombre.setText(c.getNombre()); inicio.setText(texto(c.getHoraInicio())); fin.setText(texto(c.getHoraFin()));
        cupo.setText(String.valueOf(c.getCupoMaximo())); dia.setSelectedItem(c.getDiaSemana());
        entrenador.setSelectedIndex(-1);
        for(int i=0;i<entrenador.getItemCount();i++) if(entrenador.getItemAt(i).id()==c.getIdEntrenador()) entrenador.setSelectedIndex(i);
        cambiar.setText(r.activo()?"Desactivar clase":"Reactivar clase");
    }
    private void guardar(boolean edicion) {
        ClaseDAO.Registro actual=seleccionada();
        if(edicion && actual==null) { UI.aviso(this,"Selecciona una clase para guardar los cambios."); return; }
        try {
            ClaseDAO.EntrenadorOpcion opcion=(ClaseDAO.EntrenadorOpcion)entrenador.getSelectedItem();
            if(opcion==null) throw new IllegalArgumentException("Selecciona un entrenador activo.");
            String n=nombre.getText().trim(),d=(String)dia.getSelectedItem();
            Time i=Time.valueOf(LocalTime.parse(inicio.getText().trim())),f=Time.valueOf(LocalTime.parse(fin.getText().trim()));
            int capacidad=Integer.parseInt(cupo.getText().trim());
            tarea(() -> edicion?servicio.actualizar(actual.clase().getIdClase(),n,opcion.id(),d,i,f,capacidad)
                    :servicio.registrar(n,opcion.id(),d,i,f,capacidad),
                    "No se guardó la clase: el entrenador debe estar activo y el cupo no puede ser menor al reservado.");
        } catch(java.time.format.DateTimeParseException|NumberFormatException ex) {
            UI.aviso(this,"Revisa las horas (HH:mm) y el cupo numérico.");
        } catch(IllegalArgumentException ex) { UI.aviso(this,ex.getMessage()); }
    }
    private void cambiarEstado() {
        ClaseDAO.Registro r=seleccionada();
        if(r==null) { UI.aviso(this,"Selecciona una clase."); return; }
        String accion=r.activo()?"desactivar":"reactivar";
        if(JOptionPane.showConfirmDialog(this,"¿Deseas "+accion+" "+r.clase().getNombre()+"?\nSe conservan sus reservas e historial.",
                "Cambiar estado",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION) return;
        tarea(() -> servicio.cambiarEstado(r.clase().getIdClase(),!r.activo()),"La clase ya cambió de estado. Actualiza los datos.");
    }
    private void tarea(java.util.concurrent.Callable<Boolean> accion,String mensaje) {
        ocupado(true);
        new SwingWorker<Boolean,Void>() {
            protected Boolean doInBackground() throws Exception { return accion.call(); }
            protected void done() {
                try { if(!get()) UI.aviso(ClaseView.this,mensaje); }
                catch(Exception ex) { error(ex); }
                finally { ocupado(false); if(isShowing())refrescar(); }
            }
        }.execute();
    }
    private void limpiar() {
        tabla.clearSelection(); nombre.setText(""); inicio.setText("07:00"); fin.setText("08:00"); cupo.setText("20");
        dia.setSelectedIndex(0); if(entrenador.getItemCount()>0) entrenador.setSelectedIndex(0);
        cambiar.setText("Desactivar / reactivar");
    }
    private void siguienteEntrenador() {
        ClaseDAO.EntrenadorOpcion turno=turnos.siguiente();
        if(turno==null) { UI.aviso(this,"No hay entrenadores activos."); return; }
        entrenador.setSelectedItem(turno);
    }
    private void ocupado(boolean valor) {
        ocupado=valor;
        for(JButton boton:new JButton[]{nuevo,guardar,cambiar,limpiar,recargar,siguiente}) boton.setEnabled(!valor);
    }
    private void error(Exception ex) {
        Throwable causa=ex instanceof ExecutionException?ex.getCause():ex;
        UI.aviso(this,causa==null?"No se pudo completar la operación.":causa.getMessage());
    }
    private static String texto(LocalTime hora) { return hora==null?"":hora.toString(); }
    private record Datos(List<ClaseDAO.Registro> clases,List<ClaseDAO.EntrenadorOpcion> entrenadores) {}
}