package com.smartfit.gymApp.utils;

import javax.swing.*;
import java.awt.*;


public final class UI {

    public static final Color FONDO = new Color(25, 25, 35);
    public static final Color PANEL = new Color(30, 30, 42);
    public static final Color CAMPO = new Color(45, 45, 60);
    public static final Color ACENTO = new Color(255, 204, 0);

    private UI() {}

    public static JLabel titulo(String texto) {
        JLabel l = new JLabel("   " + texto, JLabel.LEFT);
        l.setFont(new Font("Segoe UI", Font.BOLD, 22));
        l.setForeground(Color.WHITE);
        l.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        return l;
    }

    public static JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(Color.LIGHT_GRAY);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return l;
    }

    public static JButton boton(String texto) {
        JButton b = new JButton(texto);
        b.setBackground(ACENTO);
        b.setForeground(Color.BLACK);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JTextField campo(int columnas) {
        JTextField t = new JTextField(columnas);
        t.setBackground(CAMPO);
        t.setForeground(Color.WHITE);
        t.setCaretColor(Color.WHITE);
        return t;
    }

    public static JTextArea areaSalida() {
        JTextArea a = new JTextArea();
        a.setEditable(false);
        a.setBackground(CAMPO);
        a.setForeground(Color.WHITE);
        a.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        return a;
    }

    public static JScrollPane scroll(JComponent c, String titulo) {
        JScrollPane sp = new JScrollPane(c);
        sp.getViewport().setBackground(PANEL);
        sp.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 90)), titulo,
                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), ACENTO));
        return sp;
    }

    public static JPanel fila() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        p.setOpaque(false);
        return p;
    }

    public static void info(Component padre, String msg) {
        JOptionPane.showMessageDialog(padre, msg, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void aviso(Component padre, String msg) {
        JOptionPane.showMessageDialog(padre, msg, "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}