package com.smartfit.gymApp.views;

import com.smartfit.gymApp.model.Equipo;
import com.smartfit.gymApp.model.Incidencia;
import com.smartfit.gymApp.service.EquipoService;
import com.smartfit.gymApp.service.IncidenciaService;
import com.smartfit.gymApp.utils.UI;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

public class IncidenciaView extends JPanel {
    private final IncidenciaService servicio=IncidenciaService.persistente();
    private final EquipoService equiposServicio=new EquipoService();
    private final JComboBox<Equipo> equipo=new JComboBox<>();
    private final JTextField descripcion=UI.campo(20),buscar=UI.campo(18);
    private final JComboBox<String> prioridad=new JComboBox<>(new String[]{"1 - Alta","2 - Media","3 - Baja"});
    private final JComboBox<String> estado=new JComboBox<>(new String[]{"Todos","PENDIENTE","ATENDIDA","CANCELADA"});
    private final JComboBox<String> filtroPrioridad=new JComboBox<>(new String[]{"Todas","1 - Alta","2 - Media","3 - Baja"});
    private final DefaultTableModel modelo=new DefaultTableModel(new Object[]{"ID","Equipo","Descripcion","Prioridad","Estado","Reporte","Atencion"},0){public boolean isCellEditable(int r,int c){return false;}};
    private final JTable tabla=new JTable(modelo);
    private final TablePager paginacion=new TablePager(modelo,50);
    private final JLabel resumen=UI.etiqueta("Cargando incidencias...");
    private final JButton registrar=UI.boton("Registrar"),actualizar=UI.boton("Guardar cambios"),atender=UI.boton("Atender siguiente"),cancelar=UI.boton("Cancelar incidencia"),reactivar=UI.boton("Reactivar"),recargar=UI.boton("Actualizar lista"),verCola=UI.boton("Ver cola de prioridad");
    private List<Equipo> equipos=List.of();
    private boolean ocupado;

