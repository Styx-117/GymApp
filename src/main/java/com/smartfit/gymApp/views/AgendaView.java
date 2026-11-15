package com.smartfit.gymApp.views;

import com.smartfit.gymApp.service.*;
import com.smartfit.gymApp.utils.UI;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public final class AgendaView extends JPanel {
    private final DefaultTableModel modelo=new DefaultTableModel(new String[]{"Hora","Lunes","Martes","Miércoles","Jueves","Viernes","Sábado","Domingo"},0){public boolean isCellEditable(int r,int c){return false;}};
    private final JTable tabla=new JTable(modelo);
    private final JTextArea detalle=UI.areaSalida();
    private final JButton actualizar=UI.boton("Actualizar horario");
    private AgendaSemanal agenda;
    private boolean cargando;
    public AgendaView(){
        super(new BorderLayout(10,10));setBackground(UI.FONDO);setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        JPanel cabecera=new JPanel(new BorderLayout());cabecera.setOpaque(false);cabecera.add(UI.titulo("Horario semanal de clases"),BorderLayout.CENTER);cabecera.add(actualizar,BorderLayout.EAST);add(cabecera,BorderLayout.NORTH);
        UI.estilizarTabla(tabla);tabla.setCellSelectionEnabled(true);tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object value,boolean selected,boolean focus,int row,int column){
                JLabel label=(JLabel)super.getTableCellRendererComponent(t,value,selected,focus,row,column);
                label.setBackground(UI.CAMPO);label.setForeground(Color.WHITE);label.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));return label;
            }
        });
        for(int c=0;c<8;c++)tabla.getColumnModel().getColumn(c).setPreferredWidth(c==0?65:170);
        add(UI.scroll(tabla,"Selecciona un día y hora para consultar las clases"),BorderLayout.CENTER);
        detalle.setRows(6);add(UI.scroll(detalle,"Horario, entrenador y disponibilidad"),BorderLayout.SOUTH);
        tabla.getSelectionModel().addListSelectionListener(e->seleccionar());tabla.getColumnModel().getSelectionModel().addListSelectionListener(e->seleccionar());
        actualizar.addActionListener(e->cargar());addHierarchyListener(e->{if((e.getChangeFlags()&java.awt.event.HierarchyEvent.SHOWING_CHANGED)!=0&&isShowing())cargar();});
    }
    private void seleccionar(){int h=tabla.getSelectedRow(),d=tabla.getSelectedColumn()-1;if(agenda!=null&&h>=0&&d>=0)detalle.setText(agenda.detalle(d,h));}
    private void cargar(){
        if(cargando)return;cargando=true;actualizar.setEnabled(false);detalle.setText("Consultando clases…");
        new SwingWorker<AgendaSemanal,Void>(){
            protected AgendaSemanal doInBackground(){return new AgendaSemanal(new ClaseService().listar());}
            protected void done(){try{agenda=get();modelo.setRowCount(0);for(int h=0;h<24;h++){Object[] fila=new Object[8];fila[0]=String.format("%02d:00",h);for(int d=0;d<7;d++)fila[d+1]=agenda.resumen(d,h);modelo.addRow(fila);}detalle.setText("Los cambios de Clases se reflejan al actualizar. Las clases simultáneas aparecen en el mismo bloque.");}catch(Exception ex){detalle.setText("No se pudo cargar el horario. Pulsa Actualizar para reintentar.");}finally{cargando=false;actualizar.setEnabled(true);}}
        }.execute();
    }
}
