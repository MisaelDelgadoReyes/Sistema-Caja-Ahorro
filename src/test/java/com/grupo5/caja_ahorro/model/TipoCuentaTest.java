package com.grupo5.caja_ahorro.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TipoCuentaTest {

    @Test
    void debeExistirTipoAhorro() {
        assertEquals(TipoCuenta.AHORRO, TipoCuenta.valueOf("AHORRO"));
    }

    @Test
    void debeExistirTipoCorriente() {
        assertEquals(TipoCuenta.CORRIENTE, TipoCuenta.valueOf("CORRIENTE"));
    }

    @Test
    void debeTenerDosValores() {
        assertEquals(2, TipoCuenta.values().length);
    }

    @Test
    void primerValorDebeSerAhorro() {
        assertEquals(TipoCuenta.AHORRO, TipoCuenta.values()[0]);
    }

    @Test
    void segundoValorDebeSerCorriente() {
        assertEquals(TipoCuenta.CORRIENTE, TipoCuenta.values()[1]);
    }
}