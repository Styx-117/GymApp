package com.smartfit.gymApp.service;

import com.smartfit.gymApp.repository.ClaseDAO;
import com.smartfit.gymApp.structures.listas.ListaEnlazadaSimple;
import java.text.Normalizer;
import java.util.Locale;

/** Matriz día/hora con listas propias para conservar clases simultáneas. */
public final class AgendaSemanal {
    public static final String[] DIAS={"LUNES","MARTES","MIERCOLES","JUEVES","VIERNES","SABADO","DOMINGO"};
    private final ListaEnlazadaSimple<ClaseDAO.Registro>[][] bloques;
    @SuppressWarnings("unchecked")
    public AgendaSemanal(Iterable<ClaseDAO.Registro> clases) {
        bloques=(ListaEnlazadaSimple<ClaseDAO.Registro>[][])new ListaEnlazadaSimple<?>[7][24];
        for(int d=0;d<7;d++)for(int h=0;h<24;h++)bloques[d][h]=new ListaEnlazadaSimple<>();
        for(var r:clases){
            if(!r.activo())continue;
            var c=r.clase();int dia=dia(c.getDiaSemana());
            if(dia<0||c.getHoraInicio()==null||c.getHoraFin()==null)continue;
            int inicio=c.getHoraInicio().toSecondOfDay(),fin=c.getHoraFin().toSecondOfDay();
            if(fin<=inicio)continue;
            for(int h=0;h<24;h++)if(inicio<(h+1)*3600 && fin>h*3600)bloques[dia][h].agregar(r);
        }
    }
    private int dia(String texto){
        if(texto==null)return -1;
        String normal=Normalizer.normalize(texto,Normalizer.Form.NFD).replaceAll("\\p{M}","").trim().toUpperCase(Locale.ROOT);
        for(int i=0;i<DIAS.length;i++)if(DIAS[i].equals(normal))return i;return -1;
    }
    public String resumen(int dia,int hora){
        StringBuilder s=new StringBuilder();
        bloques[dia][hora].recorrer(r->{if(s.length()>0)s.append(" / ");s.append(r.clase().getNombre());});
        return s.toString();
    }
    public String detalle(int dia,int hora){
        StringBuilder s=new StringBuilder();
        bloques[dia][hora].recorrer(r->{var c=r.clase();s.append('#').append(c.getIdClase()).append(' ').append(c.getNombre()).append(" · ").append(c.getHoraInicio()).append("–").append(c.getHoraFin()).append("\nEntrenador: ").append(r.entrenador()).append("\nCupos: ").append(c.getCupoActual()).append('/').append(c.getCupoMaximo()).append("\n\n");});
        return s.length()==0?"Sin clases activas en este bloque.":s.toString();
    }
}
