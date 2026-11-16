package com.smartfit.gymApp.views;

import org.junit.jupiter.api.Test;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.stream.IntStream;
import static org.junit.jupiter.api.Assertions.*;

class TablePagerTest {
    @Test void recorreCientoVeinticincoResultadosEnPaginasDeCincuenta() throws Exception {
        SwingUtilities.invokeAndWait(()->{
            DefaultTableModel model=new DefaultTableModel(new String[]{"ID","Nombre"},0);
            TablePager pager=new TablePager(model,50);
            pager.setRows(rows(1,125));
            assertEquals(125,pager.totalRows());assertEquals(3,pager.pageCount());
            assertEquals(50,model.getRowCount());assertEquals(1,model.getValueAt(0,0));
            pager.nextPage();assertEquals(50,model.getRowCount());assertEquals(51,model.getValueAt(0,0));
            pager.nextPage();assertEquals(25,model.getRowCount());assertEquals(101,model.getValueAt(0,0));assertEquals(125,model.getValueAt(24,0));
            pager.nextPage();assertEquals(2,pager.page());assertEquals(25,model.getRowCount());
            pager.previousPage();assertEquals(51,model.getValueAt(0,0));
            pager.previousPage();pager.previousPage();assertEquals(0,pager.page());assertEquals(1,model.getValueAt(0,0));
        });
    }
    @Test void alReemplazarLosResultadosFiltradosReiniciaLaPaginaYSusIdentidades() throws Exception {
        SwingUtilities.invokeAndWait(()->{
            DefaultTableModel model=new DefaultTableModel(new String[]{"ID","Nombre"},0);
            TablePager pager=new TablePager(model,50);pager.setRows(rows(1,125));pager.nextPage();pager.nextPage();
            pager.setRows(rows(1001,1060));
            assertEquals(0,pager.page());assertEquals(60,pager.totalRows());assertEquals(1001,model.getValueAt(0,0));
            pager.setBusy(true);pager.nextPage();assertEquals(1001,model.getValueAt(0,0));
            pager.setBusy(false);pager.nextPage();assertEquals(10,model.getRowCount());assertEquals(1051,model.getValueAt(0,0));
            pager.setRows(List.of());pager.nextPage();pager.previousPage();assertEquals(0,model.getRowCount());assertEquals(0,pager.page());
        });
    }
    private static List<Object[]> rows(int first,int last){return IntStream.rangeClosed(first,last).mapToObj(n->new Object[]{n,"Persona "+n}).toList();}
}
