package com.smartfit.gymApp.views;

import javax.swing.*;
import java.awt.*;

public class MainDashboardView extends JFrame {

    public MainDashboardView(String usuarioLogueado) {
        setTitle("SmartFit - Panel Principal");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel lateral izquierdo (Sidebar)
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(250, 650));
        sidebar.setBackground(new Color(15, 15, 18));
        sidebar.setLayout(null);

        JLabel lblLogo = new JLabel("SMARTFIT SYSTEM");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLogo.setForeground(new Color(255, 204, 0));
        lblLogo.setBounds(25, 30, 200, 30);
        sidebar.add(lblLogo);

        // Opciones del menú usando símbolos compatibles que no fallan en Java Swing
        String[] opciones = {
            "   🏠   Inicio", 
            "   👥   Gestión de Socios", 
            "   💪   Entrenadores", 
            "   📋   Rutinas y Clases", 
            "   🚪   Cerrar Sesión"
        };
        
        int yPos = 100;
        
        for (String opcion : opciones) {
            JButton btnMenu = new JButton(opcion);
            
            // Forzamos una fuente compatible con símbolos gráficos en Windows
            btnMenu.setFont(new Font("Dialog", Font.BOLD, 13));
            btnMenu.setForeground(Color.WHITE);
            btnMenu.setBackground(new Color(15, 15, 18));
            btnMenu.setHorizontalAlignment(SwingConstants.LEFT);
            btnMenu.setBorderPainted(false);
            btnMenu.setFocusPainted(false);
            
            // Ancho de 230 para alojar todo correctamente
            btnMenu.setBounds(15, yPos, 230, 42);
            btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Efecto hover (cambio de color y texto amarillo al pasar el mouse)
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

            // Acción para abrir la Gestión de Socios conectada a la BD de Render
            if (opcion.contains("Gestión de Socios")) {
                btnMenu.addActionListener(e -> {
                    new SocioView().setVisible(true);
                });
            }

            // Acción para Cerrar Sesión
            if (opcion.contains("Cerrar Sesión")) {
                btnMenu.addActionListener(e -> {
                    dispose();
                    new LoginView().setVisible(true);
                });
            }

            sidebar.add(btnMenu);
            yPos += 55;
        }

        // Panel de contenido principal derecho
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(new Color(245, 247, 250));
        contentPanel.setLayout(null);

        JLabel lblWelcome = new JLabel("¡Bienvenido, " + usuarioLogueado + "!");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblWelcome.setForeground(new Color(30, 30, 30));
        lblWelcome.setBounds(40, 30, 500, 40);
        contentPanel.add(lblWelcome);

        JLabel lblSub = new JLabel("Aquí tienes el resumen operativo del gimnasio para hoy.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(new Color(100, 100, 100));
        lblSub.setBounds(40, 75, 500, 25);
        contentPanel.add(lblSub);

        // Tarjetas de Estadísticas rápidas (KPIs)
        contentPanel.add(crearTarjetaKPI("Socios Activos", "1,240", 40, 130, new Color(255, 204, 0)));
        contentPanel.add(crearTarjetaKPI("Entrenadores", "18", 270, 130, new Color(40, 40, 45)));
        contentPanel.add(crearTarjetaKPI("Clases Hoy", "35", 500, 130, new Color(40, 40, 45)));

        // Agregar paneles al Frame principal
        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    // Método auxiliar para crear tarjetas de estadísticas rápidas
    private JPanel crearTarjetaKPI(String titulo, String valor, int x, int y, Color colorFondo) {
        JPanel card = new JPanel();
        card.setBounds(x, y, 210, 110);
        card.setBackground(colorFondo);
        card.setLayout(null);

        JLabel lblTitleCard = new JLabel(titulo);
        lblTitleCard.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitleCard.setForeground(colorFondo.equals(new Color(255, 204, 0)) ? Color.BLACK : Color.WHITE);
        lblTitleCard.setBounds(20, 20, 170, 20);
        card.add(lblTitleCard);

        JLabel lblValueCard = new JLabel(valor);
        lblValueCard.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValueCard.setForeground(colorFondo.equals(new Color(255, 204, 0)) ? Color.BLACK : Color.WHITE);
        lblValueCard.setBounds(20, 50, 170, 40);
        card.add(lblValueCard);

        return card;
    }
}