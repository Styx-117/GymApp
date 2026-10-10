package com.smartfit.gymApp.views;

import com.smartfit.gymApp.model.Incidencia;
import com.smartfit.gymApp.service.IncidenciaService;
import com.smartfit.gymApp.utils.UI;

import javax.swing.*;
import java.awt.*;


public class IncidenciaView extends JPanel {

    private final IncidenciaService servicio = IncidenciaService.getInstance();
    private final JTextField txtEquipo = UI.campo(8);
    private final JTextField txtDescripcion = UI.campo(22);
    private final JComboBox<String> cmbPrioridad = new JComboBox<>(new String[]{"1 - Alta", "2 - Media", "3 - Baja"});
    private final JTextArea txtPendientes = UI.areaSalida();
    private final JTextArea txtAtendidas = UI.areaSalida();

    public IncidenciaView() {
        setLayout(new BorderLayout());
        setBackground(UI.FONDO);
        add(UI.titulo("Incidencias de mantenimiento (Cola de prioridad)"), BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(1, 2, 10, 0));
        centro.setOpaque(false);
        centro.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        centro.add(UI.scroll(txtPendientes, "Pendientes (sale primero la de mayor prioridad)"));
        centro.add(UI.scroll(txtAtendidas, "Historial de atendidas (cola FIFO)"));
        add(centro, BorderLayout.CENTER);

        JPanel sur = new JPanel(new GridLayout(2, 1));
        sur.setBackground(UI.PANEL);
        JPanel f1 = UI.fila();
        f1.add(UI.etiqueta("Código equipo:"));
        f1.add(txtEquipo);
        f1.add(UI.etiqueta("Descripción:"));
        f1.add(txtDescripcion);
        f1.add(UI.etiqueta("Prioridad:"));
        f1.add(cmbPrioridad);
        JButton btnRegistrar = UI.boton("Registrar incidencia");
        f1.add(btnRegistrar);
        JPanel f2 = UI.fila();
        JButton btnVer = UI.boton("Ver siguiente");
        JButton btnAtender = UI.boton("Atender siguiente");
        f2.add(btnVer);
        f2.add(btnAtender);
        sur.add(f1);
        sur.add(f2);
        add(sur, BorderLayout.SOUTH);

        btnRegistrar.addActionListener(e -> {
            String eq = txtEquipo.getText().trim().toUpperCase();
            String desc = txtDescripcion.getText().trim();
            if (eq.isEmpty() || desc.isEmpty()) {
                UI.aviso(this, "Ingresa el código del equipo y la descripción.");
                return;
            }
            servicio.registrar(eq, desc, cmbPrioridad.getSelectedIndex() + 1);
            txtEquipo.setText("");
            txtDescripcion.setText("");
            refrescar();
        });
        btnVer.addActionListener(e -> {
            Incidencia i = servicio.verSiguiente();
            if (i == null) UI.aviso(this, "La cola está vacía: no hay incidencias pendientes.");
            else UI.info(this, "Siguiente a atender:\nEquipo " + i.getCodigoEquipo() + " - " + i.getDescripcion()
                    + "\nPrioridad " + i.getPrioridad());
        });
        btnAtender.addActionListener(e -> {
            Incidencia i = servicio.atenderSiguiente();
            if (i == null) UI.aviso(this, "La cola está vacía: no hay incidencias pendientes.");
            refrescar();
        });
        refrescar();
    }

    private void refrescar() {
        StringBuilder p = new StringBuilder();
        servicio.getPendientes().recorrer(i -> p.append(linea(i)).append('\n'));
        txtPendientes.setText(servicio.hayPendientes() ? p.toString() : "(sin incidencias pendientes)");
        StringBuilder a = new StringBuilder();
        servicio.getAtendidas().recorrer(i -> a.append(linea(i)).append("   atendida el ").append(i.getFechaAtencion()).append('\n'));
        txtAtendidas.setText(servicio.getAtendidas().estaVacia() ? "(historial vacío)" : a.toString());
    }

    private String linea(Incidencia i) {
        return "[P" + i.getPrioridad() + "] " + i.getCodigoEquipo() + " - " + i.getDescripcion();
    }
}