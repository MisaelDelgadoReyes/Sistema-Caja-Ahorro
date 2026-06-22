package com.grupo5.caja_ahorro.repository;

import com.grupo5.caja_ahorro.model.Socio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocioRepository extends JpaRepository<Socio, Long> {

    Optional<Socio> findByCedula(String cedula);

}