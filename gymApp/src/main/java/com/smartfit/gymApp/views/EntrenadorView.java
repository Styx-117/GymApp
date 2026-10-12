package com.smartfit.gymApp.views;

import com.smartfit.gymApp.model.Entrenador;
import com.smartfit.gymApp.repository.EntrenadorDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EntrenadorView extends JPanel {

    private JTable tablaEntrenadores;
    private DefaultTableModel modeloTabla;
    private JTextField txtNombre, txtApellido, txtEspecialidad, txtTelefono;
    private EntrenadorDAO entrenadorDAO;

    public EntrenadorView() {
        entrenadorDAO = new EntrenadorDAO();

        // Configuramos el layout directamente en el panel principal
        setLayout(new BorderLayout());
        setBackground(new Color(25, 25, 35));

        // 1. Título Superior (NORTH)
        JLabel lblTitulo = new JLabel("    Gestión de Entrenadores", JLabel.LEFT);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(lblTitulo, BorderLayout.NORTH);

        // 2. Tabla Central (CENTER)
        String[] columnas = {"ID", "Nombre", "Apellido", "Especialidad", "Teléfono"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaEntrenadores = new JTable(modeloTabla);
        tablaEntrenadores.setBackground(new Color(45, 45, 60));
        tablaEntrenadores.setForeground(Color.WHITE);
        tablaEntrenadores.setSelectionBackground(new Color(255, 204, 0));
        tablaEntrenadores.setSelectionForeground(Color.BLACK);
        tablaEntrenadores.setRowHeight(25);
        
        // Estilo de la cabecera de la tabla
        javax.swing.table.JTableHeader header = tablaEntrenadores.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(new Color(40, 40, 50));
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setOpaque(true);
                return label;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaEntrenadores);
        scrollPane.getViewport().setBackground(new Color(30, 30, 42));
        add(scrollPane, BorderLayout.CENTER);

        // 3. Panel Inferior (SOUTH) - Contiene el formulario y los botones
        JPanel panelInferior = new JPanel(new BorderLayout(10, 5));
        panelInferior.setBackground(new Color(30, 30, 42));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        panelInferior.setPreferredSize(new Dimension(0, 115)); // Altura fija para garantizar visibilidad

        // Formulario: 2 filas y 4 columnas (Fila 1: Etiquetas, Fila 2: Campos de texto)
        JPanel panelFormulario = new JPanel(new GridLayout(2, 4, 15, 2));
        panelFormulario.setOpaque(false);

        txtNombre = new JTextField();
        txtApellido = new JTextField();
        txtEspecialidad = new JTextField();
        txtTelefono = new JTextField();

        // Fila 1: Etiquetas
        panelFormulario.add(crearLabelForm("Nombre:"));
        panelFormulario.add(crearLabelForm("Apellido:"));
        panelFormulario.add(crearLabelForm("Especialidad:"));
        panelFormulario.add(crearLabelForm("Teléfono:"));

        // Fila 2: Cuadros de texto blancos
        panelFormulario.add(txtNombre);
        panelFormulario.add(txtApellido);
        panelFormulario.add(txtEspecialidad);
        panelFormulario.add(txtTelefono);

        panelInferior.add(panelFormulario, BorderLayout.CENTER);

        // Panel de Botones (Derecha)
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);

        JButton btnGuardar = new JButton("Registrar Entrenador");
        btnGuardar.setBackground(new Color(255, 204, 0));
        btnGuardar.setForeground(Color.BLACK);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setOpaque(true);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setPreferredSize(new Dimension(170, 30));

        JButton btnEliminar = new JButton("Eliminar Entrenador");
        btnEliminar.setBackground(new Color(220, 50, 50));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEliminar.setFocusPainted(false);
        btnEliminar.setOpaque(true);
        btnEliminar.setBorderPainted(false);
        btnEliminar.setPreferredSize(new Dimension(170, 30));

        JButton btnActualizar = new JButton("Guardar cambios");
        btnActualizar.setBackground(new Color(70, 130, 180));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnActualizar.setFocusPainted(false);
        btnActualizar.setOpaque(true);
        btnActualizar.setBorderPainted(false);
        btnActualizar.setPreferredSize(new Dimension(150, 30));

        panelBotones.add(btnGuardar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);

        panelInferior.add(panelBotones, BorderLayout.SOUTH);
        
        // Añadimos el panel inferior completo al SOUTH de la vista principal
        add(panelInferior, BorderLayout.SOUTH);

        // Cargar datos al iniciar
        cargarDatosTabla();

        tablaEntrenadores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaEntrenadores.getSelectedRow() >= 0) {
                int fila = tablaEntrenadores.convertRowIndexToModel(tablaEntrenadores.getSelectedRow());
                txtNombre.setText(String.valueOf(modeloTabla.getValueAt(fila, 1)));
                txtApellido.setText(String.valueOf(modeloTabla.getValueAt(fila, 2)));
                txtEspecialidad.setText(String.valueOf(modeloTabla.getValueAt(fila, 3)));
                txtTelefono.setText(String.valueOf(modeloTabla.getValueAt(fila, 4)));
            }
        });

        // Evento del Botón Registrar
        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String especialidad = txtEspecialidad.getText().trim();
            String telefono = txtTelefono.getText().trim();

            if (nombre.isEmpty() || apellido.isEmpty() || especialidad.isEmpty() || telefono.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                boolean exito = entrenadorDAO.registrarEntrenador(nombre, apellido, especialidad, telefono);
                if (exito) {
                    JOptionPane.showMessageDialog(this, "¡Entrenador registrado exitosamente!");
                    limpiarCampos();
                    cargarDatosTabla();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo registrar el entrenador.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al registrar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnActualizar.addActionListener(e -> {
            int fila = tablaEntrenadores.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un entrenador para actualizar.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (txtNombre.getText().trim().isEmpty() || txtApellido.getText().trim().isEmpty()
                    || txtEspecialidad.getText().trim().isEmpty() || txtTelefono.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete todos los campos.", "Campos vacíos",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            int filaModelo = tablaEntrenadores.convertRowIndexToModel(fila);
            int id = Integer.parseInt(String.valueOf(modeloTabla.getValueAt(filaModelo, 0)));
            boolean actualizado = entrenadorDAO.actualizarEntrenador(id, txtNombre.getText().trim(),
                    txtApellido.getText().trim(), txtEspecialidad.getText().trim(), txtTelefono.getText().trim());
            if (actualizado) {
                JOptionPane.showMessageDialog(this, "Entrenador actualizado correctamente.");
                cargarDatosTabla();
                limpiarCampos();
                tablaEntrenadores.clearSelection();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar el entrenador.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Evento del Botón Eliminar
        btnEliminar.addActionListener(e -> {
            int filaSeleccionada = tablaEntrenadores.getSelectedRow();
            if (filaSeleccionada >= 0) {
                int confirmacion = JOptionPane.showConfirmDialog(
                    this, 
                    "¿Estás seguro de eliminar el entrenador seleccionado?", 
                    "Confirmar eliminación", 
                    JOptionPane.YES_NO_OPTION
                );
                
                if (confirmacion == JOptionPane.YES_OPTION) {
                    int idEntrenador = Integer.parseInt(tablaEntrenadores.getValueAt(filaSeleccionada, 0).toString());
                    boolean eliminado = entrenadorDAO.eliminarEntrenador(idEntrenador);
                    
                    if (eliminado) {
                        JOptionPane.showMessageDialog(this, "Entrenador eliminado correctamente.");
                        cargarDatosTabla();
                    } else {
                        JOptionPane.showMessageDialog(this, "No se pudo eliminar el entrenador.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(
                    this, 
                    "Por favor selecciona un entrenador de la tabla para eliminar.", 
                    "Aviso", 
                    JOptionPane.WARNING_MESSAGE
                );
            }
        });
    }

    private JLabel crearLabelForm(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return lbl;
    }

    private void cargarDatosTabla() {
        modeloTabla.setRowCount(0);
        List<Entrenador> entrenadores = entrenadorDAO.obtenerEntrenadores();
        for (Entrenador e : entrenadores) {
            Object[] fila = {
                e.getIdEntrenador(),
                e.getNombre(),
                e.getApellido(),
                e.getEspecialidad(),
                e.getTelefono()
            };
            modeloTabla.addRow(fila);
        }
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtApellido.setText("");
        txtEspecialidad.setText("");
        txtTelefono.setText("");
    }
}