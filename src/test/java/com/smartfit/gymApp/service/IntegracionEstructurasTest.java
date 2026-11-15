package com.smartfit.gymApp.service;

import com.smartfit.gymApp.model.*;
import com.smartfit.gymApp.repository.*;
import com.smartfit.gymApp.structures.arreglos.ArregloSocios;
import com.smartfit.gymApp.structures.listas.ListaEnlazadaSimple;
import com.smartfit.gymApp.structures.tad.TADLista;
import org.junit.jupiter.api.Test;
import java.time.LocalTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class IntegracionEstructurasTest {
    private Socio socio(int id,boolean activo){Socio s=new Socio(String.format("SM-%04d",id),"Nombre "+id,"Apellido","12345678");s.setIdSocio((long)id);s.setActivo(activo);return s;}
    @Test void directorioPaginaFiltraYBuscaEnAvlConRegistrosReales(){
        List<Socio> filas=new ArrayList<>();for(int i=125;i>0;i--)filas.add(socio(i,i%2==0));
        SocioService servicio=new SocioService(new SocioDAO(){@Override public List<Socio> listarSocios(){return filas;}},null);
        var pagina=servicio.buscarPagina("",null,2,50);assertEquals(125,pagina.total());assertEquals(25,pagina.items().size());assertEquals(101,pagina.items().get(0).id());
        assertEquals(62,servicio.buscarPagina("",true,0,50).total());
        assertEquals(1,servicio.buscarPagina("sm-0125",false,0,50).total());
        assertEquals(0,servicio.buscarPagina("SM-9999",null,0,50).total());
        assertEquals(0,servicio.buscarPagina("%",null,0,50).total());
        assertEquals(125,servicio.buscarPagina("",null,99,50).total());
    }
    @Test void copiaDelArregloPuedeActualizarYEliminarSinCambiarOriginal(){
        var original=new ArregloSocios(0);original.agregar(socio(2,true));original.agregar(socio(1,true));
        var copia=original.copiar();Socio actualizado=socio(2,false);assertTrue(copia.actualizar(actualizado));
        assertTrue(original.get(0).getActivo());assertFalse(copia.get(0).getActivo());
        copia.ordenar(Comparator.naturalOrder());assertEquals("SM-0001",copia.get(0).getCodigoSocio());
        assertTrue(copia.eliminar(s->s.getCodigoSocio().equals("SM-0001")));assertEquals(2,original.tamanio());assertEquals(1,copia.tamanio());
    }
    @Test void tadEnlazadoOrdenaMilElementosBuscaYElimina(){
        var lista=new ListaEnlazadaSimple<Integer>();TADLista<Integer> tad=lista;
        for(int n=0;n<1000;n++)tad.agregar(n);
        lista.ordenar(Comparator.naturalOrder());assertEquals(0,lista.aLista().get(0));assertEquals(999,lista.aLista().get(999));
        assertEquals(500,tad.buscar(x->x==500));assertTrue(tad.eliminar(x->x==500));assertNull(tad.buscar(x->x==500));assertEquals(999,tad.tamanio());
        tad.vaciar();lista.ordenar(Comparator.naturalOrder());assertTrue(tad.estaVacia());
    }
    @Test void agendaConservaClasesSimultaneasYBloquesParciales(){
        var a=new Clase(1,"Yoga",1,"MIÉRCOLES",LocalTime.of(9,30),LocalTime.of(11,0),20,2);
        var b=new Clase(2,"Spinning",2,"MIERCOLES",LocalTime.of(10,0),LocalTime.of(10,45),15,8);
        var agenda=new AgendaSemanal(List.of(new ClaseDAO.Registro(a,"Ana",true),new ClaseDAO.Registro(b,"Juan",true)));
        assertTrue(agenda.resumen(2,9).contains("Yoga"));assertTrue(agenda.resumen(2,10).contains("Yoga"));assertTrue(agenda.resumen(2,10).contains("Spinning"));
        assertEquals("",agenda.resumen(2,11));assertTrue(agenda.detalle(2,10).contains("Juan"));
        var inactiva=new AgendaSemanal(List.of(new ClaseDAO.Registro(a,"Ana",false)));assertEquals("",inactiva.resumen(2,9));
    }
    @Test void listaDeClasesOrdenaSinOrdenarEnBaseDeDatos(){
        var a=new ClaseDAO.Registro(new Clase(1,"Yoga",1,"LUNES",LocalTime.NOON,LocalTime.of(13,0),10,0),"Ana",true);
        var b=new ClaseDAO.Registro(new Clase(2,"Boxeo",1,"MARTES",LocalTime.NOON,LocalTime.of(13,0),10,0),"Ana",false);
        var servicio=new ClaseService(new ClaseDAO(){@Override public List<Registro> listarRegistros(){return List.of(a,b);}});
        assertEquals(List.of(b,a),servicio.listar());
    }
}
