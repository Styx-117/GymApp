package com.smartfit.gymApp.views;

import com.smartfit.gymApp.repository.SocioDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SocioView extends JFrame {

    private JTable tablaSocios;
    private DefaultTableModel modeloTabla;
    private JTextField txtNombre, txtApellido, txtDni, txtTelefono, txtCorreo;
    private SocioDAO socioDAO;

    public SocioView() {
        socioDAO = new SocioDAO();

        setTitle("SmartFit - Gestión de Socios");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel Principal Oscuro
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(25, 25, 35));

        // Título Superior
        JLabel lblTitulo = new JLabel("  Gestión de Socios y Clientes", JLabel.LEFT);
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

        // Panel Inferior para Registro Rápido
        JPanel panelFormulario = new JPanel(new GridLayout(2, 6, 10, 10));
        panelFormulario.setBackground(new Color(30, 30, 42));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        txtNombre = new JTextField();
        txtApellido = new JTextField();
        txtDni = new JTextField();
        txtTelefono = new JTextField();
        txtCorreo = new JTextField();
        JButton btnGuardar = new JButton("Registrar Socio");
        btnGuardar.setBackground(new Color(255, 204, 0));
        btnGuardar.setForeground(Color.BLACK);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 12));

        panelFormulario.add(new JLabel("<html><font color='white'>Nombre:</font></html>"));
        panelFormulario.add(new JLabel("<html><font color='white'>Apellido:</font></html>"));
        panelFormulario.add(new JLabel("<html><font color='white'>DNI:</font></html>"));
        panelFormulario.add(new JLabel("<html><font color='white'>Teléfono:</font></html>"));
        panelFormulario.add(new JLabel("<html><font color='white'>Correo:</font></html>"));
        panelFormulario.add(new JLabel("")); // Espacio vacío

        panelFormulario.add(txtNombre);
        panelFormulario.add(txtApellido);
        panelFormulario.add(txtDni);
        panelFormulario.add(txtTelefono);
        panelFormulario.add(txtCorreo);
        panelFormulario.add(btnGuardar);

        mainPanel.add(panelFormulario, BorderLayout.SOUTH);
        add(mainPanel);

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
    }

    private void cargarDatosTabla() {
        modeloTabla.setRowCount(0); // Limpiar tabla
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