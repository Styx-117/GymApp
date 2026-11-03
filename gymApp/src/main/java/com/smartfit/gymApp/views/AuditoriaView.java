package com.smartfit.gymApp.views;

import com.smartfit.gymApp.model.Auditoria;
import com.smartfit.gymApp.repository.AuditoriaDAO;
import com.smartfit.gymApp.service.AuditoriaService;
import com.smartfit.gymApp.utils.UI;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

public class AuditoriaView extends JPanel {
    private final AuditoriaService servicio=AuditoriaService.getInstance();
    private final JTextField buscar=UI.campo(22),nota=UI.campo(32);
    private final JComboBox<String> estado=new JComboBox<>(new String[]{"Activos","Archivados","Todos"});
    private final DefaultTableModel modelo=new DefaultTableModel(new Object[]{"ID","Fecha y hora","Accion","Detalle","Usuario","Estado"},0){public boolean isCellEditable(int f,int c){return false;}};
    private final JTable tabla=new JTable(modelo);
    private final TablePager paginacion=new TablePager(modelo,50);
    private final JLabel resumen=UI.etiqueta("Historial persistente de acciones");
    private final JButton registrar=UI.boton("Registrar nota"),ultima=UI.boton("Ver ultima"),archivar=UI.boton("Archivar ultima"),restaurar=UI.boton("Restaurar seleccionada"),recargar=UI.boton("Actualizar lista");
    private List<AuditoriaDAO.Registro> datos=List.of();
    private boolean ocupado;
    public AuditoriaView(){
        setLayout(new BorderLayout(8,8));setBackground(UI.FONDO);setBorder(BorderFactory.createEmptyBorder(8,12,12,12));
        JPanel norte=new JPanel(new BorderLayout());norte.setOpaque(false);norte.add(UI.titulo("Historial de acciones"),BorderLayout.NORTH);
        JPanel filtros=new JPanel(new GridLayout(1,3,8,0));filtros.setOpaque(false);filtros.add(campo("Buscar por accion o detalle",buscar));filtros.add(campo("Estado",estado));filtros.add(campo("Datos guardados",recargar));norte.add(filtros,BorderLayout.CENTER);add(norte,BorderLayout.NORTH);
        tabla.setRowHeight(29);tabla.setBackground(UI.CAMPO);tabla.setForeground(Color.WHITE);tabla.setSelectionBackground(UI.ACENTO);tabla.setSelectionForeground(Color.BLACK);tabla.setAutoCreateRowSorter(true);tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);int[] anchos={55,190,210,350,90,110};for(int n=0;n<anchos.length;n++)tabla.getColumnModel().getColumn(n).setPreferredWidth(anchos[n]);
        JPanel resultados=new JPanel(new BorderLayout(0,6));resultados.setOpaque(false);
        resultados.add(UI.scroll(tabla,"Acciones mas recientes primero"),BorderLayout.CENTER);
        resultados.add(paginacion,BorderLayout.SOUTH);add(resultados,BorderLayout.CENTER);
        JPanel sur=new JPanel(new BorderLayout(8,8));sur.setOpaque(false);JPanel escribir=new JPanel(new BorderLayout(8,4));escribir.setOpaque(false);escribir.add(UI.etiqueta("Nota (hasta 255 caracteres)"),BorderLayout.NORTH);escribir.add(nota,BorderLayout.CENTER);escribir.add(registrar,BorderLayout.EAST);sur.add(escribir,BorderLayout.NORTH);
        JPanel acciones=new JPanel(new GridLayout(1,3,8,0));acciones.setOpaque(false);acciones.add(ultima);acciones.add(archivar);acciones.add(restaurar);sur.add(acciones,BorderLayout.CENTER);sur.add(resumen,BorderLayout.SOUTH);add(sur,BorderLayout.SOUTH);
        buscar.getDocument().addDocumentListener(new DocumentListener(){public void insertUpdate(DocumentEvent e){mostrar();}public void removeUpdate(DocumentEvent e){mostrar();}public void changedUpdate(DocumentEvent e){mostrar();}});estado.addActionListener(e->mostrar());
        recargar.addActionListener(e->refrescarDatos());
        registrar.addActionListener(e->{String texto=nota.getText().trim();if(texto.isEmpty()){UI.aviso(this,"Escribe una nota para registrar.");return;}ejecutar(()->{servicio.registrar("NOTA_PERSONAL",texto);cargar();});});
        ultima.addActionListener(e->{Auditoria a=servicio.ultima();UI.info(this,a==null?"No hay acciones activas.":"Ultima accion: #"+a.getIdAuditoria()+"\n"+a.getAccion()+"\n"+a.getDetalle());});
        archivar.addActionListener(e->{Auditoria a=servicio.ultima();if(a==null){UI.aviso(this,"No hay acciones activas para archivar.");return;}if(JOptionPane.showConfirmDialog(this,"¿Archivar la ultima accion del historial?\nEl registro se conserva. Esta accion no deshace la operacion registrada.","Archivar historial",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)ejecutar(()->{servicio.archivarUltima();datos=servicio.instantanea();});});
        restaurar.addActionListener(e->{int f=tabla.getSelectedRow();if(f<0){UI.aviso(this,"Selecciona un registro archivado.");return;}int modeloFila=tabla.convertRowIndexToModel(f);int id=((Number)modelo.getValueAt(modeloFila,0)).intValue();if(!"ARCHIVADO".equals(modelo.getValueAt(modeloFila,5))){UI.aviso(this,"El registro seleccionado ya esta activo.");return;}ejecutar(()->{if(!servicio.restaurar(id))throw new IllegalStateException("El registro ya cambio de estado. Actualiza la lista.");datos=servicio.instantanea();});});
        addHierarchyListener(e->{if((e.getChangeFlags()&java.awt.event.HierarchyEvent.SHOWING_CHANGED)!=0&&isShowing())refrescarDatos();});
    }
    private JPanel campo(String texto,JComponent control){JPanel p=new JPanel(new BorderLayout(0,4));p.setOpaque(false);p.add(UI.etiqueta(texto),BorderLayout.NORTH);p.add(control,BorderLayout.CENTER);return p;}
    public void refrescarDatos(){ejecutar(()->{servicio.activarPersistencia();datos=servicio.refrescarYListar();});}
    private void cargar(){datos=servicio.refrescarYListar();}
    private void mostrar(){
        List<Object[]> filas=new ArrayList<>();String texto=buscar.getText().trim().toLowerCase(Locale.ROOT);int filtro=estado.getSelectedIndex();
        for(int n=datos.size()-1;n>=0;n--){AuditoriaDAO.Registro r=datos.get(n);Auditoria a=r.auditoria();
            if((filtro==0&&!r.activo())||(filtro==1&&r.activo()))continue;
            if(!(a.getIdAuditoria()+" "+a.getAccion()+" "+a.getDetalle()+" "+a.getFechaHora()).toLowerCase(Locale.ROOT).contains(texto))continue;
            filas.add(new Object[]{a.getIdAuditoria(),a.getFechaHora(),a.getAccion(),a.getDetalle(),a.getIdUsuario()==0?"Sistema":a.getIdUsuario(),r.activo()?"ACTIVO":"ARCHIVADO"});
        }
        paginacion.setRows(filas);
        resumen.setText(filas.size()+" resultados. "+servicio.total()+" acciones activas. Archivar conserva la operacion original.");
    }
    private void ejecutar(Runnable tarea){
        if(ocupado)return;ocupado=true;botones(false);resumen.setText("Consultando el historial...");
        new SwingWorker<Void,Void>(){protected Void doInBackground(){tarea.run();return null;}protected void done(){try{get();mostrar();}catch(Exception e){Throwable c=e.getCause()==null?e:e.getCause();resumen.setText("No se pudo completar la operacion.");UI.aviso(AuditoriaView.this,c.getMessage());}finally{ocupado=false;botones(true);}}}.execute();
    }
    private void botones(boolean valor){paginacion.setBusy(!valor);for(JButton b:new JButton[]{registrar,ultima,archivar,restaurar,recargar})b.setEnabled(valor);buscar.setEnabled(valor);estado.setEnabled(valor);nota.setEnabled(valor);tabla.setEnabled(valor);}
}
