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

       
        String[] columnas = {"ID", "Nombre", "Entrenador", "Día", "Inicio", "Fin", "Cupo Máx", "Cupo Actual"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        tablaClases = new JTable(modeloTabla);
        tablaClases.setRowHeight(25);
        
     
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
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnEliminar);

        panelInferior.add(panelBotones, BorderLayout.SOUTH);
        add(panelInferior, BorderLayout.SOUTH);

        // Cargar datos iniciales
        cargarDatosClases();

        // Acciones de los botones
        btnRegistrar.addActionListener(e -> registrarClase());
        btnEliminar.addActionListener(e -> eliminarClase());
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

    private void limpiarCampos() {
        txtNombre.setText("");
        txtEntrenadorId.setText("");
        txtDia.setText("");
        txtHoraInicio.setText("");
        txtHoraFin.setText("");
        txtCupoMax.setText("");
    }
}