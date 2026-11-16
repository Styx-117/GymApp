package com.smartfit.gymApp.views;

import com.smartfit.gymApp.utils.UI;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Objects;

/** Pagina la presentacion sin consultar BD ni recortar las estructuras de negocio. */
public final class TablePager extends JPanel {
    private final DefaultTableModel model;
    private final int pageSize;
    private final JLabel status=UI.etiqueta("0–0 de 0 registros");
    private final JButton previous=DirectoryTableView.button("<",false),next=DirectoryTableView.button(">",false);
    private List<Object[]> rows=List.of();
    private int page;
    private boolean busy;

    public TablePager(DefaultTableModel model,int pageSize) {
        super(new BorderLayout(12,0));
        if(pageSize<=0)throw new IllegalArgumentException("El tamaño de página debe ser positivo.");
        this.model=Objects.requireNonNull(model);this.pageSize=pageSize;setBackground(UI.FONDO);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,0,3,0,UI.ACENTO),
                BorderFactory.createEmptyBorder(6,10,6,8)));
        JPanel buttons=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));buttons.setOpaque(false);
        previous.setToolTipText("Página anterior");previous.getAccessibleContext().setAccessibleName("Página anterior");
        next.setToolTipText("Página siguiente");next.getAccessibleContext().setAccessibleName("Página siguiente");
        previous.setPreferredSize(new Dimension(36,30));next.setPreferredSize(new Dimension(36,30));
        buttons.add(previous);buttons.add(next);add(status,BorderLayout.CENTER);add(buttons,BorderLayout.EAST);
        status.setToolTipText("La ordenación de la tabla se aplica a la página visible.");
        previous.addActionListener(e->previousPage());next.addActionListener(e->nextPage());render();
    }

    /** Un filtro o recarga reemplaza los resultados y vuelve a la primera pagina. */
    public void setRows(List<Object[]> rows) {this.rows=List.copyOf(rows);page=0;render();}
    public int totalRows(){return rows.size();}
    public int page(){return page;}
    public int pageCount(){return Math.max(1,(rows.size()+pageSize-1)/pageSize);}
    public void setBusy(boolean busy){this.busy=busy;buttons();}
    public void nextPage(){if(!busy&&page+1<pageCount()){page++;render();}}
    public void previousPage(){if(!busy&&page>0){page--;render();}}

    private void render() {
        model.setRowCount(0);
        int first=page*pageSize,last=Math.min(rows.size(),first+pageSize);
        for(int n=first;n<last;n++)model.addRow(rows.get(n));
        status.setText((rows.isEmpty()?0:first+1)+"–"+last+" de "+rows.size()+" registros · Página "+(page+1)+" de "+pageCount());
        buttons();
    }
    private void buttons(){previous.setEnabled(!busy&&page>0);next.setEnabled(!busy&&page+1<pageCount());}
}
