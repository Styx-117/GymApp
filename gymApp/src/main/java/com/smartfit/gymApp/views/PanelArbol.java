package com.smartfit.gymApp.views;

import com.smartfit.gymApp.structures.arboles.NodoVisual;
import com.smartfit.gymApp.structures.arboles.Recorridos;
import com.smartfit.gymApp.utils.UI;

import javax.swing.*;
import java.awt.*;


public class PanelArbol extends JPanel {

    private static final int ANCHO = 76;
    private static final int ALTO = 28;
    private static final int SEPARACION_Y = 72;

    private NodoVisual raiz;
    private String resaltado;

    public PanelArbol() {
        setBackground(UI.PANEL);
    }

    public void setRaiz(NodoVisual raiz) {
        this.raiz = raiz;
        int h = Recorridos.altura(raiz);
        int ancho = Math.max(700, (1 << Math.min(Math.max(h, 1), 8)) * (ANCHO / 2 + 10));
        setPreferredSize(new Dimension(ancho, Math.max(260, h * SEPARACION_Y + 70)));
        revalidate();
        repaint();
    }

    
    public void setResaltado(String etiqueta) {
        this.resaltado = etiqueta;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (raiz == null) {
            g.setColor(Color.GRAY);
            g.setFont(new Font("Segoe UI", Font.ITALIC, 15));
            g.drawString("Árbol vacío", 20, 30);
            return;
        }
        dibujar(g, raiz, getWidth() / 2, 30, getWidth() / 4);
    }

    private void dibujar(Graphics2D g, NodoVisual n, int x, int y, int desplazamiento) {
        int sig = Math.max(desplazamiento / 2, ANCHO / 2 + 2);
        g.setStroke(new BasicStroke(2f));
        if (n.izquierdo() != null) {
            g.setColor(new Color(120, 120, 150));
            g.drawLine(x, y, x - desplazamiento, y + SEPARACION_Y);
            dibujar(g, n.izquierdo(), x - desplazamiento, y + SEPARACION_Y, sig);
        }
        if (n.derecho() != null) {
            g.setColor(new Color(120, 120, 150));
            g.drawLine(x, y, x + desplazamiento, y + SEPARACION_Y);
            dibujar(g, n.derecho(), x + desplazamiento, y + SEPARACION_Y, sig);
        }
        boolean marcado = n.etiqueta().equals(resaltado);
        g.setColor(marcado ? new Color(80, 200, 120) : UI.ACENTO);
        g.fillRoundRect(x - ANCHO / 2, y - ALTO / 2, ANCHO, ALTO, 14, 14);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Segoe UI", Font.BOLD, 12));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(n.etiqueta(), x - fm.stringWidth(n.etiqueta()) / 2, y + fm.getAscent() / 2 - 1);
        String det = n.detalle();
        if (!det.isEmpty()) {
            g.setColor(Color.LIGHT_GRAY);
            g.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            FontMetrics f2 = g.getFontMetrics();
            g.drawString(det, x - f2.stringWidth(det) / 2, y + ALTO / 2 + 12);
        }
    }
}