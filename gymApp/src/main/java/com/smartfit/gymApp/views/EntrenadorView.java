package com.smartfit.gymApp.views;

import com.smartfit.gymApp.service.EntrenadorService;
import com.smartfit.gymApp.repository.PageResult;
import javax.swing.*;
import java.util.List;

public class EntrenadorView extends DirectoryTableView {
    private final EntrenadorService servicio = new EntrenadorService();
    private final JTextField name = field();
    private final JTextField surname = field();
    private final JTextField specialty = field();
    private final JTextField phone = field();
    private final JButton register = button("Registrar entrenador", true);
    private final JButton save = button("Guardar cambios", false);
    private final JButton deactivate = button("Desactivar", false);
    private final JButton activate = button("Reactivar", false);
    private final JButton clear = button("Nuevo / limpiar", false);
    private Integer selectedId;
    private boolean selectedActive;

   public EntrenadorView() {
        super("Entrenadores", "Busca por nombre, especialidad o teléfono. Filtra por estado.",
                new String[]{"ID", "Nombre", "Apellido", "Especialidad", "Teléfono", "Estado"});
        
        // Aplicar tu filtro numérico al teléfono del entrenador (máximo 9 dígitos)
        ((javax.swing.text.AbstractDocument) phone.getDocument()).setDocumentFilter(new com.smartfit.gymApp.utils.NumericDocumentFilter(9));

        addField("Nombre *", name, 210);
        addField("Apellido *", surname, 250);
        addField("Especialidad *", specialty, 230);
        addField("Teléfono", phone, 145);
        actions.add(register);
        actions.add(save);
        actions.add(clear);
        actions.add(deactivate);
        actions.add(activate);
        table.getColumnModel().getColumn(3).setPreferredWidth(175);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);
        register.addActionListener(e -> save(false));
        save.addActionListener(e -> save(true));
        clear.addActionListener(e -> { table.clearSelection(); selectionChanged(null); name.requestFocusInWindow(); });
        deactivate.addActionListener(e -> changeState(false));
        activate.addActionListener(e -> changeState(true));
        updateActions();
    }

    @Override protected PageResult<Object[]> fetchPage(String query, Boolean active, int page, int pageSize) {
        return servicio.buscarPagina(query,active,page,pageSize).map(t -> new Object[]{t.getIdEntrenador(),
                t.getNombre(),t.getApellido(),t.getEspecialidad(),t.getTelefono(),t.isActivo()?"Activo":"Inactivo"});
    }

    @Override protected void selectionChanged(Object[] row) {
        selectedId = row == null ? null : (Integer) row[0];
        selectedActive = row != null && "Activo".equals(row[5]);
        name.setText(row == null ? "" : text(row[1]));
        surname.setText(row == null ? "" : text(row[2]));
        specialty.setText(row == null ? "" : text(row[3]));
        phone.setText(row == null ? "" : text(row[4]));
        updateActions();
    }

    @Override protected void updateActions() {
        boolean ready = !isBusy();
        register.setEnabled(ready && selectedId == null);
        save.setEnabled(ready && selectedId != null);
        deactivate.setEnabled(ready && selectedId != null && selectedActive);
        activate.setEnabled(ready && selectedId != null && !selectedActive);
        clear.setEnabled(ready);
    }

    private void save(boolean editing) {
        if (editing && selectedId == null) return;
        String n = name.getText().trim();
        String a = surname.getText().trim();
        String s = specialty.getText().trim();
        String t = phone.getText().trim();
        try { servicio.validarDatos(n,a,s,t); }
        catch (IllegalArgumentException ex) { warn(ex.getMessage()); return; }
        Integer id = selectedId;
        write(editing ? "Entrenador actualizado" : "Entrenador registrado",
                () -> editing ? servicio.actualizar(id,n,a,s,t) : servicio.registrar(n,a,s,t));
    }

    private void changeState(boolean active) {
        if (selectedId == null || selectedActive == active) return;
        int id = selectedId;
        String person = name.getText().trim() + " " + surname.getText().trim();
        if (!confirm((active ? "¿Reactivar a " : "¿Desactivar a ") + person
                + "?\nSe conservarán sus datos y las clases asignadas.")) return;
        write(active ? "Entrenador reactivado" : "Entrenador desactivado", () -> servicio.cambiarEstado(id,active));
    }
}
