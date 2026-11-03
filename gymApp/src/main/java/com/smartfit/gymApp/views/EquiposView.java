package com.smartfit.gymApp.views;

import com.smartfit.gymApp.model.Equipo;
import com.smartfit.gymApp.service.EquipoService;
import com.smartfit.gymApp.service.AuditoriaService;
import com.smartfit.gymApp.utils.UI;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class EquiposView extends JPanel {
    private final EquipoService servicio=new EquipoService();
    private final JTextField buscar=UI.campo(20),codigo=UI.campo(10),nombre=UI.campo(18),tipo=UI.campo(14),ubicacion=UI.campo(18),fecha=UI.campo(10);
    private final JComboBox<String> filtro=new JComboBox<>(new String[]{"Todos","OPERATIVO","EN_MANTENIMIENTO","FUERA_DE_SERVICIO"});
    private final JComboBox<String> estado=new JComboBox<>(new String[]{"OPERATIVO","EN_MANTENIMIENTO","FUERA_DE_SERVICIO"});
    private final DefaultTableModel modelo=new DefaultTableModel(new Object[]{"Codigo","Nombre","Tipo","Ubicacion","Adquisicion","Estado"},0){public boolean isCellEditable(int f,int c){return false;}};
    private final JTable tabla=new JTable(modelo);
    private final JLabel resumen=UI.etiqueta("Equipos registrados");
    private final JButton guardar=UI.boton("Registrar"),actualizar=UI.boton("Guardar cambios"),desactivar=UI.boton("Desactivar"),activar=UI.boton("Reactivar"),recargar=UI.boton("Actualizar lista");
    private List<Equipo> datos=List.of();
    private boolean ocupado;
    public EquiposView(){
        setLayout(new BorderLayout(8,8));setBackground(UI.FONDO);setBorder(BorderFactory.createEmptyBorder(8,12,12,12));
        JPanel norte=new JPanel(new BorderLayout());norte.setOpaque(false);norte.add(UI.titulo("Equipos del gimnasio"),BorderLayout.NORTH);JPanel filtros=new JPanel(new GridLayout(1,3,8,0));filtros.setOpaque(false);filtros.add(campo("Buscar por codigo o nombre",buscar));filtros.add(campo("Estado",filtro));filtros.add(campo("Datos guardados",recargar));norte.add(filtros,BorderLayout.CENTER);add(norte,BorderLayout.NORTH);
        tabla.setRowHeight(29);tabla.setBackground(UI.CAMPO);tabla.setForeground(Color.WHITE);tabla.setSelectionBackground(UI.ACENTO);tabla.setSelectionForeground(Color.BLACK);tabla.setAutoCreateRowSorter(true);tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);int[] anchos={110,240,150,200,120,170};for(int n=0;n<anchos.length;n++)tabla.getColumnModel().getColumn(n).setPreferredWidth(anchos[n]);add(UI.scroll(tabla,"Listado de equipos"),BorderLayout.CENTER);
        JPanel sur=new JPanel(new BorderLayout(8,8));sur.setOpaque(false);JPanel formulario=new JPanel(new GridLayout(2,3,12,8));formulario.setOpaque(false);formulario.add(campo("Codigo",codigo));formulario.add(campo("Nombre",nombre));formulario.add(campo("Tipo",tipo));formulario.add(campo("Ubicacion",ubicacion));formulario.add(campo("Adquisicion (AAAA-MM-DD)",fecha));formulario.add(campo("Estado",estado));sur.add(formulario,BorderLayout.NORTH);
        JPanel acciones=new JPanel(new GridLayout(2,3,8,8));acciones.setOpaque(false);acciones.add(guardar);acciones.add(actualizar);acciones.add(desactivar);acciones.add(activar);JButton limpiar=UI.boton("Nuevo / limpiar");acciones.add(limpiar);sur.add(acciones,BorderLayout.CENTER);sur.add(resumen,BorderLayout.SOUTH);add(sur,BorderLayout.SOUTH);
        buscar.getDocument().addDocumentListener(new DocumentListener(){public void insertUpdate(DocumentEvent e){mostrar();}public void removeUpdate(DocumentEvent e){mostrar();}public void changedUpdate(DocumentEvent e){mostrar();}});filtro.addActionListener(e->mostrar());recargar.addActionListener(e->refrescarDatos());
        tabla.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting())seleccionar();});
        guardar.addActionListener(e->guardar(true));actualizar.addActionListener(e->guardar(false));
        desactivar.addActionListener(e->cambiar("FUERA_DE_SERVICIO"));activar.addActionListener(e->cambiar("OPERATIVO"));
        limpiar.addActionListener(e->{tabla.clearSelection();codigo.setEditable(true);codigo.setText("");nombre.setText("");tipo.setText("");ubicacion.setText("");fecha.setText("");estado.setSelectedIndex(0);});
        addHierarchyListener(e->{if((e.getChangeFlags()&java.awt.event.HierarchyEvent.SHOWING_CHANGED)!=0&&isShowing())refrescarDatos();});
    }
    private JPanel campo(String texto,JComponent control){JPanel p=new JPanel(new BorderLayout(0,4));p.setOpaque(false);p.add(UI.etiqueta(texto),BorderLayout.NORTH);p.add(control,BorderLayout.CENTER);return p;}
    public void refrescarDatos(){ejecutar(()->datos=servicio.listar());}
    private String seleccionado(){int f=tabla.getSelectedRow();return f<0?null:String.valueOf(modelo.getValueAt(tabla.convertRowIndexToModel(f),0));}
    private void seleccionar(){String c=seleccionado();if(c==null)return;for(Equipo e:datos)if(e.getCodigoEquipo().equals(c)){codigo.setText(c);codigo.setEditable(false);nombre.setText(e.getNombre());tipo.setText(e.getTipo());ubicacion.setText(e.getUbicacion());fecha.setText(e.getFechaAdquisicion()==null?"":e.getFechaAdquisicion().toString());estado.setSelectedItem(e.getEstado());break;}}
    private void guardar(boolean nuevo){
        try{
            if(!nuevo&&seleccionado()==null){UI.aviso(this,"Selecciona el equipo que deseas actualizar.");return;}
            String c=codigo.getText().trim().toUpperCase(Locale.ROOT),n=nombre.getText().trim(),t=tipo.getText().trim(),u=ubicacion.getText().trim();
            Equipo equipo=new Equipo(c,n,t,u,fecha.getText().isBlank()?null:LocalDate.parse(fecha.getText().trim()),(String)estado.getSelectedItem());
            if(nuevo&&datos.stream().anyMatch(e->e.getCodigoEquipo().equals(c)))throw new IllegalArgumentException("El codigo ya existe. Selecciona el equipo para actualizarlo o reactivarlo.");
            ejecutar(()->{if(!servicio.guardar(equipo,nuevo))throw new IllegalStateException("El equipo ya cambio. Actualiza la lista.");datos=servicio.listar();});
        }catch(java.time.format.DateTimeParseException ex){UI.aviso(this,"Escribe la fecha como AAAA-MM-DD o deja el campo vacio.");}catch(IllegalArgumentException ex){UI.aviso(this,ex.getMessage());}
    }
    private void cambiar(String destino){String c=seleccionado();if(c==null){UI.aviso(this,"Selecciona un equipo.");return;}if(JOptionPane.showConfirmDialog(this,"¿Cambiar el estado del equipo a "+destino+"? El registro se conservara.","Confirmar estado",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)ejecutar(()->{if(!servicio.cambiarEstado(c,destino))throw new IllegalStateException("El equipo ya tiene ese estado o cambio. Actualiza la lista.");datos=servicio.listar();});}
    private void mostrar(){modelo.setRowCount(0);String texto=buscar.getText().trim().toLowerCase(Locale.ROOT),ef=(String)filtro.getSelectedItem();for(Equipo e:datos){if(!"Todos".equals(ef)&&!ef.equals(e.getEstado()))continue;if(!(e.getCodigoEquipo()+" "+e.getNombre()+" "+e.getTipo()+" "+e.getUbicacion()).toLowerCase(Locale.ROOT).contains(texto))continue;modelo.addRow(new Object[]{e.getCodigoEquipo(),e.getNombre(),e.getTipo(),e.getUbicacion(),e.getFechaAdquisicion(),e.getEstado()});}resumen.setText(modelo.getRowCount()+" resultados. Desactivar conserva el equipo fuera de servicio.");}
    private void ejecutar(Runnable tarea){if(ocupado)return;ocupado=true;botones(false);resumen.setText("Consultando equipos...");new SwingWorker<Void,Void>(){protected Void doInBackground(){tarea.run();return null;}protected void done(){try{get();mostrar();}catch(Exception e){Throwable c=e.getCause()==null?e:e.getCause();resumen.setText("No se pudo completar la operacion.");UI.aviso(EquiposView.this,c.getMessage());}finally{ocupado=false;botones(true);}}}.execute();}
    private void botones(boolean valor){for(JButton b:new JButton[]{guardar,actualizar,desactivar,activar,recargar})b.setEnabled(valor);buscar.setEnabled(valor);filtro.setEnabled(valor);tabla.setEnabled(valor);codigo.setEnabled(valor);nombre.setEnabled(valor);tipo.setEnabled(valor);ubicacion.setEnabled(valor);fecha.setEnabled(valor);estado.setEnabled(valor);}
}
