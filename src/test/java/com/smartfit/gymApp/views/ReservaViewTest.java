package com.smartfit.gymApp.views;

import com.smartfit.gymApp.model.*;
import com.smartfit.gymApp.repository.ReservaDAO;
import com.smartfit.gymApp.service.GestorReservasClase;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.util.List;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class ReservaViewTest {
    @Test void bloqueaConfirmadosYEsperaAunqueLaTablaEsteFiltrada() throws Exception {
        SwingUtilities.invokeAndWait(()->{
            try {
                var vista=new ReservaView();var clase=new Clase(1,"Yoga",1,"LUNES",null,null,3,0);
                var filas=List.of(fila(1,"SM-0001","CONFIRMADA"),fila(2,"SM-0002","LISTA_ESPERA"),fila(3,"SM-0003","CANCELADA"));
                campo(vista,"carga",new ReservaDAO.Carga(clase,true,filas,new GestorReservasClase(clase)));
                @SuppressWarnings("unchecked") var socios=(JComboBox<ReservaDAO.SocioOpcion>)campo(vista,"socios");
                JButton reservar=(JButton)campo(vista,"reservar");
                for(int i=1;i<=4;i++)socios.addItem(new ReservaDAO.SocioOpcion("SM-000"+i,"Socio "+i,true));
                for(int i=0;i<2;i++){socios.setSelectedIndex(i);assertFalse(reservar.isEnabled());}
                socios.setSelectedIndex(2);assertTrue(reservar.isEnabled()); // cancelada permite otra reserva
                socios.setSelectedIndex(3);assertTrue(reservar.isEnabled());
                campo(vista,"ocupado",true);socios.setSelectedIndex(2);assertFalse(reservar.isEnabled());
                var metodo=ReservaView.class.getDeclaredMethod("reservar");metodo.setAccessible(true);metodo.invoke(vista);
                campo(vista,"ocupado",false);
                campo(vista,"carga",new ReservaDAO.Carga(clase,false,filas,new GestorReservasClase(clase)));
                socios.setSelectedIndex(3);assertFalse(reservar.isEnabled());
            }catch(Exception ex){throw new RuntimeException(ex);}
        });
    }
    private static ReservaDAO.Fila fila(int id,String codigo,String estado){return new ReservaDAO.Fila(new ReservaClase(id,1,codigo,LocalDate.now(),estado,-1),"Socio",true);}
    private static Object campo(Object o,String nombre)throws Exception{var f=o.getClass().getDeclaredField(nombre);f.setAccessible(true);return f.get(o);}
    private static void campo(Object o,String nombre,Object valor)throws Exception{var f=o.getClass().getDeclaredField(nombre);f.setAccessible(true);f.set(o,valor);}
}
