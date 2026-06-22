package com.grupo5.caja_ahorro.repository;

import com.grupo5.caja_ahorro.model.AsientoDiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsientoDiarioRepository extends JpaRepository<AsientoDiario, Long> {
}