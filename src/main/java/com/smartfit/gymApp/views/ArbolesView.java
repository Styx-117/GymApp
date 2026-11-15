package com.smartfit.gymApp.views;

import com.smartfit.gymApp.model.Equipo;
import com.smartfit.gymApp.model.Socio;
import com.smartfit.gymApp.service.EquipoIndiceService;
import com.smartfit.gymApp.service.SocioIndiceService;
import com.smartfit.gymApp.structures.arboles.ABB;
import com.smartfit.gymApp.structures.arboles.ArbolBinario;
import com.smartfit.gymApp.structures.arboles.NodoVisual;
import com.smartfit.gymApp.structures.avl.ArbolAVL;
import com.smartfit.gymApp.utils.UI;

import javax.swing.*;
import java.awt.*;


public class ArbolesView extends JPanel {

   
    private interface Ops {
        boolean insertar(String codigo);
        String buscar(String codigo);          
        boolean eliminar(String codigo);
        String preorden();
        String inorden();
        String postorden();
        String porNiveles();
        NodoVisual raiz();
        int altura();
        int tamanio();
        String extra();                       
        void vaciar();
        void limpiarExtra();
    }

    public ArbolesView() {
        setLayout(new BorderLayout());
        setBackground(UI.FONDO);
        add(UI.titulo("Árboles: Binario, ABB y AVL"), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Árbol binario (códigos)", new PestanaArbol(opsBinario(), "SM-", "Árbol binario: se llena por niveles, la búsqueda recorre todo el árbol."));
        tabs.addTab("ABB (equipos)", new PestanaArbol(opsABB(), "EQ-", "ABB por codigoEquipo (ej. EQ-0001). Prueba una secuencia ordenada: se degenera en lista."));
        tabs.addTab("AVL (socios)", new PestanaArbol(opsAVL(), "SM-", "AVL por codigoSocio (ej. SM-0001). Misma secuencia ordenada: el árbol se balancea con rotaciones."));
        add(tabs, BorderLayout.CENTER);
    }

    
    private Ops opsBinario() {
        ArbolBinario<String> arbol = new ArbolBinario<>();
        return new Ops() {
            public boolean insertar(String c) { return arbol.insertar(c); }
            public String buscar(String c) { return arbol.contiene(c) ? "Encontrado: " + c : null; }
            public boolean eliminar(String c) { return arbol.eliminar(c); }
            public String preorden() { return arbol.preorden(); }
            public String inorden() { return arbol.inorden(); }
            public String postorden() { return arbol.postorden(); }
            public String porNiveles() { return arbol.porNiveles(); }
            public NodoVisual raiz() { return arbol.getRaizVisual(); }
            public int altura() { return arbol.altura(); }
            public int tamanio() { return arbol.tamanio(); }
            public String extra() { return ""; }
            public void vaciar() { arbol.vaciar(); }
            public void limpiarExtra() { }
        };
    }

    private Ops opsABB() {
        ABB<Equipo> arbol = EquipoIndiceService.getInstance().getArbol();
        EquipoIndiceService srv = EquipoIndiceService.getInstance();
        return new Ops() {
            public boolean insertar(String c) {
                return srv.agregar(new Equipo(c, "Equipo " + c, "GENERAL", "Sede Ica", null, "OPERATIVO"));
            }
            public String buscar(String c) {
                Equipo e = srv.buscarPorCodigo(c);
                return e == null ? null : "Encontrado: " + e.getCodigoEquipo() + " - " + e.getNombre()
                        + "   (comparaciones: " + arbol.getComparaciones() + ")";
            }
            public boolean eliminar(String c) { return srv.eliminar(c); }
            public String preorden() { return arbol.preorden(); }
            public String inorden() { return arbol.inorden(); }
            public String postorden() { return arbol.postorden(); }
            public String porNiveles() { return arbol.porNiveles(); }
            public NodoVisual raiz() { return arbol.getRaizVisual(); }
            public int altura() { return arbol.altura(); }
            public int tamanio() { return arbol.tamanio(); }
            public String extra() { return ""; }
            public void vaciar() { arbol.vaciar(); }
            public void limpiarExtra() { }
        };
    }

    private Ops opsAVL() {
        ArbolAVL<Socio> arbol = SocioIndiceService.getInstance().getArbol();
        SocioIndiceService srv = SocioIndiceService.getInstance();
        return new Ops() {
            public boolean insertar(String c) { return srv.agregar(new Socio(c, "Socio", c, "00000000")); }
            public String buscar(String c) {
                Socio s = srv.buscarPorCodigo(c);
                return s == null ? null : "Encontrado: " + s.getCodigoSocio() + " - " + s.getNombreCompleto()
                        + "   (comparaciones: " + arbol.getComparaciones() + ")";
            }
            public boolean eliminar(String c) { return srv.eliminar(c); }
            public String preorden() { return arbol.preorden(); }
            public String inorden() { return arbol.inorden(); }
            public String postorden() { return arbol.postorden(); }
            public String porNiveles() { return arbol.porNiveles(); }
            public NodoVisual raiz() { return arbol.getRaizVisual(); }
            public int altura() { return arbol.altura(); }
            public int tamanio() { return arbol.tamanio(); }
            public String extra() { return arbol.getBitacora() + "\nRotaciones totales: " + arbol.getTotalRotaciones(); }
            public void vaciar() { arbol.vaciar(); }
            public void limpiarExtra() { arbol.limpiarBitacora(); }
        };
    }

    // ---------------------------------------------------------------- pestaña
    private class PestanaArbol extends JPanel {

        private final Ops ops;
        private final String prefijo;
        private final PanelArbol panelArbol = new PanelArbol();
        private final JTextField txtCodigo = UI.campo(10);
        private final JTextField txtSecuencia = UI.campo(42);
        private final JTextArea txtResultado = UI.areaSalida();
        private final JTextArea txtBitacora = UI.areaSalida();

        PestanaArbol(Ops ops, String prefijo, String ayuda) {
            this.ops = ops;
            this.prefijo = prefijo;
            setLayout(new BorderLayout());
            setBackground(UI.FONDO);

            JPanel norte = new JPanel(new GridLayout(4, 1));
            norte.setBackground(UI.PANEL);
            JPanel f0 = UI.fila();
            f0.add(UI.etiqueta(ayuda));
            JPanel f1 = UI.fila();
            f1.add(UI.etiqueta("Código:"));
            f1.add(txtCodigo);
            JButton btnIns = UI.boton("Insertar");
            JButton btnBus = UI.boton("Buscar");
            JButton btnEli = UI.boton("Eliminar");
            JButton btnVac = UI.boton("Vaciar");
            f1.add(btnIns);
            f1.add(btnBus);
            f1.add(btnEli);
            f1.add(btnVac);
            JPanel f2 = UI.fila();
            f2.add(UI.etiqueta("Secuencia (separada por comas):"));
            txtSecuencia.setText(ejemplo());
            f2.add(txtSecuencia);
            JButton btnSec = UI.boton("Insertar secuencia");
            f2.add(btnSec);
            JPanel f3 = UI.fila();
            JButton btnPre = UI.boton("Preorden");
            JButton btnIn = UI.boton("Inorden");
            JButton btnPost = UI.boton("Postorden");
            JButton btnNiv = UI.boton("Por niveles");
            f3.add(btnPre);
            f3.add(btnIn);
            f3.add(btnPost);
            f3.add(btnNiv);
            norte.add(f0);
            norte.add(f1);
            norte.add(f2);
            norte.add(f3);
            add(norte, BorderLayout.NORTH);

            JScrollPane sp = new JScrollPane(panelArbol);
            sp.getViewport().setBackground(UI.PANEL);
            add(sp, BorderLayout.CENTER);

            txtResultado.setRows(5);
            txtBitacora.setRows(5);
            JPanel sur = new JPanel(new GridLayout(1, 2, 10, 0));
            sur.setOpaque(false);
            sur.add(UI.scroll(txtResultado, "Resultado"));
            sur.add(UI.scroll(txtBitacora, "Información / bitácora de rotaciones"));
            add(sur, BorderLayout.SOUTH);

            btnIns.addActionListener(e -> {
                String c = codigo();
                if (c == null) return;
                boolean ok = ops.insertar(c);
                txtResultado.setText(ok ? "Insertado: " + c : "No se insertó: la clave " + c + " ya existe.");
                refrescar();
            });
            btnBus.addActionListener(e -> {
                String c = codigo();
                if (c == null) return;
                String r = ops.buscar(c);
                txtResultado.setText(r != null ? r : "No existe el código " + c + " en el árbol.");
                panelArbol.setResaltado(r != null ? c : null);
            });
            btnEli.addActionListener(e -> {
                String c = codigo();
                if (c == null) return;
                boolean ok = ops.eliminar(c);
                txtResultado.setText(ok ? "Eliminado: " + c : "No se pudo eliminar: " + c + " no existe (o el árbol está vacío).");
                refrescar();
            });
            btnVac.addActionListener(e -> {
                ops.vaciar();
                txtResultado.setText("Árbol vaciado.");
                refrescar();
            });
            btnSec.addActionListener(e -> insertarSecuencia());
            btnPre.addActionListener(e -> txtResultado.setText("Preorden:\n" + ops.preorden()));
            btnIn.addActionListener(e -> txtResultado.setText("Inorden:\n" + ops.inorden()));
            btnPost.addActionListener(e -> txtResultado.setText("Postorden:\n" + ops.postorden()));
            btnNiv.addActionListener(e -> txtResultado.setText("Por niveles:\n" + ops.porNiveles()));
            refrescar();
        }

        private String ejemplo() {
            return prefijo + "0010," + prefijo + "0020," + prefijo + "0030," + prefijo + "0040,"
                    + prefijo + "0050," + prefijo + "0025";
        }

        private String codigo() {
            String c = txtCodigo.getText().trim().toUpperCase();
            if (c.isEmpty()) {
                UI.aviso(this, "Ingresa un código.");
                return null;
            }
            return c;
        }

        private void insertarSecuencia() {
            String[] partes = txtSecuencia.getText().split(",");
            int nuevos = 0;
            int repetidos = 0;
            for (String p : partes) {
                String c = p.trim().toUpperCase();
                if (c.isEmpty()) continue;
                if (ops.insertar(c)) nuevos++;
                else repetidos++;
            }
            txtResultado.setText("Secuencia procesada: " + nuevos + " insertados, " + repetidos + " repetidos (ignorados).");
            refrescar();
        }

        private void refrescar() {
            panelArbol.setResaltado(null);
            panelArbol.setRaiz(ops.raiz());
            String info = "Nodos: " + ops.tamanio() + "   Altura: " + ops.altura();
            String extra = ops.extra();
            txtBitacora.setText(extra.isEmpty() ? info : info + "\n\n" + extra);
            txtBitacora.setCaretPosition(0);
        }
    }
}