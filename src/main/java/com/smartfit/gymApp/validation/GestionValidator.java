package com.smartfit.gymApp.validation;

import com.smartfit.gymApp.model.Equipo;
import java.sql.Time;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Set;

/** Reglas compartidas por servicios y entradas JDBC compatibles. No consulta la BD. */
public final class GestionValidator {
    private static final Set<String> DIAS=Set.of("LUNES","MARTES","MIERCOLES","JUEVES","VIERNES","SABADO","DOMINGO");
    private static final Set<String> ESTADOS_EQUIPO=Set.of("OPERATIVO","EN_MANTENIMIENTO","FUERA_DE_SERVICIO");
    private GestionValidator() {}
    public static void id(int id,String nombre) { if(id<=0)throw new IllegalArgumentException("Selecciona "+nombre+" válido."); }
    public static String requerido(String value,int max,String nombre) {
        if(value==null||value.isBlank()||value.trim().length()>max)throw new IllegalArgumentException(nombre+" debe tener entre 1 y "+max+" caracteres.");
        return value.trim();
    }
    public static void opcional(String value,int max,String nombre) {
        if(value!=null&&value.trim().length()>max)throw new IllegalArgumentException(nombre+" admite hasta "+max+" caracteres.");
    }
    public static String dia(String value) {
        String normal=value==null?"":Normalizer.normalize(value.trim().toUpperCase(Locale.ROOT),Normalizer.Form.NFD).replaceAll("\\p{M}","");
        if(!DIAS.contains(normal))throw new IllegalArgumentException("Selecciona un día válido.");return normal;
    }
    public static void clase(String nombre,int entrenador,String dia,Time inicio,Time fin,int cupo) {
        requerido(nombre,100,"El nombre de la clase");id(entrenador,"un entrenador");dia(dia);
        if(inicio==null||fin==null||!inicio.before(fin))throw new IllegalArgumentException("La hora final debe ser posterior al inicio.");
        if(cupo<=0)throw new IllegalArgumentException("El cupo debe ser mayor que cero.");
    }
    public static void estadoEquipo(String estado) {
        if(estado==null||!ESTADOS_EQUIPO.contains(estado))throw new IllegalArgumentException("Selecciona un estado de equipo válido.");
    }
    public static void equipo(Equipo equipo) {
        if(equipo==null)throw new IllegalArgumentException("Completa los datos del equipo.");
        requerido(equipo.getCodigoEquipo(),20,"El código");requerido(equipo.getNombre(),100,"El nombre");
        opcional(equipo.getTipo(),50,"El tipo");opcional(equipo.getUbicacion(),100,"La ubicación");estadoEquipo(equipo.getEstado());
    }
    public static void incidencia(String equipo,String descripcion,int prioridad) {
        requerido(equipo,20,"El código del equipo");requerido(descripcion,200,"La descripción");
        if(prioridad<1||prioridad>3)throw new IllegalArgumentException("Selecciona una prioridad válida.");
    }
    public static void codigoSocio(String codigo) { requerido(codigo,20,"El código del socio"); }
}
