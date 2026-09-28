package com.smartfit.gymApp.controller;

import com.smartfit.gymApp.model.Socio;
import com.smartfit.gymApp.service.SocioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/socios")
@CrossOrigin(origins = "*")
public class SocioController {

    @Autowired
    private SocioService socioService;

    @GetMapping
    public List<Socio> listar() {
        return socioService.listarTodos();
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Socio s) {
        if (socioService.existeDni(s.getDni())) {
            return ResponseEntity.badRequest().body("Ya existe un socio con ese DNI");
        }
        return ResponseEntity.ok(socioService.guardar(s));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Socio> buscar(@PathVariable Long id) {
        Socio s = socioService.buscarPorId(id);
        return s != null ? ResponseEntity.ok(s) : ResponseEntity.notFound().build();
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<Socio> buscarPorDni(@PathVariable String dni) {
        Socio s = socioService.buscarPorDni(dni);
        return s != null ? ResponseEntity.ok(s) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return socioService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    /**
     * Registrar socio consultando RENIEC.
     * POST /api/socios/registrar-por-dni/{dni}
     */
    @PostMapping("/registrar-por-dni/{dni}")
    public ResponseEntity<?> registrarPorDni(@PathVariable String dni) {
        try {
            Socio s = socioService.registrarPorDni(dni);
            return ResponseEntity.ok(s);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}