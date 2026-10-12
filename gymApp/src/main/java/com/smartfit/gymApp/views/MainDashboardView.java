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

        JPanel panelBotonesMenu = new JPanel(new GridLayout(0, 1, 0, 8));
        panelBotonesMenu.setOpaque(false);
        panelBotonesMenu.setBorder(BorderFactory.createEmptyBorder(10, 15, 20, 15));

        String[] opciones = {
                "Inicio",
                "Gestión de Socios",
                "Entrenadores",
                "Clases",
                "Reservas",
                "Incidencias",
                "Auditoría",
                "Cerrar Sesión"
        };

        for (String opcion : opciones) {
            JButton btnMenu = new JButton("   " + opcion);
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

            if (opcion.equals("Inicio")) {
                btnMenu.addActionListener(e -> cardLayout.show(contentPanel, "INICIO"));
            } else if (opcion.equals("Gestión de Socios")) {
                btnMenu.addActionListener(e -> cardLayout.show(contentPanel, "SOCIOS"));
            } else if (opcion.equals("Entrenadores")) {
                btnMenu.addActionListener(e -> cardLayout.show(contentPanel, "ENTRENADORES"));
            } else if (opcion.equals("Clases")) {
                btnMenu.addActionListener(e -> cardLayout.show(contentPanel, "CLASES"));
            } else if (opcion.equals("Reservas")) {
                btnMenu.addActionListener(e -> cardLayout.show(contentPanel, "RESERVAS"));
            } else if (opcion.equals("Incidencias")) {
                btnMenu.addActionListener(e -> cardLayout.show(contentPanel, "INCIDENCIAS"));
            } else if (opcion.equals("Auditoría")) {
                btnMenu.addActionListener(e -> cardLayout.show(contentPanel, "AUDITORIA"));
            } else if (opcion.equals("Cerrar Sesión")) {
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
        EntrenadorView panelEntrenadores = new EntrenadorView();
        ClaseView panelClases = new ClaseView();
        ReservaView panelReservas = new ReservaView();
        IncidenciaView panelIncidencias = new IncidenciaView();
        AuditoriaView panelAuditoria = new AuditoriaView();

        contentPanel.add(panelInicio, "INICIO");
        contentPanel.add(panelSocios, "SOCIOS");
        contentPanel.add(panelEntrenadores, "ENTRENADORES");
        contentPanel.add(panelClases, "CLASES");
        contentPanel.add(panelReservas, "RESERVAS");
        contentPanel.add(panelIncidencias, "INCIDENCIAS");
        contentPanel.add(panelAuditoria, "AUDITORIA");

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
}
