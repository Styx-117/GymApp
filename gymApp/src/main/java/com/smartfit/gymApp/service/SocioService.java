package com.smartfit.gymApp.service;

import com.smartfit.gymApp.dto.ReniecData;
import com.smartfit.gymApp.model.Socio;
import com.smartfit.gymApp.repository.SocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SocioService {

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private ReniecService reniecService;

    public List<Socio> listarTodos() {
        return socioRepository.findAll();
    }

    public Socio guardar(Socio s) {
        return socioRepository.save(s);
    }

    public Socio buscarPorId(Long id) {
        return socioRepository.findById(id).orElse(null);
    }

    public Socio buscarPorDni(String dni) {
        return socioRepository.findByDni(dni).orElse(null);
    }

    public boolean existeDni(String dni) {
        return socioRepository.existsByDni(dni);
    }

    public boolean eliminar(Long id) {
        if (socioRepository.existsById(id)) {
            socioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Genera el siguiente código de socio: SM-0001, SM-0002, ...
     */
    private String generarCodigoSocio() {
        long total = socioRepository.count() + 1;
        return String.format("SM-%04d", total);
    }

    /**
     * Registra un socio consultando RENIEC por DNI.
     * Si ya existe, devuelve el existente.
     */
    public Socio registrarPorDni(String dni) {
        Socio existente = socioRepository.findByDni(dni).orElse(null);
        if (existente != null) {
            return existente;
        }

        ReniecData data = reniecService.consultarDni(dni);

        Socio s = new Socio();
        s.setDni(dni);
        s.setCodigoSocio(generarCodigoSocio());
        s.setNombre(data.getNombres());
        s.setApellido(data.getApellidoPaterno() + " " + data.getApellidoMaterno());
        s.setActivo(true);

        return socioRepository.save(s);
    }
}