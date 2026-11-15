package com.smartfit.gymApp.views;

import com.smartfit.gymApp.model.*;
import com.smartfit.gymApp.repository.ClaseDAO;
import com.smartfit.gymApp.service.*;
import com.smartfit.gymApp.structures.arboles.*;
import com.smartfit.gymApp.structures.avl.ArbolAVL;
import com.smartfit.gymApp.structures.listas.ListaEnlazadaSimple;
import com.smartfit.gymApp.utils.UI;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Locale;

/** Consulta y cambio de estado de registros reales mediante índices propios. */
public final class ConsultaIndicesView extends JPanel {
    private final SocioService socios = new SocioService();
    private final EquipoService equipos = new EquipoService();
    private final ClaseService clases = new ClaseService();
    private final JComboBox<String> tipo = new JComboBox<>(new String[]{"Socios · AVL", "Equipos · ABB", "Clases · árbol binario"});
    private final JComboBox<String> orden = new JComboBox<>(new String[]{"Orden de registro", "Orden inverso"});
    private final JTextField codigo = UI.campo(16);
    private final PanelArbol dibujo = new PanelArbol();
    private final JTextArea resultado = UI.areaSalida();
    private final JLabel estado = UI.etiqueta("Consulta por código o ID. Los cambios de estado se guardan en la base de datos.");
    private final JButton actualizar = UI.boton("Actualizar"), buscar = UI.boton("Buscar"), baja = UI.boton("Desactivar"), alta = UI.boton("Reactivar");
    private final JButton pre = UI.boton("Preorden"), in = UI.boton("Inorden"), post = UI.boton("Postorden");
    private List<Socio> datosSocios = List.of();
    private List<Equipo> datosEquipos = List.of();
    private List<ClaseDAO.Registro> datosClases = List.of();
    private ArbolAVL<Socio> avl = new ArbolAVL<>();
    private ABB<Equipo> abb = new ABB<>();
    private ArbolBinario<Integer> binario = new ArbolBinario<>();
    private ListaEnlazadaSimple<ClaseDAO.Registro> catalogo = new ListaEnlazadaSimple<>();
    private boolean ocupado;

