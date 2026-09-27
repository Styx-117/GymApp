package com.smartfit.gymApp.service;

import com.smartfit.gymApp.dto.ReniecData;
import com.smartfit.gymApp.model.Cliente;
import com.smartfit.gymApp.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ReniecService reniecService;

    public List<Cliente> listarTodos() { return clienteRepository.findAll(); }

    public Cliente guardar(Cliente c) { return clienteRepository.save(c); }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id).orElse(null);
    }

    public Cliente buscarPorDni(String dni) {
        return clienteRepository.findByDni(dni).orElse(null);
    }

    public boolean existeDni(String dni) { return clienteRepository.existsByDni(dni); }

    public boolean eliminar(Long id) {
        if (clienteRepository.existsById(id)) {
            clienteRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Registra un cliente consultando RENIEC por DNI.
     * Si ya existe ese DNI, devuelve el existente.
     */
    public Cliente registrarPorDni(String dni) {
        Cliente existente = clienteRepository.findByDni(dni).orElse(null);
        if (existente != null) {
            return existente;
        }

        ReniecData data = reniecService.consultarDni(dni);

        Cliente c = new Cliente();
        c.setDni(dni);
        c.setNombre(data.getNombres());
        c.setApellido(data.getApellidoPaterno() + " " + data.getApellidoMaterno());

        return clienteRepository.save(c);
    }
}