package com.smartfit.gymApp.views;

import javax.swing.*;
import java.awt.*;

public class MainDashboardView extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;

    public MainDashboardView(String usuarioLogueado) {
        setTitle("SmartFit - Panel Principal");
        setSize(1100, 650);
        setMinimumSize(new Dimension(850, 500)); // Evita que se deforme al achicarla demasiado
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. PANEL LATERAL

        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(250, 650));
        sidebar.setBackground(new Color(15, 15, 18));
        sidebar.setLayout(new BorderLayout());

        // Logo superior
        JPanel panelLogo = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 30));
        panelLogo.setOpaque(false);
        JLabel lblLogo = new JLabel("SMARTFIT SYSTEM");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLogo.setForeground(new Color(255, 204, 0));
        panelLogo.add(lblLogo);
        sidebar.add(panelLogo, BorderLayout.NORTH);

        JPanel panelBotonesMenu = new JPanel(new GridLayout(5, 1, 0, 10));
        panelBotonesMenu.setOpaque(false);
        panelBotonesMenu.setBorder(BorderFactory.createEmptyBorder(10, 15, 20, 15));

        String[] opciones = {
                "   🏠   Inicio",
                "   👥   Gestión de Socios",
                "   💪   Entrenadores",
                "   📋   Rutinas y Clases",
                "   🚪   Cerrar Sesión"
        };

        for (String opcion : opciones) {
            JButton btnMenu = new JButton(opcion);
            btnMenu.setFont(new Font("Dialog", Font.BOLD, 13));
            btnMenu.setForeground(Color.WHITE);
            btnMenu.setBackground(new Color(15, 15, 18));
            btnMenu.setHorizontalAlignment(SwingConstants.LEFT);
            btnMenu.setBorderPainted(false);
            btnMenu.setFocusPainted(false);
            btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnMenu.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btnMenu.setBackground(new Color(30, 30, 35));
                    btnMenu.setForeground(new Color(255, 204, 0));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btnMenu.setBackground(new Color(15, 15, 18));
                    btnMenu.setForeground(Color.WHITE);
                }
            });

            if (opcion.contains("Inicio")) {
                btnMenu.addActionListener(e -> cardLayout.show(contentPanel, "INICIO"));
            } else if (opcion.contains("Gestión de Socios")) {
                btnMenu.addActionListener(e -> cardLayout.show(contentPanel, "SOCIOS"));
            } else if (opcion.contains("Entrenadores")) {
                btnMenu.addActionListener(e -> cardLayout.show(contentPanel, "ENTRENADORES"));
            } else if (opcion.contains("Cerrar Sesión")) {
                btnMenu.addActionListener(e -> {
                    dispose();
                    new LoginView().setVisible(true);
                });
            }

            panelBotonesMenu.add(btnMenu);
        }

        sidebar.add(panelBotonesMenu, BorderLayout.CENTER);

        // 2. CONFIGURACIÓN DEL CARDLAYOUT Y PANELES

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        JPanel panelInicio = crearPanelInicio(usuarioLogueado);
        SocioView panelSocios = new SocioView();
        JPanel panelEntrenadores = crearPanelEntrenadores();

        contentPanel.add(panelInicio, "INICIO");
        contentPanel.add(panelSocios, "SOCIOS");
        contentPanel.add(panelEntrenadores, "ENTRENADORES");

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    // 3. PANEL DE INICIO

    private JPanel crearPanelInicio(String usuarioLogueado) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));

        // Títulos superiores con BoxLayout vertical fluido
        JPanel panelTop = new JPanel();
        panelTop.setLayout(new BoxLayout(panelTop, BoxLayout.Y_AXIS));
        panelTop.setOpaque(false);

        JLabel lblWelcome = new JLabel("¡Bienvenido, " + usuarioLogueado + "!");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblWelcome.setForeground(new Color(30, 30, 30));

        JLabel lblSub = new JLabel("Aquí tienes el resumen operativo del gimnasio para hoy.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(new Color(100, 100, 100));

        panelTop.add(lblWelcome);
        panelTop.add(Box.createRigidArea(new Dimension(0, 5)));
        panelTop.add(lblSub);

        panel.add(panelTop, BorderLayout.NORTH);

        JPanel panelTarjetas = new JPanel(new GridLayout(1, 3, 20, 0));
        panelTarjetas.setOpaque(false);
        panelTarjetas.setBorder(BorderFactory.createEmptyBorder(40, 0, 150, 0));

        panelTarjetas.add(crearTarjetaKPI("Socios Activos", "1,240", new Color(255, 204, 0)));
        panelTarjetas.add(crearTarjetaKPI("Entrenadores", "18", new Color(40, 40, 45)));
        panelTarjetas.add(crearTarjetaKPI("Clases Hoy", "35", new Color(40, 40, 45)));

        panel.add(panelTarjetas, BorderLayout.CENTER);

        return panel;
    }

    // Método auxiliar para crear tarjetas KPI adaptables
    private JPanel crearTarjetaKPI(String titulo, String valor, Color colorFondo) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(colorFondo);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        boolean esClaro = colorFondo.equals(new Color(255, 204, 0));
        Color textColor = esClaro ? Color.BLACK : Color.WHITE;

        JLabel lblTitleCard = new JLabel(titulo);
        lblTitleCard.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitleCard.setForeground(textColor);

        JLabel lblValueCard = new JLabel(valor);
        lblValueCard.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValueCard.setForeground(textColor);
        lblValueCard.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        card.add(lblTitleCard, BorderLayout.NORTH);
        card.add(lblValueCard, BorderLayout.CENTER);

        return card;
    }

    // 4. PANEL DE ENTRENADORES
    private JPanel crearPanelEntrenadores() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(25, 25, 35));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Título superior
        JLabel lblTitulo = new JLabel("Gestión de Entrenadores");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);

        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelNorte.setOpaque(false);
        panelNorte.add(lblTitulo);
        panel.add(panelNorte, BorderLayout.NORTH);

        // Columnas de la tabla
        String[] columnas = { "ID", "Nombre", "Apellido", "Especialidad", "Teléfono" };
        javax.swing.table.DefaultTableModel modelo = new javax.swing.table.DefaultTableModel(columnas, 0);
        JTable tablaEntrenadores = new JTable(modelo);

        tablaEntrenadores.setRowHeight(25);
        tablaEntrenadores.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaEntrenadores.setGridColor(new Color(50, 50, 60));
        tablaEntrenadores.setBackground(new Color(30, 30, 40));
        tablaEntrenadores.setForeground(Color.WHITE);

        // Estilo de la cabecera
        javax.swing.table.JTableHeader header = tablaEntrenadores.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(header.getWidth(), 30));
        header.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
                label.setBackground(new Color(40, 40, 50));
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setOpaque(true);
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaEntrenadores);
        scrollPane.getViewport().setBackground(new Color(30, 30, 40));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel inferior idéntico al diseño de socios (Inputs arriba, Botones abajo a
        // la derecha)
        JPanel panelSur = new JPanel(new BorderLayout(0, 10));
        panelSur.setOpaque(false);
        panelSur.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        // Subpanel para etiquetas y campos distribuidos en 4 columnas
        JPanel panelCampos = new JPanel(new GridLayout(2, 4, 15, 5));
        panelCampos.setOpaque(false);

        panelCampos.add(crearLabelForm("Nombre:"));
        panelCampos.add(crearLabelForm("Apellido:"));
        panelCampos.add(crearLabelForm("Especialidad:"));
        panelCampos.add(crearLabelForm("Teléfono:"));

        JTextField txtNombre = new JTextField();
        JTextField txtApellido = new JTextField();
        JTextField txtEspecialidad = new JTextField();
        JTextField txtTelefono = new JTextField();

        panelCampos.add(txtNombre);
        panelCampos.add(txtApellido);
        panelCampos.add(txtEspecialidad);
        panelCampos.add(txtTelefono);

        panelSur.add(panelCampos, BorderLayout.CENTER);

        // Subpanel de botones (Registrar y Eliminar alineados a la derecha)
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);
        JButton btnRegistrar = new JButton("Registrar Entrenador");
        btnRegistrar.setBackground(new Color(255, 204, 0));
        btnRegistrar.setForeground(Color.BLACK);
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setOpaque(true);
        btnRegistrar.setBorderPainted(false);
        btnRegistrar.setPreferredSize(new Dimension(160, 30));

        JButton btnEliminar = new JButton("Eliminar Entrenador");
        btnEliminar.setBackground(new Color(220, 53, 69));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEliminar.setFocusPainted(false);
        btnEliminar.setOpaque(true);
        btnEliminar.setBorderPainted(false);
        btnEliminar.setPreferredSize(new Dimension(160, 30));
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnEliminar);

        panelSur.add(panelBotones, BorderLayout.SOUTH);
        panel.add(panelSur, BorderLayout.SOUTH);

        // Instancia del DAO para interactuar con PostgreSQL
        com.smartfit.gymApp.repository.EntrenadorDAO entrenadorDAO = new com.smartfit.gymApp.repository.EntrenadorDAO();

        // Función para cargar los datos en la tabla
        Runnable cargarDatos = () -> {
            modelo.setRowCount(0);
            java.util.List<com.smartfit.gymApp.model.Entrenador> lista = entrenadorDAO.obtenerEntrenadores();
            for (com.smartfit.gymApp.model.Entrenador e : lista) {
                Object[] fila = {
                        e.getIdEntrenador(),
                        e.getNombre(),
                        e.getApellido(),
                        e.getEspecialidad(),
                        e.getTelefono()
                };
                modelo.addRow(fila);
            }
        };

        // Cargar datos al abrir el panel
        cargarDatos.run();

        // Acción del botón Registrar
        btnRegistrar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String especialidad = txtEspecialidad.getText().trim();
            String telefono = txtTelefono.getText().trim();

            if (nombre.isEmpty() || apellido.isEmpty() || especialidad.isEmpty() || telefono.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Por favor, completa todos los campos.", "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean exito = entrenadorDAO.registrarEntrenador(nombre, apellido, especialidad, telefono);
            if (exito) {
                JOptionPane.showMessageDialog(panel, "¡Entrenador registrado con éxito!");
                txtNombre.setText("");
                txtApellido.setText("");
                txtEspecialidad.setText("");
                txtTelefono.setText("");
                cargarDatos.run();
            } else {
                JOptionPane.showMessageDialog(panel, "Error al registrar en la base de datos.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Acción del botón Eliminar (selecciona la fila de la tabla)
        btnEliminar.addActionListener(e -> {
            int filaSeleccionada = tablaEntrenadores.getSelectedRow();
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(panel, "Por favor, selecciona un entrenador de la tabla para eliminar.",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int idEntrenador = (int) modelo.getValueAt(filaSeleccionada, 0);
            String nombreEntrenador = modelo.getValueAt(filaSeleccionada, 1) + " "
                    + modelo.getValueAt(filaSeleccionada, 2);

            int confirmacion = JOptionPane.showConfirmDialog(
                    panel,
                    "¿Estás seguro de eliminar al entrenador " + nombreEntrenador + "?",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                boolean exito = entrenadorDAO.eliminarEntrenador(idEntrenador);
                if (exito) {
                    JOptionPane.showMessageDialog(panel, "¡Entrenador eliminado con éxito!");
                    cargarDatos.run();
                } else {
                    JOptionPane.showMessageDialog(panel, "Error al eliminar el entrenador de la base de datos.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;

    }

    // Método auxiliar para las etiquetas del formulario
    private JLabel crearLabelForm(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return lbl;
    }
}