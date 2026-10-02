package com.smartfit.gymApp.service;

import com.smartfit.gymApp.model.Usuario;
import com.smartfit.gymApp.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> listarTodos() { 
        return usuarioRepository.findAll(); 
    }

    public Usuario guardar(Usuario u) { 
        return usuarioRepository.save(u); 
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario login(String nombreUsuario, String contrasena) {
        Usuario u = usuarioRepository.findByNombreUsuario(nombreUsuario).orElse(null);
        if (u != null && u.getContrasena().equals(contrasena)) {
            // Si tu tabla no usa estado, puedes quitar la validación de estado o dejarla si ya existe
            return u;
        }
        return null;
    }

    public boolean eliminar(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }
}