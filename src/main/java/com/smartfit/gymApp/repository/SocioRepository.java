package com.smartfit.gymApp.repository;

import com.smartfit.gymApp.model.Socio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocioRepository extends JpaRepository<Socio, Long> {
    Optional<Socio> findByDni(String dni);
    Optional<Socio> findByCodigoSocio(String codigoSocio);
    boolean existsByDni(String dni);
    boolean existsByCodigoSocio(String codigoSocio);
}