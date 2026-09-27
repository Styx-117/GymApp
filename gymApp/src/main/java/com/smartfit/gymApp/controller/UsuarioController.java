package com.smartfit.gymApp.controller;

import com.smartfit.gymApp.model.Usuario;
import com.smartfit.gymApp.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> listar() { return usuarioService.listarTodos(); }

    @PostMapping
    public Usuario crear(@Valid @RequestBody Usuario u) {
        return usuarioService.guardar(u);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscar(@PathVariable Long id) {
        Usuario u = usuarioService.buscarPorId(id);
        return u != null ? ResponseEntity.ok(u) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return usuarioService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> cred) {
        Usuario u = usuarioService.login(cred.get("nombreUsuario"), cred.get("contrasena"));
        return u != null
                ? ResponseEntity.ok(u)
                : ResponseEntity.status(401).body("Credenciales inválidas");
    }
}