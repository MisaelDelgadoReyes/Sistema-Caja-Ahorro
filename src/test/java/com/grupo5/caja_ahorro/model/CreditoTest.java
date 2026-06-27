package com.grupo5.caja_ahorro.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class CreditoTest {
@Test
void deberiaAsignarEstadoPendienteCuandoEsNull() {

    Credito credito = new Credito();

    credito.setMontoSolicitado(new BigDecimal("1000"));

    credito.prePersist();

    assertEquals(
            EstadoCredito.PENDIENTE,
            credito.getEstado());

}

@Test
void deberiaAsignarFechaSolicitudActual() {

	Credito credito = new Credito();

	credito.setMontoSolicitado(new BigDecimal("500"));

credito.prePersist();

assertEquals(
        LocalDate.now(),
        credito.getFechaSolicitud());
	
}

@Test
void deberiaInicializarSaldoPendiente() {

    Credito credito = new Credito();

    credito.setMontoSolicitado(new BigDecimal("2500"));

    credito.prePersist();

    assertEquals(
            new BigDecimal("2500"),
            credito.getSaldoPendiente());

}

@Test
void deberiaNoCambiarEstadoSiYaEstaAprobado() {

    Credito credito = new Credito();

    credito.setMontoSolicitado(new BigDecimal("1000"));

    credito.setEstado(EstadoCredito.APROBADO);

    credito.prePersist();

    assertEquals(
            EstadoCredito.APROBADO,
            credito.getEstado());

}

@Test
void agregarCuota() {
Credito credito = new Credito();

Cuota cuota = new Cuota();
credito.agregarCuota(cuota);
assertEquals(1, credito.getCuotas().size());
assertEquals(
        credito,
        cuota.getCredito());
}

}

