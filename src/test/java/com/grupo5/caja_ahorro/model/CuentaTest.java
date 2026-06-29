package com.grupo5.caja_ahorro.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    @Test
    void deberiaCrearCuentaConConstructorVacio() {

        Cuenta cuenta = new Cuenta();

        assertNotNull(cuenta);
    }

    @Test
    void deberiaAsignarYObtenerDatos() {

        Socio socio = new Socio();

        Cuenta cuenta = new Cuenta();

        cuenta.setIdCuenta(1L);
        cuenta.setNumeroCuenta("CTA-001");
        cuenta.setTipoCuenta(TipoCuenta.AHORRO);
        cuenta.setSaldo(new BigDecimal("250.50"));
        cuenta.setFechaApertura(LocalDate.of(2025,1,1));
        cuenta.setActiva(true);
        cuenta.setSocio(socio);

        assertEquals(1L, cuenta.getIdCuenta());
        assertEquals("CTA-001", cuenta.getNumeroCuenta());
        assertEquals(TipoCuenta.AHORRO, cuenta.getTipoCuenta());
        assertEquals(new BigDecimal("250.50"), cuenta.getSaldo());
        assertEquals(LocalDate.of(2025,1,1), cuenta.getFechaApertura());
        assertTrue(cuenta.getActiva());
        assertEquals(socio, cuenta.getSocio());
    }

    @Test
    void deberiaCrearCuentaConConstructorCompleto() {

        Socio socio = new Socio();

        LocalDate fecha = LocalDate.of(2025,1,1);

        Cuenta cuenta = new Cuenta(
                1L,
                "CTA-001",
                TipoCuenta.CORRIENTE,
                new BigDecimal("1000"),
                fecha,
                true,
                socio
        );

        assertEquals(1L, cuenta.getIdCuenta());
        assertEquals("CTA-001", cuenta.getNumeroCuenta());
        assertEquals(TipoCuenta.CORRIENTE, cuenta.getTipoCuenta());
        assertEquals(new BigDecimal("1000"), cuenta.getSaldo());
        assertEquals(fecha, cuenta.getFechaApertura());
        assertTrue(cuenta.getActiva());
        assertEquals(socio, cuenta.getSocio());
    }

    @Test
    void prePersistDebeAsignarValoresPorDefecto() {

        Cuenta cuenta = new Cuenta();

        cuenta.prePersist();

        assertNotNull(cuenta.getFechaApertura());
        assertEquals(BigDecimal.ZERO, cuenta.getSaldo());
        assertTrue(cuenta.getActiva());
    }

    @Test
    void prePersistNoDebeModificarValoresExistentes() {

        LocalDate fecha = LocalDate.of(2024,5,10);

        Cuenta cuenta = new Cuenta();

        cuenta.setFechaApertura(fecha);
        cuenta.setSaldo(new BigDecimal("500"));
        cuenta.setActiva(false);

        cuenta.prePersist();

        assertEquals(fecha, cuenta.getFechaApertura());
        assertEquals(new BigDecimal("500"), cuenta.getSaldo());
        assertFalse(cuenta.getActiva());
    }

}