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
    public ResponseEntity<?> crear(@RequestBody Socio s) {
        if (s != null && s.getIdSocio() != null) {
            throw new IllegalArgumentException("El alta no acepta un identificador. Para actualizar un socio, usa PUT /api/socios/{id}.");
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
        return ResponseEntity.ok(socioService.registrarPorDni(dni));
    }

    @GetMapping("/pagina")
    public com.smartfit.gymApp.repository.PageResult<com.smartfit.gymApp.repository.SocioDAO.RegistroSocio> pagina(
            @RequestParam(defaultValue="") String buscar,@RequestParam(required=false) Boolean activo,
            @RequestParam(defaultValue="0") int pagina,@RequestParam(defaultValue="50") int tamanio) {
        return socioService.buscarPagina(buscar,activo,pagina,tamanio);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Socio> actualizar(@PathVariable Long id,@RequestBody Socio socio) {
        if(socio==null)throw new IllegalArgumentException("Faltan los datos del socio.");
        socio.setIdSocio(id);return ResponseEntity.ok(socioService.guardar(socio));
    }
}