    public ConsultaIndicesView() {
        super(new BorderLayout(10,10)); setBackground(UI.FONDO);
        setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        JPanel norte = new JPanel(new BorderLayout()); norte.setOpaque(false);
        norte.add(UI.titulo("Consulta de socios, equipos y clases"),BorderLayout.NORTH);
        JPanel controles = new JPanel(new GridLayout(2,1,0,6)); controles.setOpaque(false);
        JPanel seleccion=UI.fila(), operaciones=UI.fila();
        seleccion.add(tipo); seleccion.add(orden);
        for (JComponent c : new JComponent[]{codigo,buscar,actualizar,baja,alta}) operaciones.add(c);
        controles.add(seleccion); controles.add(operaciones);
        for (JComboBox<?> c : new JComboBox[]{tipo,orden}) { c.setBackground(UI.CAMPO); c.setForeground(Color.WHITE); }
        norte.add(controles,BorderLayout.CENTER); norte.add(estado,BorderLayout.SOUTH); add(norte,BorderLayout.NORTH);
        JScrollPane visual = new JScrollPane(dibujo); visual.getViewport().setBackground(UI.PANEL); add(visual,BorderLayout.CENTER);
        JPanel sur = new JPanel(new BorderLayout()); sur.setOpaque(false); JPanel recorridos = UI.fila();
        recorridos.add(pre); recorridos.add(in); recorridos.add(post); sur.add(recorridos,BorderLayout.NORTH);
        resultado.setRows(7); sur.add(UI.scroll(resultado,"Consulta, recorridos y balanceo"),BorderLayout.CENTER); add(sur,BorderLayout.SOUTH);
        actualizar.addActionListener(e -> cargar()); tipo.addActionListener(e -> mostrar()); orden.addActionListener(e -> reconstruir());
        buscar.addActionListener(e -> consultar()); codigo.addActionListener(e -> consultar());
        baja.addActionListener(e -> cambiar(false)); alta.addActionListener(e -> cambiar(true));
        pre.addActionListener(e -> recorrido(0)); in.addActionListener(e -> recorrido(1)); post.addActionListener(e -> recorrido(2));
        addHierarchyListener(e -> { if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED)!=0 && isShowing()) cargar(); });
    }
    private record Datos(List<Socio> socios,List<Equipo> equipos,List<ClaseDAO.Registro> clases) {}
    private Datos leer() { return new Datos(socios.listarTodos(),equipos.listar(),clases.listar()); }
    private void cargar() { ejecutar(() -> leer()); }
    private void ejecutar(java.util.concurrent.Callable<Datos> operacion) {
        ejecutar(operacion, null);
    }
    private void ejecutar(java.util.concurrent.Callable<Datos> operacion, Runnable cambioIndice) {
        if (ocupado) return; ocupado=true; controles(false); estado.setText("Consultando la base de datos…");
        new SwingWorker<Datos,Void>() {
            protected Datos doInBackground() throws Exception { return operacion.call(); }
            protected void done() {
                try { Datos d=get(); datosSocios=d.socios(); datosEquipos=d.equipos(); datosClases=d.clases(); if(cambioIndice==null)reconstruir();else{cambioIndice.run();mostrar();} estado.setText("Índices de registros activos. Reactivar admite también códigos inactivos."); }
                catch(Exception ex) { estado.setText("No se pudo completar la operación. Actualiza para reintentar."); UI.aviso(ConsultaIndicesView.this,ex.getCause()==null?ex.getMessage():ex.getCause().getMessage()); }
                finally { ocupado=false; controles(true); }
            }
        }.execute();
    }
    private void controles(boolean enabled) { for (JComponent c : new JComponent[]{tipo,orden,codigo,actualizar,buscar,baja,alta,pre,in,post}) c.setEnabled(enabled); }
    private void reconstruir() {
        avl=new ArbolAVL<>(); avl.setEtiquetador(Socio::getCodigoSocio);
        abb=new ABB<>(); abb.setEtiquetador(Equipo::getCodigoEquipo);
        binario=new ArbolBinario<>(); catalogo=new ListaEnlazadaSimple<>();
        boolean inverso=orden.getSelectedIndex()==1;
        for(int i=0;i<datosSocios.size();i++){Socio s=datosSocios.get(inverso?datosSocios.size()-1-i:i); if(Boolean.TRUE.equals(s.getActivo())) avl.insertar(s);}
        for(int i=0;i<datosEquipos.size();i++){Equipo e=datosEquipos.get(inverso?datosEquipos.size()-1-i:i); if(!"FUERA_DE_SERVICIO".equals(e.getEstado())) abb.insertar(e);}
        for(int i=0;i<datosClases.size();i++){var r=datosClases.get(inverso?datosClases.size()-1-i:i);catalogo.agregar(r);if(r.activo())binario.insertar(r.clase().getIdClase());}
        mostrar();
    }
    private void mostrar() {
        dibujo.setResaltado(null);
        dibujo.setRaiz(switch(tipo.getSelectedIndex()){case 0 -> avl.getRaizVisual();case 1 -> abb.getRaizVisual();default -> binario.getRaizVisual();});
        SwingUtilities.invokeLater(() -> {if(dibujo.getParent() instanceof JViewport ventana)ventana.setViewPosition(new Point(Math.max(0,(dibujo.getWidth()-ventana.getWidth())/2),0));});
        resultado.setText(tipo.getSelectedIndex()==0 ? "Rotaciones: "+avl.getTotalRotaciones()+"\n"+avl.getBitacora() : "Consulta un código para ver los datos del registro. Desactivar retira su nodo del índice activo.");
    }
    private String clave(){return codigo.getText().trim().toUpperCase(Locale.ROOT);}
    private void consultar() {
        String c=clave(); if(c.isEmpty()){UI.aviso(this,"Ingresa un código o ID de clase.");return;}
        String texto=null;
        try {
            if(tipo.getSelectedIndex()==0){Socio s=avl.buscar(new Socio(c,"","",""));if(s!=null)texto=s.getNombreCompleto()+" · DNI "+s.getDni()+" · Teléfono "+s.getTelefono()+"\nComparaciones: "+avl.getComparaciones();}
            else if(tipo.getSelectedIndex()==1){Equipo e=abb.buscar(new Equipo(c,null,null,null,null,null));if(e!=null)texto=e.getNombre()+" · "+e.getUbicacion()+" · "+e.getEstado()+"\nComparaciones: "+abb.getComparaciones();}
            else {int id=Integer.parseInt(c);if(binario.buscar(id)!=null){var r=catalogo.buscar(x->x.clase().getIdClase()==id);texto=r.toString();}}
            resultado.setText(texto==null?"No existe un registro activo con ese código.":texto); dibujo.setResaltado(texto==null?null:c);
        }catch(NumberFormatException ex){UI.aviso(this,"El ID de clase debe ser un número.");}
    }
    private void recorrido(int modo) {
        NodoVisual raiz=switch(tipo.getSelectedIndex()){case 0->avl.getRaizVisual();case 1->abb.getRaizVisual();default->binario.getRaizVisual();};
        resultado.setText(switch(modo){case 0->Recorridos.preorden(raiz);case 1->Recorridos.inorden(raiz);default->Recorridos.postorden(raiz);});
    }
    private void cambiar(boolean activo) {
        String c=clave(); int seleccion=tipo.getSelectedIndex();
        if(c.isEmpty()){UI.aviso(this,"Ingresa un código o ID.");return;}
        if(JOptionPane.showConfirmDialog(this,(activo?"¿Reactivar ":"¿Desactivar ")+c+"?","Confirmar estado",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;
        ejecutar(() -> {
            boolean ok;
            if(seleccion==0){Socio encontrado=null;for(Socio s:datosSocios)if(s.getCodigoSocio().equals(c)){encontrado=s;break;}if(encontrado==null)throw new IllegalArgumentException("Socio inexistente.");ok=socios.cambiarEstado(Math.toIntExact(encontrado.getIdSocio()),activo);}
            else if(seleccion==1)ok=equipos.cambiarEstado(c,activo?"OPERATIVO":"FUERA_DE_SERVICIO");
            else ok=clases.cambiarEstado(Integer.parseInt(c),activo);
            if(!ok)throw new IllegalStateException("El registro no existe o ya tiene ese estado.");
            return leer();
        }, () -> {
            if(seleccion==0){
                avl.eliminar(new Socio(c,"","",""));
                if(activo)for(Socio s:datosSocios)if(s.getCodigoSocio().equals(c))avl.insertar(s);
            }else if(seleccion==1){
                abb.eliminar(new Equipo(c,null,null,null,null,null));
                if(activo)for(Equipo e:datosEquipos)if(e.getCodigoEquipo().equals(c))abb.insertar(e);
            }else{
                int id=Integer.parseInt(c);binario.eliminar(id);catalogo.eliminar(r->r.clase().getIdClase()==id);
                for(var r:datosClases)if(r.clase().getIdClase()==id){catalogo.agregar(r);if(activo)binario.insertar(id);}
            }
        });
    }
}
