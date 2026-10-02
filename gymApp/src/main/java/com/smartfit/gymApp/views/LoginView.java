package com.smartfit.gymApp.views;

import com.smartfit.gymApp.repository.UsuarioRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;

    public LoginView() {
        setTitle("SmartFit - Acceso al Sistema");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel de fondo con degradado moderno oscuro
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Degradado elegante de tonos oscuros y morados sutiles
                GradientPaint gp = new GradientPaint(0, 0, new Color(25, 25, 35), getWidth(), getHeight(), new Color(10, 10, 15));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Formas geométricas decorativas de fondo
                g2d.setColor(new Color(255, 204, 0, 10)); // Amarillo corporativo muy sutil
                g2d.fillOval(80, -40, 280, 280);
                g2d.setColor(new Color(100, 50, 150, 20));
                g2d.fillRoundRect(450, 280, 350, 200, 40, 40);
            }
        };
        backgroundPanel.setLayout(null);

        // ==========================================
        // SECCIÓN IZQUIERDA: Bienvenida y Marca
        // ==========================================
        JLabel lblBienvenido = new JLabel("¡Bienvenido!");
        lblBienvenido.setFont(new Font("Segoe UI", Font.BOLD, 44));
        lblBienvenido.setForeground(Color.WHITE);
        lblBienvenido.setBounds(60, 110, 380, 50);
        backgroundPanel.add(lblBienvenido);

        JLabel lblSlogan = new JLabel("<html>Controla tus socios, entrenadores y rutinas<br>en un solo lugar con la mejor tecnología.</html>");
        lblSlogan.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSlogan.setForeground(new Color(180, 180, 190));
        lblSlogan.setBounds(60, 180, 380, 45);
        backgroundPanel.add(lblSlogan);

        // Badge corporativo SmartFit
        JLabel lblBadge = new JLabel("SMARTFIT SYSTEM");
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBadge.setForeground(new Color(255, 204, 0));
        lblBadge.setBounds(60, 75, 200, 20);
        backgroundPanel.add(lblBadge);

        // ==========================================
        // SECCIÓN DERECHA: Tarjeta de Inicio de Sesión
        // ==========================================
        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(new Color(30, 30, 42)); // Tarjeta sólida elegante
        cardPanel.setBounds(480, 60, 360, 415);
        cardPanel.setLayout(null);

        JLabel lblAcceso = new JLabel("Iniciar Sesión");
        lblAcceso.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblAcceso.setForeground(Color.WHITE);
        lblAcceso.setBounds(40, 35, 250, 35);
        cardPanel.add(lblAcceso);

        // Línea amarilla decorativa bajo el título
        JPanel underline = new JPanel();
        underline.setBackground(new Color(255, 204, 0));
        underline.setBounds(40, 75, 40, 3);
        cardPanel.add(underline);

        // Campo Usuario
        JLabel lblUser = new JLabel("Usuario o Correo");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblUser.setForeground(new Color(180, 180, 190));
        lblUser.setBounds(40, 100, 200, 20);
        cardPanel.add(lblUser);

        txtUsuario = new JTextField();
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsuario.setBounds(40, 125, 280, 40);
        txtUsuario.setBackground(new Color(45, 45, 60));
        txtUsuario.setForeground(Color.WHITE);
        txtUsuario.setCaretColor(Color.WHITE);
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 80), 1),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        cardPanel.add(txtUsuario);

        // Campo Contraseña
        JLabel lblPass = new JLabel("Contraseña");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPass.setForeground(new Color(180, 180, 190));
        lblPass.setBounds(40, 185, 200, 20);
        cardPanel.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setBounds(40, 210, 280, 40);
        txtPassword.setBackground(new Color(45, 45, 60));
        txtPassword.setForeground(Color.WHITE);
        txtPassword.setCaretColor(Color.WHITE);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 80), 1),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        cardPanel.add(txtPassword);

        // Botón Ingresar (Estilo SmartFit Amarillo/Negro)
        btnIngresar = new JButton("Ingresar") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(230, 180, 0) : new Color(255, 204, 0));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIngresar.setForeground(Color.BLACK);
        btnIngresar.setBounds(40, 285, 280, 45);
        btnIngresar.setFocusPainted(false);
        btnIngresar.setContentAreaFilled(false);
        btnIngresar.setBorderPainted(false);
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cardPanel.add(btnIngresar);

        // Acción del botón conectada a la base de datos de Render
        btnIngresar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usuario = txtUsuario.getText().trim();
                String password = new String(txtPassword.getPassword()).trim();

                if (usuario.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor complete todos los campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Verificación contra PostgreSQL en Render
                UsuarioRepository usuarioRepo = new UsuarioRepository();
                boolean accesoValido = usuarioRepo.verificarCredenciales(usuario, password);

                if (accesoValido) {
                    JOptionPane.showMessageDialog(null, "¡Bienvenido a SmartFit, " + usuario + "!", "Acceso Exitoso", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // Cierra el login
                    new MainDashboardView(usuario).setVisible(true); // Abre el panel principal
                } else {
                    JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos.", "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
                    txtPassword.setText("");
                }
            }
        });
        
        // Permitir presionar Enter en la contraseña para ingresar directamente
        txtPassword.addActionListener(btnIngresar.getActionListeners()[0]);

        backgroundPanel.add(cardPanel);
        add(backgroundPanel);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new LoginView().setVisible(true);
        });
    }
}