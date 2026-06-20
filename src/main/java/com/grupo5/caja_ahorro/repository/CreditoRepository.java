package com.grupo5.caja_ahorro.repository;

import com.grupo5.caja_ahorro.model.Credito;
import com.grupo5.caja_ahorro.model.EstadoCredito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditoRepository extends JpaRepository<Credito, Long> {

    List<Credito> findByCedulaSocioOrderByFechaSolicitudDesc(String cedulaSocio);

    List<Credito> findByEstado(EstadoCredito estado);
}