package com.smartfit.gymApp.views;

import com.smartfit.gymApp.model.Clase;
import com.smartfit.gymApp.model.ReservaClase;
import com.smartfit.gymApp.service.GestorReservasClase;
import com.smartfit.gymApp.service.ReservaService;
import com.smartfit.gymApp.utils.UI;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;


public class ReservaView extends JPanel {

    private final ReservaService servicio = ReservaService.getInstance();
    private final JComboBox<Clase> cmbClase = new JComboBox<>();
    private final JTextField txtId = UI.campo(4);
    private final JTextField txtNombre = UI.campo(10);
    private final JTextField txtCupo = UI.campo(4);
    private final JTextField txtSocio = UI.campo(10);
    private final JTextArea txtConfirmadas = UI.areaSalida();
    private final JTextArea txtEspera = UI.areaSalida();
    private final JLabel lblCupo = UI.etiqueta(" ");

    public ReservaView() {
        setLayout(new BorderLayout());
        setBackground(UI.FONDO);
        add(UI.titulo("Reservas de clases (Cola FIFO + lista de espera)"), BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(1, 2, 10, 0));
        centro.setOpaque(false);
        centro.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        centro.add(UI.scroll(txtConfirmadas, "Reservas confirmadas (cola)"));
        centro.add(UI.scroll(txtEspera, "Lista de espera (cola)"));
        add(centro, BorderLayout.CENTER);

        JPanel sur = new JPanel(new GridLayout(3, 1));
        sur.setBackground(UI.PANEL);
        JPanel f1 = UI.fila();
        f1.add(UI.etiqueta("Nueva clase  Id:"));
        f1.add(txtId);
        f1.add(UI.etiqueta("Nombre:"));
        f1.add(txtNombre);
        f1.add(UI.etiqueta("Cupo máx.:"));
        f1.add(txtCupo);
        JButton btnClase = UI.boton("Crear clase");
        f1.add(btnClase);
        JPanel f2 = UI.fila();
        f2.add(UI.etiqueta("Clase:"));
        f2.add(cmbClase);
        f2.add(UI.etiqueta("Código socio:"));
        f2.add(txtSocio);
        JButton btnReservar = UI.boton("Reservar");
        JButton btnActualizar = UI.boton("Actualizar socio");
        JButton btnCancelar = UI.boton("Cancelar reserva");
        f2.add(btnReservar);
        f2.add(btnActualizar);
        f2.add(btnCancelar);
        JPanel f3 = UI.fila();
        f3.add(lblCupo);
        sur.add(f1);
        sur.add(f2);
        sur.add(f3);
        add(sur, BorderLayout.SOUTH);

        btnClase.addActionListener(e -> crearClase());
        btnReservar.addActionListener(e -> reservar());
        btnActualizar.addActionListener(e -> actualizar());
        btnCancelar.addActionListener(e -> cancelar());
        cmbClase.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean sel, boolean foco) {
                String texto = value instanceof Clase c ? c.getIdClase() + " - " + c.getNombre() : "";
                return super.getListCellRendererComponent(list, texto, index, sel, foco);
            }
        });
        cmbClase.addActionListener(e -> refrescar());
        refrescar();
    }

    private void crearClase() {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            int cupo = Integer.parseInt(txtCupo.getText().trim());
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty() || cupo <= 0) throw new NumberFormatException();
            if (servicio.gestorDe(id) != null) {
                UI.aviso(this, "Ya existe una clase con el id " + id);
                return;
            }
            Clase c = new Clase(id, nombre, 0, "LUNES", LocalTime.of(7, 0), LocalTime.of(8, 0), cupo, 0);
            servicio.registrarClase(c);
            cmbClase.addItem(c);
            cmbClase.setSelectedItem(c);
            txtId.setText("");
            txtNombre.setText("");
            txtCupo.setText("");
        } catch (NumberFormatException ex) {
            UI.aviso(this, "Ingresa un id y un cupo numéricos (> 0) y un nombre.");
        }
    }

    private Clase claseElegida() {
        Clase c = (Clase) cmbClase.getSelectedItem();
        if (c == null) UI.aviso(this, "Primero crea o selecciona una clase.");
        return c;
    }

    private String codigoSocio() {
        String cod = txtSocio.getText().trim().toUpperCase();
        if (cod.isEmpty()) UI.aviso(this, "Ingresa el código del socio (ej. SM-0001).");
        return cod.isEmpty() ? null : cod;
    }

    private void reservar() {
        Clase c = claseElegida();
        String cod = codigoSocio();
        if (c == null || cod == null) return;
        ReservaClase r = servicio.reservar(c, cod);
        if (r == null) UI.aviso(this, "El socio " + cod + " ya está inscrito en esta clase.");
        else if (r.getEstado().equals("LISTA_ESPERA"))
            UI.info(this, "Clase llena. " + cod + " quedó en lista de espera, posición " + r.getPosicionListaEspera() + ".");
        txtSocio.setText("");
        refrescar();
    }

    private void cancelar() {
        Clase c = claseElegida();
        String cod = codigoSocio();
        if (c == null || cod == null) return;
        ReservaClase r = servicio.cancelar(c.getIdClase(), cod);
        if (r == null) {
            UI.aviso(this, "El socio " + cod + " no tiene reserva en esta clase.");
        } else {
            ReservaClase p = servicio.gestorDe(c.getIdClase()).getUltimaPromovida();
            if (p != null) UI.info(this, "Se liberó un cupo: " + p.getCodigoSocio() + " pasó de la espera a confirmado.");
        }
        txtSocio.setText("");
        refrescar();
    }

    private void actualizar() {
        Clase clase = claseElegida();
        String codigoActual = codigoSocio();
        if (clase == null || codigoActual == null) return;

        String codigoNuevo = JOptionPane.showInputDialog(this,
                "Ingrese el código de socio nuevo para esta reserva:", codigoActual);
        if (codigoNuevo == null) return;
        codigoNuevo = codigoNuevo.trim().toUpperCase();
        if (codigoNuevo.isEmpty()) {
            UI.aviso(this, "El código del socio no puede estar vacío.");
            return;
        }
        if (!servicio.actualizarCodigoSocio(clase.getIdClase(), codigoActual, codigoNuevo)) {
            UI.aviso(this, "No se encontró la reserva o el nuevo socio ya está inscrito en esta clase.");
            return;
        }
        UI.info(this, "Reserva actualizada correctamente.");
        txtSocio.setText("");
        refrescar();
    }

    private void refrescar() {
        Clase c = (Clase) cmbClase.getSelectedItem();
        if (c == null) {
            txtConfirmadas.setText("(crea una clase para empezar)");
            txtEspera.setText("");
            lblCupo.setText(" ");
            return;
        }
        GestorReservasClase g = servicio.gestorDe(c.getIdClase());
        StringBuilder sb = new StringBuilder();
        g.getConfirmadas().recorrer(r -> sb.append(r.getCodigoSocio()).append("  (reserva #").append(r.getIdReserva()).append(")\n"));
        txtConfirmadas.setText(g.getConfirmadas().estaVacia() ? "(cola vacía)" : sb.toString());
        StringBuilder se = new StringBuilder();
        g.getListaEspera().recorrer(r -> se.append("Pos ").append(r.getPosicionListaEspera()).append(": ").append(r.getCodigoSocio()).append("\n"));
        txtEspera.setText(g.getListaEspera().estaVacia() ? "(cola vacía)" : se.toString());
        lblCupo.setText("Cupo: " + c.getCupoActual() + " / " + c.getCupoMaximo()
                + (c.tieneCupoDisponible() ? "" : "   (CLASE LLENA)"));
    }
}