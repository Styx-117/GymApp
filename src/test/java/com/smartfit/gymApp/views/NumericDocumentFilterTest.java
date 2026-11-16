package com.smartfit.gymApp.views;

import com.smartfit.gymApp.utils.NumericDocumentFilter;
import org.junit.jupiter.api.Test;
import javax.swing.text.PlainDocument;
import static org.junit.jupiter.api.Assertions.*;

class NumericDocumentFilterTest {
    @Test void rechazaPegadoMixtoYExcesoPermitiendoReemplazarYBorrar() throws Exception {
        for(int limite:new int[]{8,9}){
            var documento=new PlainDocument();documento.setDocumentFilter(new NumericDocumentFilter(limite));
            for(String texto:new String[]{"123ABC","123-456","+51987654321","123 456","１２３"}){
                documento.insertString(0,texto,null);assertEquals(0,documento.getLength());
            }
            String valido="1".repeat(limite);documento.insertString(0,valido,null);
            documento.insertString(limite,"2",null);assertEquals(valido,documento.getText(0,limite));
            documento.replace(0,1,"a",null);assertEquals(valido,documento.getText(0,limite));
            documento.replace(0,limite,"987",null);assertEquals("987",documento.getText(0,3));
            documento.replace(0,3,null,null);assertEquals(0,documento.getLength());
            documento.insertString(0,"0123",null);documento.remove(0,4);assertEquals(0,documento.getLength());
        }
    }
}
