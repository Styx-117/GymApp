package com.smartfit.gymApp.controller;

import com.smartfit.gymApp.model.Cliente;
import com.smartfit.gymApp.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public List<Cliente> listar() { return clienteService.listarTodos(); }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Cliente c) {
        if (clienteService.existeDni(c.getDni())) {
            return ResponseEntity.badRequest().body("Ya existe un cliente con ese DNI");
        }
        return ResponseEntity.ok(clienteService.guardar(c));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscar(@PathVariable Long id) {
        Cliente c = clienteService.buscarPorId(id);
        return c != null ? ResponseEntity.ok(c) : ResponseEntity.notFound().build();
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<Cliente> buscarPorDni(@PathVariable String dni) {
        Cliente c = clienteService.buscarPorDni(dni);
        return c != null ? ResponseEntity.ok(c) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return clienteService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    /**
     * Registra un cliente consultando RENIEC automáticamente por DNI.
     * POST /api/clientes/registrar-por-dni/{dni}
     */
    @PostMapping("/registrar-por-dni/{dni}")
    public ResponseEntity<?> registrarPorDni(@PathVariable String dni) {
        try {
            Cliente c = clienteService.registrarPorDni(dni);
            return ResponseEntity.ok(c);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}