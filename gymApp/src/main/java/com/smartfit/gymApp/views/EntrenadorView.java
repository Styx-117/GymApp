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
        setLayout(new BorderLayout());
        setBackground(new Color(24, 24, 30));

        // Título superior
        JLabel lblTitulo = new JLabel("Gestión de Entrenadores");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        add(lblTitulo, BorderLayout.NORTH);

        // Tabla con las 5 columnas exactas (ID, Nombre, Apellido, Especialidad, Teléfono)
        String[] columnas = {"ID", "Nombre", "Apellido", "Especialidad", "Teléfono"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaEntrenadores = new JTable(modeloTabla);
        tablaEntrenadores.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(tablaEntrenadores);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        scrollPane.getViewport().setBackground(new Color(24, 24, 30));
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior para el formulario de registro (4 campos reales)
        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setBackground(new Color(24, 24, 30));
        panelSur.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));

        JPanel panelFormulario = new JPanel(new GridLayout(2, 4, 10, 5));
        panelFormulario.setBackground(new Color(24, 24, 30));

        txtNombre = new JTextField();
        txtApellido = new JTextField();
        txtEspecialidad = new JTextField();
        txtTelefono = new JTextField();
        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBackground(new Color(255, 204, 0));
        btnRegistrar.setForeground(Color.BLACK);
        btnRegistrar.setFocusPainted(false);

        panelFormulario.add(crearLabelForm("Nombre:"));
        panelFormulario.add(crearLabelForm("Apellido:"));
        panelFormulario.add(crearLabelForm("Especialidad:"));
        panelFormulario.add(crearLabelForm("Teléfono:"));

        JPanel panelInputs = new JPanel(new GridLayout(1, 4, 10, 0));
        panelInputs.setBackground(new Color(24, 24, 30));
        panelInputs.add(txtNombre);
        panelInputs.add(txtApellido);
        panelInputs.add(txtEspecialidad);
        panelInputs.add(txtTelefono);

        panelSur.add(panelFormulario, BorderLayout.NORTH);
        panelSur.add(panelInputs, BorderLayout.CENTER);
        
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBoton.setBackground(new Color(24, 24, 30));
        panelBoton.add(btnRegistrar);
        panelSur.add(panelBoton, BorderLayout.SOUTH);

        add(panelSur, BorderLayout.SOUTH);

        // Evento del botón registrar
        btnRegistrar.addActionListener(e -> registrarEntrenador());

        // Cargar datos de PostgreSQL al iniciar
        cargarDatosTabla();
    }

    private JLabel crearLabelForm(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
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

    private void registrarEntrenador() {
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String especialidad = txtEspecialidad.getText().trim();
        String telefono = txtTelefono.getText().trim();

        if (nombre.isEmpty() || apellido.isEmpty() || especialidad.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean exito = entrenadorDAO.registrarEntrenador(nombre, apellido, especialidad, telefono);
            
            if (exito) {
                JOptionPane.showMessageDialog(this, "¡Entrenador registrado con éxito!");
                limpiarCampos();
                cargarDatosTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar el entrenador.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtApellido.setText("");
        txtEspecialidad.setText("");
        txtTelefono.setText("");
    }
}