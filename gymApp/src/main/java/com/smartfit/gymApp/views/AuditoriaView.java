package com.smartfit.gymApp.views;

import com.smartfit.gymApp.model.Auditoria;
import com.smartfit.gymApp.service.AuditoriaService;
import com.smartfit.gymApp.utils.UI;

import javax.swing.*;
import java.awt.*;


public class AuditoriaView extends JPanel {

    private final AuditoriaService servicio = AuditoriaService.getInstance();
    private final JTextArea txtPila = UI.areaSalida();
    private final JComboBox<String> cmbAccion = new JComboBox<>(new String[]{
            "REGISTRAR_SOCIO", "ACTUALIZAR_SOCIO", "ELIMINAR_SOCIO",
            "RESERVAR_CLASE", "ACTUALIZAR_RESERVA", "CANCELAR_RESERVA",
            "REGISTRAR_INCIDENCIA", "ACTUALIZAR_INCIDENCIA", "ELIMINAR_INCIDENCIA",
            "ATENDER_INCIDENCIA"});
    private final JTextField txtDetalle = UI.campo(20);

    public AuditoriaView() {
        setLayout(new BorderLayout());
        setBackground(UI.FONDO);
        add(UI.titulo("Historial de acciones (Pila - LIFO)"), BorderLayout.NORTH);
        add(UI.scroll(txtPila, "Pila (el tope es la acción más reciente)"), BorderLayout.CENTER);

        JPanel sur = new JPanel(new GridLayout(2, 1));
        sur.setBackground(UI.PANEL);
        JPanel f1 = UI.fila();
        f1.add(UI.etiqueta("Acción:"));
        f1.add(cmbAccion);
        f1.add(UI.etiqueta("Detalle:"));
        f1.add(txtDetalle);
        JButton btnPush = UI.boton("Registrar (push)");
        f1.add(btnPush);
        JPanel f2 = UI.fila();
        JButton btnPeek = UI.boton("Ver última (peek)");
        JButton btnPop = UI.boton("Revertir última (pop)");
        f2.add(btnPeek);
        f2.add(btnPop);
        sur.add(f1);
        sur.add(f2);
        add(sur, BorderLayout.SOUTH);

        btnPush.addActionListener(e -> {
            String detalle = txtDetalle.getText().trim();
            servicio.registrar((String) cmbAccion.getSelectedItem(), detalle.isEmpty() ? "-" : detalle);
            txtDetalle.setText("");
            refrescar();
        });
        btnPeek.addActionListener(e -> {
            Auditoria a = servicio.ultima();
            if (a == null) UI.aviso(this, "La pila está vacía: no hay acciones registradas.");
            else UI.info(this, "Última acción:\n" + a.getAccion() + " - " + a.getDetalle() + "\n" + a.getFechaHora());
        });
        btnPop.addActionListener(e -> {
            Auditoria a = servicio.revertirUltima(null);
            if (a == null) UI.aviso(this, "La pila está vacía: no hay nada que revertir.");
            else UI.info(this, "Acción revertida: " + a.getAccion() + " - " + a.getDetalle());
            refrescar();
        });
        refrescar();
    }

    private void refrescar() {
        StringBuilder sb = new StringBuilder();
        sb.append("Acciones en la pila: ").append(servicio.total()).append("\n\n");
        servicio.getHistorial().recorrer(a -> sb.append(String.format("#%d  %s  [%s]  %s%n",
                a.getIdAuditoria(), a.getAccion(), a.getDetalle(),
                a.getFechaHora().toLocalTime().withNano(0))));
        if (servicio.total() == 0) sb.append("(pila vacía)");
        txtPila.setText(sb.toString());
    }
}