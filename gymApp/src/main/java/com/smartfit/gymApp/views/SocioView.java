package com.smartfit.gymApp.views;

import com.smartfit.gymApp.repository.SocioDAO;
import com.smartfit.gymApp.dto.ReniecData;
import com.smartfit.gymApp.service.ReniecApiClient;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SocioView extends JPanel {

    private final JTable tablaSocios;
    private final DefaultTableModel modeloTabla;
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtApellido = new JTextField();
    private final JTextField txtDni = new JTextField();
    private final JTextField txtTelefono = new JTextField();
    private final JTextField txtCorreo = new JTextField();
    private final SocioDAO socioDAO = new SocioDAO();
    private final ReniecApiClient reniecApiClient = new ReniecApiClient();

    public SocioView() {
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
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
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

        txtDni.setDocument(new javax.swing.text.PlainDocument() {
            @Override
            public void insertString(int offset, String text, javax.swing.text.AttributeSet attributes)
                    throws javax.swing.text.BadLocationException {
                if (text != null && text.matches("\\d*") && getLength() + text.length() <= 8) {
                    super.insertString(offset, text, attributes);
                }
            }
        });

        // Fila 1: Etiquetas
        panelFormulario.add(crearLabelForm("Nombre:"));
        panelFormulario.add(crearLabelForm("Apellido:"));
        panelFormulario.add(crearLabelForm("DNI:"));
        panelFormulario.add(crearLabelForm("Teléfono:"));
        panelFormulario.add(crearLabelForm("Correo:"));

        // Fila 2: Campos de texto
        panelFormulario.add(txtNombre);
        panelFormulario.add(txtApellido);
        JPanel panelDni = new JPanel(new BorderLayout(5, 0));
        panelDni.setOpaque(false);
        panelDni.add(txtDni, BorderLayout.CENTER);
        JButton btnConsultarReniec = new JButton("Consultar");
        btnConsultarReniec.setToolTipText("Completar nombres y apellidos a partir del DNI");
        panelDni.add(btnConsultarReniec, BorderLayout.EAST);
        panelFormulario.add(panelDni);
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

        JButton btnActualizar = new JButton("Guardar cambios");
        btnActualizar.setBackground(new Color(70, 130, 180));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnActualizar.setFocusPainted(false);
        btnActualizar.setOpaque(true);
        btnActualizar.setBorderPainted(false);
        btnActualizar.setPreferredSize(new Dimension(140, 35));

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setPreferredSize(new Dimension(90, 35));

        panelBotones.add(btnGuardar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        panelInferior.add(panelBotones, BorderLayout.SOUTH);

        mainPanel.add(panelInferior, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);

        // Cargar datos al iniciar
        cargarDatosTabla();

        btnConsultarReniec.addActionListener(e -> consultarReniec(btnConsultarReniec));
        btnLimpiar.addActionListener(e -> {
            tablaSocios.clearSelection();
            limpiarCampos();
        });
        tablaSocios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaSocios.getSelectedRow() >= 0) {
                int fila = tablaSocios.convertRowIndexToModel(tablaSocios.getSelectedRow());
                txtNombre.setText(String.valueOf(modeloTabla.getValueAt(fila, 1)));
                txtApellido.setText(String.valueOf(modeloTabla.getValueAt(fila, 2)));
                txtDni.setText(String.valueOf(modeloTabla.getValueAt(fila, 3)));
                txtTelefono.setText(String.valueOf(modeloTabla.getValueAt(fila, 4)));
                txtCorreo.setText(String.valueOf(modeloTabla.getValueAt(fila, 5)));
            }
        });

        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String dni = txtDni.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String correo = txtCorreo.getText().trim();

            if (nombre.isEmpty() || apellido.isEmpty() || !dni.matches("\\d{8}")) {
                JOptionPane.showMessageDialog(this, "Ingrese nombre, apellido y un DNI válido de 8 dígitos.",
                        "Datos inválidos", JOptionPane.WARNING_MESSAGE);
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

        btnActualizar.addActionListener(e -> {
            int fila = tablaSocios.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un socio para actualizar.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int idSocio = Integer.parseInt(String.valueOf(
                    modeloTabla.getValueAt(tablaSocios.convertRowIndexToModel(fila), 0)));
            String dni = txtDni.getText().trim();
            if (txtNombre.getText().trim().isEmpty() || txtApellido.getText().trim().isEmpty()
                    || !dni.matches("\\d{8}")) {
                JOptionPane.showMessageDialog(this, "Ingrese nombre, apellido y un DNI válido de 8 dígitos.",
                        "Datos inválidos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean actualizado = socioDAO.actualizarCliente(idSocio, txtNombre.getText().trim(),
                    txtApellido.getText().trim(), dni, txtTelefono.getText().trim(), txtCorreo.getText().trim());
            if (actualizado) {
                JOptionPane.showMessageDialog(this, "Socio actualizado correctamente.");
                cargarDatosTabla();
                limpiarCampos();
                tablaSocios.clearSelection();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar. Verifique que el DNI no esté duplicado.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

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

    private void consultarReniec(JButton boton) {
        String dni = txtDni.getText().trim();
        if (!dni.matches("\\d{8}")) {
            JOptionPane.showMessageDialog(this, "Ingrese primero un DNI válido de 8 dígitos.",
                    "DNI inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boton.setEnabled(false);
        boton.setText("Consultando...");
        new SwingWorker<ReniecData, Void>() {
            @Override
            protected ReniecData doInBackground() throws Exception {
                return reniecApiClient.consultarDni(dni);
            }

            @Override
            protected void done() {
                boton.setEnabled(true);
                boton.setText("Consultar");
                try {
                    ReniecData data = get();
                    txtNombre.setText(data.getNombres());
                    String apellidos = String.join(" ",
                            java.util.stream.Stream.of(data.getApellidoPaterno(), data.getApellidoMaterno())
                                    .filter(apellido -> apellido != null && !apellido.isBlank())
                                    .toArray(String[]::new));
                    txtApellido.setText(apellidos);
                } catch (Exception ex) {
                    Throwable causa = ex.getCause() == null ? ex : ex.getCause();
                    JOptionPane.showMessageDialog(SocioView.this,
                            "No se pudieron consultar los datos del DNI. " + causa.getMessage(),
                            "Error de consulta", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
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