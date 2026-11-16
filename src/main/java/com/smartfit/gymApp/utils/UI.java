package com.smartfit.gymApp.utils;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;


public final class UI {

    public static final Color FONDO = new Color(25, 26, 29);
    public static final Color PANEL = new Color(34, 35, 39);
    public static final Color CAMPO = new Color(44, 46, 50);
    public static final Color ACENTO = new Color(255, 204, 0);

    private UI() {}

    public static void separadorAmarillo(JSplitPane panel) {
        panel.setUI(new BasicSplitPaneUI() {
            @Override public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    @Override public void paint(Graphics g) {
                        g.setColor(ACENTO);
                        g.fillRect(0, 0, getWidth(), getHeight());
                    }

                    @Override public void print(Graphics g) {
                        paint(g);
                    }
                };
            }
        });
        panel.setBorder(null);
        panel.setOpaque(true);
        panel.setBackground(ACENTO);
        ((BasicSplitPaneUI) panel.getUI()).getDivider().setBorder(null);
        ((BasicSplitPaneUI) panel.getUI()).getDivider().setBackground(ACENTO);
    }

    public static void aplicarTema() {
        UIManager.put("Panel.background", FONDO);
        UIManager.put("OptionPane.background", FONDO);
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Label.foreground", Color.WHITE);
        UIManager.put("TextField.background", CAMPO);
        UIManager.put("TextField.foreground", Color.WHITE);
        UIManager.put("ComboBox.background", CAMPO);
        UIManager.put("ComboBox.foreground", Color.WHITE);
        UIManager.put("ComboBox.selectionBackground", ACENTO);
        UIManager.put("ComboBox.selectionForeground", Color.BLACK);
        UIManager.put("Table.background", PANEL);
        UIManager.put("Table.foreground", Color.WHITE);
        UIManager.put("Table.selectionBackground", ACENTO);
        UIManager.put("Table.selectionForeground", Color.BLACK);
        UIManager.put("TableHeader.background", CAMPO);
        UIManager.put("TableHeader.foreground", Color.WHITE);
        UIManager.put("ScrollPane.background", PANEL);
    }

    public static void estilizarTabla(JTable table) {
        table.setBackground(PANEL); table.setForeground(Color.WHITE);
        table.setSelectionBackground(ACENTO); table.setSelectionForeground(Color.BLACK);
        table.setRowHeight(32); table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(CAMPO); table.getTableHeader().setForeground(Color.WHITE);
        table.setShowVerticalLines(false); table.setGridColor(new Color(60, 62, 67));
        table.setFillsViewportHeight(true); table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

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
