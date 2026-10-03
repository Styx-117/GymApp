package com.smartfit.gymApp.views;

import com.smartfit.gymApp.repository.SocioDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SocioView extends JPanel {

    private JTable tablaSocios;
    private DefaultTableModel modeloTabla;
    private JTextField txtNombre, txtApellido, txtDni, txtTelefono, txtCorreo;
    private SocioDAO socioDAO;

    public SocioView() {
        socioDAO = new SocioDAO();

        setLayout(new BorderLayout());

        // Panel Principal Oscuro
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(25, 25, 35));

        // Título Superior
        JLabel lblTitulo = new JLabel("   Gestión de Socios y Clientes", JLabel.LEFT);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.add(lblTitulo, BorderLayout.NORTH);

        // Tabla central para listar socios
        String[] columnas = {"ID", "Nombre", "Apellido", "DNI", "Teléfono", "Correo"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaSocios = new JTable(modeloTabla);
        tablaSocios.setBackground(new Color(45, 45, 60));
        tablaSocios.setForeground(Color.WHITE);
        tablaSocios.setSelectionBackground(new Color(255, 204, 0));
        tablaSocios.setSelectionForeground(Color.BLACK);
        tablaSocios.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(tablaSocios);
        scrollPane.getViewport().setBackground(new Color(30, 30, 42));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

       
        // PANEL INFERIOR (Formulario arriba, Botones abajo)
    
        JPanel panelInferior = new JPanel(new BorderLayout(10, 10));
        panelInferior.setBackground(new Color(30, 30, 42));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. Panel de Formulario (Inputs en la parte superior del panel inferior)
        JPanel panelFormulario = new JPanel(new GridLayout(2, 5, 10, 5));
        panelFormulario.setOpaque(false);

        txtNombre = new JTextField();
        txtApellido = new JTextField();
        txtDni = new JTextField();
        txtTelefono = new JTextField();
        txtCorreo = new JTextField();

        // Fila 1: Etiquetas
        panelFormulario.add(crearLabelForm("Nombre:"));
        panelFormulario.add(crearLabelForm("Apellido:"));
        panelFormulario.add(crearLabelForm("DNI:"));
        panelFormulario.add(crearLabelForm("Teléfono:"));
        panelFormulario.add(crearLabelForm("Correo:"));

        // Fila 2: Campos de texto
        panelFormulario.add(txtNombre);
        panelFormulario.add(txtApellido);
        panelFormulario.add(txtDni);
        panelFormulario.add(txtTelefono);
        panelFormulario.add(txtCorreo);

        panelInferior.add(panelFormulario, BorderLayout.CENTER);

        // 2. Panel de Botones (Colocados abajo, alineados a la derecha de forma segura)
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);

        JButton btnGuardar = new JButton("Registrar Socio");
        btnGuardar.setBackground(new Color(255, 204, 0));
        btnGuardar.setForeground(Color.BLACK);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setOpaque(true);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setPreferredSize(new Dimension(140, 35));

        JButton btnEliminar = new JButton("Eliminar Socio");
        btnEliminar.setBackground(new Color(220, 50, 50));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEliminar.setFocusPainted(false);
        btnEliminar.setOpaque(true);
        btnEliminar.setBorderPainted(false);
        btnEliminar.setPreferredSize(new Dimension(140, 35));

        panelBotones.add(btnGuardar);
        panelBotones.add(btnEliminar);

        panelInferior.add(panelBotones, BorderLayout.SOUTH);

        mainPanel.add(panelInferior, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);

        // Cargar datos al iniciar
        cargarDatosTabla();

        // Evento del Botón Registrar
        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String dni = txtDni.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String correo = txtCorreo.getText().trim();

            if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete al menos Nombre, Apellido y DNI.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean exito = socioDAO.registrarCliente(nombre, apellido, dni, telefono, correo);
            if (exito) {
                JOptionPane.showMessageDialog(this, "¡Socio registrado exitosamente!");
                limpiarCampos();
                cargarDatosTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar socio (verifique si el DNI ya existe).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Evento del Botón Eliminar
        btnEliminar.addActionListener(e -> {
            int filaSeleccionada = tablaSocios.getSelectedRow();
            if (filaSeleccionada >= 0) {
                int confirmacion = JOptionPane.showConfirmDialog(
                    this, 
                    "¿Estás seguro de eliminar el socio seleccionado?", 
                    "Confirmar eliminación", 
                    JOptionPane.YES_NO_OPTION
                );
                
                if (confirmacion == JOptionPane.YES_OPTION) {
                    int idSocio = Integer.parseInt(tablaSocios.getValueAt(filaSeleccionada, 0).toString());
                    boolean eliminado = socioDAO.eliminarCliente(idSocio);
                    
                    if (eliminado) {
                        JOptionPane.showMessageDialog(this, "Socio eliminado correctamente.");
                        cargarDatosTabla();
                    } else {
                        JOptionPane.showMessageDialog(this, "No se pudo eliminar el socio.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(
                    this, 
                    "Por favor selecciona un socio de la tabla para eliminar.", 
                    "Aviso", 
                    JOptionPane.WARNING_MESSAGE
                );
            }
        });
    }

    private JLabel crearLabelForm(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return lbl;
    }

    private void cargarDatosTabla() {
        modeloTabla.setRowCount(0);
        List<String[]> socios = socioDAO.obtenerClientes();
        for (String[] socio : socios) {
            modeloTabla.addRow(socio);
        }
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtApellido.setText("");
        txtDni.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
    }
}