package com.smartfit.gymApp.views;

import com.smartfit.gymApp.repository.ClaseDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Time;
import java.util.List;

public class ClaseView extends JPanel {
    private JTable tablaClases;
    private DefaultTableModel modeloTabla;
    private JTextField txtNombre, txtEntrenadorId, txtDia, txtHoraInicio, txtHoraFin, txtCupoMax;
    private ClaseDAO claseDAO;

    public ClaseView() {
        claseDAO = new ClaseDAO();
        setLayout(new BorderLayout());
        setBackground(new Color(24, 24, 30)); 

        // Título del módulo
        JLabel lblTitulo = new JLabel("Gestión de Clases", JLabel.LEFT);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));
        add(lblTitulo, BorderLayout.NORTH);

       
        String[] columnas = {"ID", "Nombre", "Entrenador", "Día", "Inicio", "Fin", "Cupo Máx", "Cupo Actual",
                "ID entrenador"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        tablaClases = new JTable(modeloTabla);
        tablaClases.setRowHeight(25);
        tablaClases.removeColumn(tablaClases.getColumnModel().getColumn(8));
        
     
        JScrollPane scrollPane = new JScrollPane(tablaClases);
        scrollPane.getViewport().setBackground(new Color(30, 30, 40));
        
        JPanel panelTablaContenedor = new JPanel(new BorderLayout());
        panelTablaContenedor.setBackground(new Color(24, 24, 30));
        panelTablaContenedor.setBorder(BorderFactory.createEmptyBorder(0, 20, 15, 20));
        panelTablaContenedor.add(scrollPane, BorderLayout.CENTER);
        add(panelTablaContenedor, BorderLayout.CENTER);

       
        JPanel panelInferior = new JPanel(new BorderLayout(0, 15));
        panelInferior.setBackground(new Color(24, 24, 30));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

       
        JPanel panelCampos = new JPanel(new GridLayout(3, 4, 15, 10));
        panelCampos.setBackground(new Color(24, 24, 30));

        txtNombre = new JTextField();
        txtEntrenadorId = new JTextField();
        txtDia = new JTextField();
        txtHoraInicio = new JTextField();
        txtHoraFin = new JTextField();
        txtCupoMax = new JTextField();

        panelCampos.add(crearLabelCampo("Nombre Clase:"));
        panelCampos.add(txtNombre);
        panelCampos.add(crearLabelCampo("ID Entrenador:"));
        panelCampos.add(txtEntrenadorId);

        panelCampos.add(crearLabelCampo("Día (ej. Lunes):"));
        panelCampos.add(txtDia);
        panelCampos.add(crearLabelCampo("Hora Inicio (HH:MM):"));
        panelCampos.add(txtHoraInicio);

        panelCampos.add(crearLabelCampo("Hora Fin (HH:MM):"));
        panelCampos.add(txtHoraFin);
        panelCampos.add(crearLabelCampo("Cupo Máximo:"));
        panelCampos.add(txtCupoMax);

        panelInferior.add(panelCampos, BorderLayout.CENTER);

        // Botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelBotones.setBackground(new Color(24, 24, 30));
JButton btnRegistrar = new JButton("Registrar Clase");
        btnRegistrar.setBackground(new Color(255, 193, 7));
        btnRegistrar.setForeground(Color.BLACK);
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setOpaque(true);
        btnRegistrar.setBorderPainted(false); 
        btnRegistrar.setPreferredSize(new Dimension(140, 35)); 
        btnRegistrar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnEliminar = new JButton("Eliminar Clase");
        btnEliminar.setBackground(new Color(220, 53, 69));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEliminar.setFocusPainted(false);
        btnEliminar.setOpaque(true);
        btnEliminar.setBorderPainted(false); 
        btnEliminar.setPreferredSize(new Dimension(140, 35)); 
        btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JButton btnActualizar = new JButton("Guardar cambios");
        btnActualizar.setBackground(new Color(70, 130, 180));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFocusPainted(false);
        btnActualizar.setPreferredSize(new Dimension(150, 35));
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);

        panelInferior.add(panelBotones, BorderLayout.SOUTH);
        add(panelInferior, BorderLayout.SOUTH);

        // Cargar datos iniciales
        cargarDatosClases();

        tablaClases.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaClases.getSelectedRow() >= 0) {
                int fila = tablaClases.convertRowIndexToModel(tablaClases.getSelectedRow());
                txtNombre.setText(String.valueOf(modeloTabla.getValueAt(fila, 1)));
                txtEntrenadorId.setText(String.valueOf(modeloTabla.getValueAt(fila, 8)));
                txtDia.setText(String.valueOf(modeloTabla.getValueAt(fila, 3)));
                txtHoraInicio.setText(String.valueOf(modeloTabla.getValueAt(fila, 4)).substring(0, 5));
                txtHoraFin.setText(String.valueOf(modeloTabla.getValueAt(fila, 5)).substring(0, 5));
                txtCupoMax.setText(String.valueOf(modeloTabla.getValueAt(fila, 6)));
            }
        });

        // Acciones de los botones
        btnRegistrar.addActionListener(e -> registrarClase());
        btnEliminar.addActionListener(e -> eliminarClase());
        btnActualizar.addActionListener(e -> actualizarClase());
    }

    private JLabel crearLabelCampo(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return label;
    }

    private void cargarDatosClases() {
        modeloTabla.setRowCount(0);
        List<Object[]> lista = claseDAO.listarClases();
        for (Object[] fila : lista) {
            modeloTabla.addRow(fila);
        }
    }

    private void registrarClase() {
        try {
            String nombre = txtNombre.getText().trim();
            int idEntrenador = Integer.parseInt(txtEntrenadorId.getText().trim());
            String dia = txtDia.getText().trim();
            Time horaInicio = Time.valueOf(txtHoraInicio.getText().trim() + ":00");
            Time horaFin = Time.valueOf(txtHoraFin.getText().trim() + ":00");
            int cupoMax = Integer.parseInt(txtCupoMax.getText().trim());

            boolean exito = claseDAO.registrarClase(nombre, idEntrenador, dia, horaInicio, horaFin, cupoMax);
            if (exito) {
                JOptionPane.showMessageDialog(this, "Clase registrada exitosamente.");
                cargarDatosClases();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar la clase.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Por favor verifique los datos ingresados (Formatos de hora HH:MM).", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void eliminarClase() {
        int filaSeleccionada = tablaClases.getSelectedRow();
        if (filaSeleccionada >= 0) {
            int idClase = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
            int confirmacion = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar esta clase?", "Confirmar", JOptionPane.YES_NO_OPTION);
            
            if (confirmacion == JOptionPane.YES_OPTION) {
                if (claseDAO.eliminarClase(idClase)) {
                    JOptionPane.showMessageDialog(this, "Clase eliminada.");
                    cargarDatosClases();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo eliminar la clase.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una clase de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void actualizarClase() {
        int fila = tablaClases.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una clase para actualizar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int filaModelo = tablaClases.convertRowIndexToModel(fila);
            int id = Integer.parseInt(String.valueOf(modeloTabla.getValueAt(filaModelo, 0)));
            String nombre = txtNombre.getText().trim();
            int entrenadorId = Integer.parseInt(txtEntrenadorId.getText().trim());
            String dia = txtDia.getText().trim();
            Time inicio = Time.valueOf(txtHoraInicio.getText().trim() + ":00");
            Time fin = Time.valueOf(txtHoraFin.getText().trim() + ":00");
            int cupo = Integer.parseInt(txtCupoMax.getText().trim());
            if (nombre.isEmpty() || dia.isEmpty() || cupo <= 0 || !inicio.before(fin)) {
                throw new IllegalArgumentException();
            }

            if (claseDAO.actualizarClase(id, nombre, entrenadorId, dia, inicio, fin, cupo)) {
                JOptionPane.showMessageDialog(this, "Clase actualizada correctamente.");
                cargarDatosClases();
                limpiarCampos();
                tablaClases.clearSelection();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo actualizar. Verifique el entrenador y que el nuevo cupo no sea menor al cupo actual.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Verifique el nombre, el ID del entrenador, el día, las horas (HH:MM) y el cupo.",
                    "Datos inválidos", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtEntrenadorId.setText("");
        txtDia.setText("");
        txtHoraInicio.setText("");
        txtHoraFin.setText("");
        txtCupoMax.setText("");
    }
}