    public IncidenciaView(){
        setLayout(new BorderLayout(10,10));setBackground(UI.FONDO);setBorder(BorderFactory.createEmptyBorder(8,12,12,12));
        JPanel norte=new JPanel(new BorderLayout());norte.setOpaque(false);norte.add(UI.titulo("Incidencias de mantenimiento"),BorderLayout.NORTH);
        JPanel filtros=new JPanel(new GridLayout(1,4,8,0));filtros.setOpaque(false);filtros.add(campo("Buscar por equipo o descripcion",buscar));filtros.add(campo("Estado",estado));filtros.add(campo("Prioridad",filtroPrioridad));filtros.add(campo("Datos guardados",recargar));norte.add(filtros,BorderLayout.CENTER);add(norte,BorderLayout.NORTH);
        tabla.setRowHeight(28);tabla.setBackground(UI.CAMPO);tabla.setForeground(Color.WHITE);tabla.setSelectionBackground(UI.ACENTO);tabla.setSelectionForeground(Color.BLACK);tabla.setAutoCreateRowSorter(true);tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);int[] anchos={55,105,300,80,115,110,110};for(int n=0;n<anchos.length;n++)tabla.getColumnModel().getColumn(n).setPreferredWidth(anchos[n]);
        JPanel resultados=new JPanel(new BorderLayout(0,6));resultados.setOpaque(false);
        resultados.add(UI.scroll(tabla,"Pendientes por prioridad, historial y canceladas"),BorderLayout.CENTER);
        resultados.add(paginacion,BorderLayout.SOUTH);add(resultados,BorderLayout.CENTER);
        JPanel sur=new JPanel(new BorderLayout(8,8));sur.setOpaque(false);
        JPanel campos=new JPanel(new GridLayout(1,3,12,0));campos.setOpaque(false);
        campos.add(campo("Equipo registrado",equipo));campos.add(campo("Descripcion (hasta 200 caracteres)",descripcion));campos.add(campo("Prioridad",prioridad));sur.add(campos,BorderLayout.NORTH);
        JPanel acciones=new JPanel(new GridLayout(2,4,8,8));acciones.setOpaque(false);for(JButton b:new JButton[]{registrar,actualizar,atender,cancelar,reactivar})acciones.add(b);
        JButton limpiar=UI.boton("Limpiar formulario");acciones.add(limpiar);acciones.add(verCola);sur.add(acciones,BorderLayout.CENTER);sur.add(resumen,BorderLayout.SOUTH);add(sur,BorderLayout.SOUTH);
        equipo.setRenderer(new DefaultListCellRenderer(){public Component getListCellRendererComponent(JList<?> l,Object v,int i,boolean s,boolean f){super.getListCellRendererComponent(l,v,i,s,f);if(v instanceof Equipo e)setText(e.getCodigoEquipo()+" - "+e.getNombre()+" ("+e.getEstado()+")");return this;}});
        recargar.addActionListener(e->refrescarDatos());estado.addActionListener(e->mostrar());filtroPrioridad.addActionListener(e->mostrar());
        verCola.addActionListener(e->UI.info(this,servicio.getPendientes().mostrar()));
        buscar.getDocument().addDocumentListener(new DocumentListener(){public void insertUpdate(DocumentEvent e){mostrar();}public void removeUpdate(DocumentEvent e){mostrar();}public void changedUpdate(DocumentEvent e){mostrar();}});
        tabla.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting())seleccionar();});
        registrar.addActionListener(e->{Equipo eq=(Equipo)equipo.getSelectedItem();if(eq==null){UI.aviso(this,"No hay equipos registrados.");return;}String texto=descripcion.getText().trim();int p=prioridad.getSelectedIndex()+1;ejecutar(()->servicio.registrar(eq.getCodigoEquipo(),texto,p));});
        actualizar.addActionListener(e->{Integer id=idSeleccionado();Equipo eq=(Equipo)equipo.getSelectedItem();if(id==null||eq==null){UI.aviso(this,"Selecciona una incidencia pendiente y su equipo.");return;}String texto=descripcion.getText().trim();int p=prioridad.getSelectedIndex()+1;ejecutar(()->{if(!servicio.actualizarPendiente(id,eq.getCodigoEquipo(),texto,p))throw new IllegalStateException("La incidencia ya no esta pendiente. Actualiza la lista.");});});
        atender.addActionListener(e->ejecutar(()->{if(servicio.atenderSiguiente()==null)throw new IllegalStateException("No hay incidencias pendientes por atender.");}));
        cancelar.addActionListener(e->{Integer id=idSeleccionado();if(id==null){UI.aviso(this,"Selecciona una incidencia pendiente.");return;}if(JOptionPane.showConfirmDialog(this,"¿Cancelar esta incidencia? El registro se conservara.","Confirmar",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)ejecutar(()->{if(!servicio.eliminarPendiente(id))throw new IllegalStateException("Solo puedes cancelar incidencias pendientes.");});});
        reactivar.addActionListener(e->{Integer id=idSeleccionado();if(id==null){UI.aviso(this,"Selecciona una incidencia cancelada.");return;}ejecutar(()->{if(!servicio.reactivar(id))throw new IllegalStateException("Solo puedes reactivar incidencias canceladas.");});});
        limpiar.addActionListener(e->{tabla.clearSelection();descripcion.setText("");prioridad.setSelectedIndex(0);});
        addHierarchyListener(e->{if((e.getChangeFlags()&java.awt.event.HierarchyEvent.SHOWING_CHANGED)!=0&&isShowing())refrescarDatos();});
    }
    private JPanel campo(String texto,JComponent control){JPanel p=new JPanel(new BorderLayout(0,4));p.setOpaque(false);p.add(UI.etiqueta(texto),BorderLayout.NORTH);p.add(control,BorderLayout.CENTER);return p;}
    public void refrescarDatos(){ejecutar(()->{servicio.recargar();equipos=equiposServicio.listar();});}
    private Integer idSeleccionado(){int f=tabla.getSelectedRow();return f<0?null:((Number)modelo.getValueAt(tabla.convertRowIndexToModel(f),0)).intValue();}
    private void seleccionar(){int f=tabla.getSelectedRow();if(f<0)return;f=tabla.convertRowIndexToModel(f);String codigo=String.valueOf(modelo.getValueAt(f,1));for(int n=0;n<equipo.getItemCount();n++)if(equipo.getItemAt(n).getCodigoEquipo().equals(codigo)){equipo.setSelectedIndex(n);break;}descripcion.setText(String.valueOf(modelo.getValueAt(f,2)));prioridad.setSelectedIndex(((Number)modelo.getValueAt(f,3)).intValue()-1);}
    private void mostrar(){
        String texto=buscar.getText().trim().toLowerCase(Locale.ROOT),filtro=(String)estado.getSelectedItem();int fp=filtroPrioridad.getSelectedIndex();List<Object[]> filas=new ArrayList<>();
        for(Incidencia i:servicio.listar()){
            String hay=(i.getIdIncidencia()+" "+i.getCodigoEquipo()+" "+i.getDescripcion()).toLowerCase(Locale.ROOT);
            if(!hay.contains(texto)||(!"Todos".equals(filtro)&&!filtro.equals(i.getEstado()))||(fp>0&&fp!=i.getPrioridad()))continue;
            filas.add(new Object[]{i.getIdIncidencia(),i.getCodigoEquipo(),i.getDescripcion(),i.getPrioridad(),i.getEstado(),i.getFechaReporte(),i.getFechaAtencion()});
        }
        paginacion.setRows(filas);
        Incidencia siguiente=servicio.verSiguiente();resumen.setText(filas.size()+" resultados. "+(siguiente==null?"Sin pendientes.":"Siguiente por prioridad: #"+siguiente.getIdIncidencia()+" - "+siguiente.getCodigoEquipo()+" (P"+siguiente.getPrioridad()+")"));
    }
    private void ejecutar(Runnable tarea){
        if(ocupado)return;ocupado=true;botones(false);resumen.setText("Consultando la base de datos...");
        new SwingWorker<Void,Void>(){protected Void doInBackground(){tarea.run();return null;}protected void done(){
            try{get();equipo.removeAllItems();for(Equipo e:equipos)equipo.addItem(e);mostrar();}
            catch(Exception e){Throwable causa=e.getCause()==null?e:e.getCause();resumen.setText("No se pudo completar la operacion.");UI.aviso(IncidenciaView.this,causa.getMessage());}
            finally{ocupado=false;botones(true);}
        }}.execute();
    }
    private void botones(boolean valor){paginacion.setBusy(!valor);for(JButton b:new JButton[]{registrar,actualizar,atender,cancelar,reactivar,recargar,verCola})b.setEnabled(valor);buscar.setEnabled(valor);estado.setEnabled(valor);filtroPrioridad.setEnabled(valor);equipo.setEnabled(valor);descripcion.setEnabled(valor);prioridad.setEnabled(valor);tabla.setEnabled(valor);}
}
