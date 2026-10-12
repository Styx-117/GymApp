package com.smartfit.gymApp.controller;

import com.smartfit.gymApp.dto.ReniecData;
import com.smartfit.gymApp.service.ReniecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;

@RestController
@RequestMapping("/api/reniec")
@CrossOrigin(origins = "*")
public class ReniecController {

    @Autowired
    private ReniecService reniecService;

    @GetMapping("/dni/{dni}")
    public ResponseEntity<?> consultarDni(@PathVariable String dni) {
        try {
            ReniecData data = reniecService.consultarDni(dni);
            return ResponseEntity.ok(data);

        } catch (IllegalArgumentException e) {
            // DNI con formato inválido → 400
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (IllegalStateException e) {
            // Falta configuración (token, URL) → 503
            return ResponseEntity.status(503).body(e.getMessage());

        } catch (HttpStatusCodeException e) {
            // El proveedor externo (Decolecta) devolvió un error HTTP.
            // Propagamos el código real y el cuerpo para diagnosticar bien.
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body("RENIEC externo ["
                        + e.getStatusCode().value()
                        + "]: "
                        + e.getResponseBodyAsString());

        } catch (RuntimeException e) {
            // Cualquier otro fallo → 502 Bad Gateway (no 404)
            return ResponseEntity
                    .status(502)
                    .body("Error al consultar RENIEC: " + e.getMessage());
        }
    }
}