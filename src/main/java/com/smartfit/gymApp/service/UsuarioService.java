package com.smartfit.gymApp.service;

import com.smartfit.gymApp.model.Usuario;
import com.smartfit.gymApp.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository repository;
    public UsuarioService(){this(new UsuarioRepository());}
    @Autowired public UsuarioService(UsuarioRepository repository){this.repository=repository;}
    public List<Usuario> listarTodos(){return repository.findAll();}
    public Usuario guardar(Usuario usuario){
        if(usuario==null)throw new IllegalArgumentException("Faltan los datos del usuario.");
        usuario.setNombre(ValidacionPersona.requerido(usuario.getNombre(),"El usuario",50));
        if(usuario.getPassword()==null||usuario.getPassword().isBlank()||usuario.getPassword().length()>255)throw new IllegalArgumentException("La contrasena debe tener entre 1 y 255 caracteres.");
        return repository.save(usuario);
    }
    public Usuario buscarPorId(Long id){if(id==null||id<=0)throw new IllegalArgumentException("El identificador debe ser positivo.");return repository.findById(id).orElse(null);}
    public Usuario login(String nombre,String contrasena){
        if(nombre==null||nombre.isBlank()||contrasena==null||contrasena.isBlank())return null;
        if(nombre.trim().length()>50||contrasena.length()>255)return null;
        // La contrasena se compara literalmente, sin eliminar sus espacios.
        Usuario usuario=repository.autenticar(nombre.trim(),contrasena);
        return usuario!=null&&!Boolean.FALSE.equals(usuario.getEstado())?usuario:null;
    }
    public boolean eliminar(Long id){if(buscarPorId(id)==null)return false;repository.deleteById(id);return true;}
}
