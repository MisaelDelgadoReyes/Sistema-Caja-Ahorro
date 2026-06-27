package com.grupo5.caja_ahorro.repository;

import com.grupo5.caja_ahorro.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);

    List<Cuenta> findBySocioCedula(String cedula);

}