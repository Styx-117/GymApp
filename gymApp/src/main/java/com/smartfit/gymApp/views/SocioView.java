package com.smartfit.gymApp.views;

import com.smartfit.gymApp.dto.ReniecData;
import com.smartfit.gymApp.service.SocioService;
import com.smartfit.gymApp.repository.PageResult;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.List;

public class SocioView extends DirectoryTableView {
    private final SocioService servicio = new SocioService();
    private final JTextField name = field();
    private final JTextField surname = field();
    private final JTextField dni = field();
    private final JTextField phone = field();
    private final JTextField email = field();
    private final JButton register = button("Registrar socio", true);
    private final JButton save = button("Guardar cambios", false);
    private final JButton deactivate = button("Desactivar", false);
    private final JButton activate = button("Reactivar", false);
    private final JButton clear = button("Nuevo / limpiar", false);
    private final JButton consult = button("Consultar DNI", false);
    private Integer selectedId;
    private boolean selectedActive;
    private boolean consulting;

   public SocioView() {
        super("Socios", "Busca por código, DNI, nombre o contacto. Filtra por estado.",
                new String[]{"ID", "Código", "Nombre", "Apellido", "DNI", "Teléfono", "Correo", "Estado"});
        
        // Aplicar  filtro numérico reutilizable
        ((AbstractDocument) dni.getDocument()).setDocumentFilter(new com.smartfit.gymApp.utils.NumericDocumentFilter(8));
        ((AbstractDocument) phone.getDocument()).setDocumentFilter(new com.smartfit.gymApp.utils.NumericDocumentFilter(9));

        JPanel dniRow = new JPanel(new BorderLayout(6, 0));
        dniRow.setOpaque(false);
        dniRow.add(dni, BorderLayout.CENTER);
        dniRow.add(consult, BorderLayout.EAST);
        consult.setToolTipText("Opcional: puedes escribir los nombres y apellidos manualmente");

        addField("Nombre *", name, 210);
        addField("Apellido *", surname, 250);
        addField("DNI * · 8 dígitos", dniRow, 225);
        addField("Teléfono", phone, 145);
        addField("Correo", email, 280);
        
        actions.add(register);
        actions.add(save);
        actions.add(clear);
        actions.add(deactivate);
        actions.add(activate);
        
        table.getColumnModel().getColumn(6).setPreferredWidth(210);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(7).setPreferredWidth(90);
        
        register.addActionListener(e -> save(false));
        save.addActionListener(e -> save(true));
        clear.addActionListener(e -> { table.clearSelection(); selectionChanged(null); name.requestFocus(); });
        deactivate.addActionListener(e -> changeState(false));
        activate.addActionListener(e -> changeState(true));
        consult.addActionListener(e -> consultDni());
        
        updateActions();
    }

    @Override protected PageResult<Object[]> fetchPage(String query, Boolean active, int page, int pageSize) {
        return servicio.buscarPagina(query,active,page,pageSize).map(s -> new Object[]{s.id(),s.codigo(),
                s.nombre(),s.apellido(),s.dni(),s.telefono(),s.correo(),s.activo()?"Activo":"Inactivo"});
    }

    @Override protected void selectionChanged(Object[] row) {
        selectedId = row == null ? null : (Integer) row[0];
        selectedActive = row != null && "Activo".equals(row[7]);
        name.setText(row == null ? "" : text(row[2]));
        surname.setText(row == null ? "" : text(row[3]));
        dni.setText(row == null ? "" : text(row[4]));
        phone.setText(row == null ? "" : text(row[5]));
        email.setText(row == null ? "" : text(row[6]));
        updateActions();
    }

    @Override protected void updateActions() {
        boolean ready = !isBusy();
        register.setEnabled(ready && selectedId == null);
        save.setEnabled(ready && selectedId != null);
        deactivate.setEnabled(ready && selectedId != null && selectedActive);
        activate.setEnabled(ready && selectedId != null && !selectedActive);
        clear.setEnabled(ready);
        consult.setEnabled(ready && !consulting);
    }

    private void save(boolean editing) {
        if (editing && selectedId == null) return;
        String n = name.getText().trim();
        String a = surname.getText().trim();
        String d = dni.getText().trim();
        String t = phone.getText().trim();
        String c = email.getText().trim();
        try { servicio.validarDatos(n,a,d,t,c); }
        catch (IllegalArgumentException ex) { warn(ex.getMessage()); return; }
        Integer id = selectedId;
        write(editing ? "Socio actualizado" : "Socio registrado",
                () -> editing ? servicio.actualizarDatos(id,n,a,d,t,c) : servicio.registrarDatos(n,a,d,t,c));
    }

    private void changeState(boolean active) {
        if (selectedId == null || selectedActive == active) return;
        int id = selectedId;
        String person = name.getText().trim() + " " + surname.getText().trim();
        if (!confirm((active ? "¿Reactivar a " : "¿Desactivar a ") + person
                + "?\nSe conservarán sus datos e historial.")) return;
        write(active ? "Socio reactivado" : "Socio desactivado", () -> servicio.cambiarEstado(id,active));
    }

    private void consultDni() {
        String value = dni.getText().trim();
        if (!value.matches("\\d{8}")) { warn("Escribe un DNI de 8 dígitos antes de consultar."); return; }
        String originalName = name.getText();
        String originalSurname = surname.getText();
        consulting = true;
        consult.setText("Consultando…");
        updateActions();
        new SwingWorker<ReniecData, Void>() {
            @Override protected ReniecData doInBackground() throws Exception { return servicio.consultarDni(value); }
            @Override protected void done() {
                consulting = false;
                consult.setText("Consultar DNI");
                updateActions();
                if (!value.equals(dni.getText().trim()) || !originalName.equals(name.getText())
                        || !originalSurname.equals(surname.getText())) return;
                try {
                    ReniecData data = get();
                    name.setText(data.getNombres());
                    surname.setText(java.util.stream.Stream.of(data.getApellidoPaterno(), data.getApellidoMaterno())
                            .filter(s -> s != null && !s.isBlank()).collect(java.util.stream.Collectors.joining(" ")));
                } catch (Exception e) {
                    warn("La consulta no está disponible. Puedes ingresar nombre y apellido manualmente y guardar.");
                }
            }
        }.execute();
    }
}
