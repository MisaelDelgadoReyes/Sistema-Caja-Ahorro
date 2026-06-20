package com.grupo5.caja_ahorro.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CuotaAmortizacionResponse {

    private Integer numeroCuota;
    private LocalDate fechaVencimiento;
    private BigDecimal capital;
    private BigDecimal interes;
    private BigDecimal seguroDesgravamen;
    private BigDecimal valorCuota;
    private BigDecimal saldoCapital;

    public CuotaAmortizacionResponse() {
    }

    public CuotaAmortizacionResponse(
            Integer numeroCuota,
            LocalDate fechaVencimiento,
            BigDecimal capital,
            BigDecimal interes,
            BigDecimal seguroDesgravamen,
            BigDecimal valorCuota,
            BigDecimal saldoCapital
    ) {
        this.numeroCuota = numeroCuota;
        this.fechaVencimiento = fechaVencimiento;
        this.capital = capital;
        this.interes = interes;
        this.seguroDesgravamen = seguroDesgravamen;
        this.valorCuota = valorCuota;
        this.saldoCapital = saldoCapital;
    }

    public Integer getNumeroCuota() {
        return numeroCuota;
    }

    public void setNumeroCuota(Integer numeroCuota) {
        this.numeroCuota = numeroCuota;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public BigDecimal getCapital() {
        return capital;
    }

    public void setCapital(BigDecimal capital) {
        this.capital = capital;
    }

    public BigDecimal getInteres() {
        return interes;
    }

    public void setInteres(BigDecimal interes) {
        this.interes = interes;
    }

    public BigDecimal getSeguroDesgravamen() {
        return seguroDesgravamen;
    }

    public void setSeguroDesgravamen(BigDecimal seguroDesgravamen) {
        this.seguroDesgravamen = seguroDesgravamen;
    }

    public BigDecimal getValorCuota() {
        return valorCuota;
    }

    public void setValorCuota(BigDecimal valorCuota) {
        this.valorCuota = valorCuota;
    }

    public BigDecimal getSaldoCapital() {
        return saldoCapital;
    }

    public void setSaldoCapital(BigDecimal saldoCapital) {
        this.saldoCapital = saldoCapital;
    }
}