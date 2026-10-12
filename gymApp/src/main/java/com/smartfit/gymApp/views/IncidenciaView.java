package com.smartfit.gymApp.views;

import com.smartfit.gymApp.model.Incidencia;
import com.smartfit.gymApp.service.IncidenciaService;
import com.smartfit.gymApp.utils.UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;


public class IncidenciaView extends JPanel {

    private final IncidenciaService servicio = IncidenciaService.getInstance();
    private final JTextField txtEquipo = UI.campo(8);
    private final JTextField txtDescripcion = UI.campo(22);
    private final JComboBox<String> cmbPrioridad = new JComboBox<>(new String[]{"1 - Alta", "2 - Media", "3 - Baja"});
    private final DefaultTableModel modeloPendientes = new DefaultTableModel(
            new Object[]{"ID", "Equipo", "Descripción", "Prioridad"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tablaPendientes = new JTable(modeloPendientes);
    private final JTextArea txtAtendidas = UI.areaSalida();

    public IncidenciaView() {
        setLayout(new BorderLayout());
        setBackground(UI.FONDO);
        add(UI.titulo("Incidencias de mantenimiento (Cola de prioridad)"), BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(1, 2, 10, 0));
        centro.setOpaque(false);
        centro.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        centro.add(UI.scroll(tablaPendientes, "Pendientes (sale primero la de mayor prioridad)"));
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
        JButton btnActualizar = UI.boton("Actualizar seleccionada");
        JButton btnEliminar = UI.boton("Eliminar seleccionada");
        f2.add(btnVer);
        f2.add(btnAtender);
        f2.add(btnActualizar);
        f2.add(btnEliminar);
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
        tablaPendientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaPendientes.getSelectedRow() >= 0) {
                int fila = tablaPendientes.convertRowIndexToModel(tablaPendientes.getSelectedRow());
                txtEquipo.setText(String.valueOf(modeloPendientes.getValueAt(fila, 1)));
                txtDescripcion.setText(String.valueOf(modeloPendientes.getValueAt(fila, 2)));
                cmbPrioridad.setSelectedIndex(Integer.parseInt(
                        String.valueOf(modeloPendientes.getValueAt(fila, 3))) - 1);
            }
        });
        btnActualizar.addActionListener(e -> {
            int fila = tablaPendientes.getSelectedRow();
            if (fila < 0) {
                UI.aviso(this, "Selecciona una incidencia pendiente para actualizar.");
                return;
            }
            int filaModelo = tablaPendientes.convertRowIndexToModel(fila);
            int id = Integer.parseInt(String.valueOf(modeloPendientes.getValueAt(filaModelo, 0)));
            if (!servicio.actualizarPendiente(id, txtEquipo.getText().trim().toUpperCase(),
                    txtDescripcion.getText().trim(), cmbPrioridad.getSelectedIndex() + 1)) {
                UI.aviso(this, "No se pudo actualizar. Completa el equipo y la descripción.");
                return;
            }
            refrescar();
            txtEquipo.setText("");
            txtDescripcion.setText("");
        });
        btnEliminar.addActionListener(e -> {
            int fila = tablaPendientes.getSelectedRow();
            if (fila < 0) {
                UI.aviso(this, "Selecciona una incidencia pendiente para eliminar.");
                return;
            }
            int filaModelo = tablaPendientes.convertRowIndexToModel(fila);
            int id = Integer.parseInt(String.valueOf(modeloPendientes.getValueAt(filaModelo, 0)));
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar la incidencia seleccionada?", "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                if (servicio.eliminarPendiente(id)) {
                    refrescar();
                    txtEquipo.setText("");
                    txtDescripcion.setText("");
                } else {
                    UI.aviso(this, "La incidencia ya no está pendiente y no se puede eliminar.");
                }
            }
        });
        refrescar();
    }

    private void refrescar() {
        modeloPendientes.setRowCount(0);
        servicio.getPendientes().recorrer(i -> modeloPendientes.addRow(new Object[]{
                i.getIdIncidencia(), i.getCodigoEquipo(), i.getDescripcion(), i.getPrioridad()}));
        StringBuilder a = new StringBuilder();
        servicio.getAtendidas().recorrer(i -> a.append(linea(i)).append("   atendida el ").append(i.getFechaAtencion()).append('\n'));
        txtAtendidas.setText(servicio.getAtendidas().estaVacia() ? "(historial vacío)" : a.toString());
    }

    private String linea(Incidencia i) {
        return "[P" + i.getPrioridad() + "] " + i.getCodigoEquipo() + " - " + i.getDescripcion();
    }
}