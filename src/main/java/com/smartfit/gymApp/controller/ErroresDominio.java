package com.smartfit.gymApp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

/** Errores de validacion compartidos sin exponer SQL ni detalles de conexion. */
@RestControllerAdvice
public class ErroresDominio {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> validacion(IllegalArgumentException error){return ResponseEntity.badRequest().body(Map.of("error",error.getMessage()==null?"Revisa los datos enviados.":error.getMessage()));}
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> indisponible(IllegalStateException error){return ResponseEntity.status(503).body(Map.of("error","No se pudo completar la operacion. Intenta nuevamente."));}
}
