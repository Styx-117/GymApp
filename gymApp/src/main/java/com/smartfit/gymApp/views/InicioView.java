package com.smartfit.gymApp.views;

import com.smartfit.gymApp.repository.DashboardDAO;
import com.smartfit.gymApp.service.DashboardService;
import com.smartfit.gymApp.utils.UI;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.text.NumberFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Consumer;

/** Resumen conectado sin graficas ni adornos. */
public class InicioView extends JPanel {
    private final JLabel[] valores=new JLabel[8],detalles=new JLabel[8];
    private final JLabel estado=new JLabel("Conectando con la base de datos…");
    private final JButton actualizar=UI.boton("Actualizar resumen");
    private final DefaultTableModel agenda=modelo("Clase","Horario","Entrenador","Cupos");
    private final DefaultTableModel incidencias=modelo("Equipo","Trabajo pendiente","Prioridad");
    private boolean cargando;
    private final DashboardService servicio=new DashboardService();
    private SwingWorker<DashboardDAO.Resumen,Void> tarea;
    private long version;

    public InicioView(String usuario,Consumer<String> navegar) {
        setLayout(new BorderLayout(0,18));setBackground(UI.FONDO);setBorder(BorderFactory.createEmptyBorder(24,28,20,28));
        JPanel cabecera=new JPanel(new BorderLayout(16,8));cabecera.setOpaque(false);
        JPanel textos=new JPanel(new GridLayout(2,1,0,5));textos.setOpaque(false);
        JLabel titulo=new JLabel("Bienvenido, "+usuario);titulo.setForeground(Color.WHITE);titulo.setFont(new Font("Segoe UI",Font.BOLD,27));
        JLabel fecha=new JLabel(LocalDate.now(ZoneId.of("America/Lima")).format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM",Locale.forLanguageTag("es-PE")))+" · Resumen de la sede");
        fecha.setForeground(Color.LIGHT_GRAY);textos.add(titulo);textos.add(fecha);cabecera.add(textos,BorderLayout.CENTER);JPanel control=new JPanel(new FlowLayout(FlowLayout.RIGHT,0,8));control.setOpaque(false);actualizar.setPreferredSize(new Dimension(155,34));control.add(actualizar);cabecera.add(control,BorderLayout.EAST);add(cabecera,BorderLayout.NORTH);
        JPanel cuerpo=new JPanel(new BorderLayout(0,20));cuerpo.setOpaque(false);
        JPanel indicadores=new JPanel(new GridLayout(2,4,18,16));indicadores.setOpaque(false);
        String[] nombres={"Socios activos","Entrenadores activos","Clases de hoy","Reservas confirmadas","Mantenimiento pendiente","Membresías por vencer","Asistencias de hoy","Cobros del mes"};
        for(int i=0;i<8;i++) {
            JPanel celda=new JPanel(new BorderLayout(0,5));celda.setBackground(UI.PANEL);celda.setBorder(BorderFactory.createEmptyBorder(12,14,12,14));
            JLabel etiqueta=new JLabel(nombres[i]);etiqueta.setFont(new Font("Segoe UI",Font.PLAIN,12));etiqueta.setForeground(Color.LIGHT_GRAY);
            valores[i]=new JLabel("—");valores[i].setForeground(Color.WHITE);valores[i].setFont(new Font("Segoe UI",Font.BOLD,27));
            detalles[i]=new JLabel("Consultando…");detalles[i].setFont(new Font("Segoe UI",Font.PLAIN,11));detalles[i].setForeground(Color.LIGHT_GRAY);
            celda.add(etiqueta,BorderLayout.NORTH);celda.add(valores[i],BorderLayout.CENTER);celda.add(detalles[i],BorderLayout.SOUTH);indicadores.add(celda);
        }
        cuerpo.add(indicadores,BorderLayout.NORTH);
        JPanel tablas=new JPanel(new GridLayout(1,2,16,0));tablas.setOpaque(false);tablas.add(bloque("Agenda de clases activas",agenda));tablas.add(bloque("Próximas atenciones de mantenimiento",incidencias));cuerpo.add(tablas,BorderLayout.CENTER);
        JScrollPane scroll=new JScrollPane(cuerpo);scroll.setBorder(null);scroll.getViewport().setBackground(UI.FONDO);scroll.getVerticalScrollBar().setUnitIncrement(16);
        cuerpo.setPreferredSize(new Dimension(760,500));add(scroll,BorderLayout.CENTER);
        JPanel pie=new JPanel(new BorderLayout());pie.setOpaque(false);estado.setForeground(Color.LIGHT_GRAY);estado.setFont(new Font("Segoe UI",Font.PLAIN,11));pie.add(estado,BorderLayout.CENTER);
        JPanel acciones=UI.fila();for(String nombre:new String[]{"Socios","Reservas","Incidencias"}) {JButton b=UI.boton(nombre);b.addActionListener(e->navegar.accept(nombre));acciones.add(b);}pie.add(acciones,BorderLayout.EAST);add(pie,BorderLayout.SOUTH);
        actualizar.addActionListener(e->refrescarDatos());
        Timer timer=new Timer(60000,e->{if(isShowing())refrescarDatos();});
        addHierarchyListener(e->{if((e.getChangeFlags()&HierarchyEvent.SHOWING_CHANGED)!=0){if(isShowing()){timer.start();refrescarDatos();}else {timer.stop();cancelarCarga();}}});
    }
    private static DefaultTableModel modelo(String...c){return new DefaultTableModel(c,0){@Override public boolean isCellEditable(int r,int col){return false;}};}
    private JPanel bloque(String titulo,DefaultTableModel modelo) {
        JPanel p=new JPanel(new BorderLayout(0,8));p.setOpaque(false);JLabel l=new JLabel(titulo);l.setForeground(Color.WHITE);l.setFont(new Font("Segoe UI",Font.BOLD,15));p.add(l,BorderLayout.NORTH);
        JTable tabla=new JTable(modelo);UI.estilizarTabla(tabla);
        
        // Estilo oscuro para la cabecera
        tabla.getTableHeader().setBackground(UI.PANEL);
        tabla.getTableHeader().setForeground(Color.WHITE);

        JScrollPane sp=new JScrollPane(tabla);sp.setBorder(BorderFactory.createLineBorder(UI.CAMPO));sp.getViewport().setBackground(UI.PANEL);p.add(sp,BorderLayout.CENTER);return p;
    }
    private void cancelarCarga() {
        version++;if(tarea!=null&&!tarea.isDone())tarea.cancel(true);
        cargando=false;actualizar.setEnabled(true);
    }
    public void refrescarDatos() {
        if(cargando)return;cargando=true;actualizar.setEnabled(false);estado.setText("Actualizando datos…");
        long actual=++version;
        tarea=new SwingWorker<DashboardDAO.Resumen,Void>() {
            protected DashboardDAO.Resumen doInBackground() throws Exception{return servicio.cargar();}
            protected void done(){if(actual!=version||isCancelled())return;try{
                var r=get();long[] c=r.cifras();int[] indices={0,2,4,6,8,10,12};for(int i=0;i<7;i++)valores[i].setText(Long.toString(c[indices[i]]));
                NumberFormat moneda=NumberFormat.getNumberInstance(Locale.forLanguageTag("es-PE"));moneda.setMinimumFractionDigits(2);moneda.setMaximumFractionDigits(2);valores[7].setText("S/ "+moneda.format(r.cobros()));
                String[] d={c[1]+" inactivos",c[3]+" inactivos",c[5]+" clases activas",c[7]+" en lista de espera",c[9]+" atendidas","Próximos 7 días · "+c[11]+" vencidas",c[13]+" socios en sala","Pagos registrados este mes"};
                for(int i=0;i<8;i++)detalles[i].setText(d[i]);agenda.setRowCount(0);r.agenda().forEach(f->agenda.addRow(new Object[]{f[0],f[1]+" "+f[2],f[3],f[4]}));incidencias.setRowCount(0);r.mantenimiento().forEach(incidencias::addRow);
                estado.setText("Actualizado "+LocalTime.now(ZoneId.of("America/Lima")).format(DateTimeFormatter.ofPattern("HH:mm:ss"))+" · Actualización cada minuto");
            }catch(Exception ex){for(int i=0;i<8;i++){valores[i].setText("—");detalles[i].setText("No disponible");}agenda.setRowCount(0);incidencias.setRowCount(0);estado.setText("No se pudo actualizar. Comprueba la conexión y pulsa Actualizar resumen.");}
            finally{cargando=false;actualizar.setEnabled(true);}}
        };tarea.execute();
    }
}