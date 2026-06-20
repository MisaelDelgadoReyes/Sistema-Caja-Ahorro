package com.grupo5.caja_ahorro.repository;

import com.grupo5.caja_ahorro.model.Cuota;
import com.grupo5.caja_ahorro.model.EstadoCuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CuotaRepository extends JpaRepository<Cuota, Long> {

    List<Cuota> findByCredito_IdCreditoOrderByNumeroCuotaAsc(Long idCredito);

    List<Cuota> findByCredito_IdCreditoAndEstadoOrderByNumeroCuotaAsc(
            Long idCredito,
            EstadoCuota estado
    );

    boolean existsByCredito_IdCreditoAndEstado(Long idCredito, EstadoCuota estado);

    boolean existsByCredito_IdCreditoAndEstadoAndFechaVencimientoBefore(
            Long idCredito,
            EstadoCuota estado,
            LocalDate fechaActual
    );
}