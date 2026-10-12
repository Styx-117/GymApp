package com.smartfit.gymApp.structures.arreglos;

import com.smartfit.gymApp.model.Clase;

/**
 * Matriz [día][hora] que ubica cada clase en su bloque horario (RF12).
 * Días: 0=Lunes ... 6=Domingo. Horas: 0..23 (bloques enteros).
 */
public class MatrizHorarios {
    private static final int DIAS = 7;
    private static final int HORAS = 24;
    private final Clase[][] matriz = new Clase[DIAS][HORAS];

    public boolean asignar(Clase c) {
        int d = dia(c.getDiaSemana());
        int h = c.getHoraInicio().getHour();
        if (d < 0 || h < 0 || h >= HORAS) return false;
        if (matriz[d][h] != null) return false;   // choque de horario
        matriz[d][h] = c;
        return true;
    }

    public Clase obtener(String dia, int hora) {
        int d = dia(dia);
        return (d < 0 || hora < 0 || hora >= HORAS) ? null : matriz[d][hora];
    }

    public void limpiar() {
        for (Clase[] fila : matriz) java.util.Arrays.fill(fila, null);
    }

    private int dia(String nombre) {
        if (nombre == null) return -1;
        switch (nombre.trim().toUpperCase()) {
            case "LUNES":     return 0;
            case "MARTES":    return 1;
            case "MIERCOLES": case "MIÉRCOLES": return 2;
            case "JUEVES":    return 3;
            case "VIERNES":   return 4;
            case "SABADO":    case "SÁBADO":    return 5;
            case "DOMINGO":   return 6;
            default:          return -1;
        }
    }
}