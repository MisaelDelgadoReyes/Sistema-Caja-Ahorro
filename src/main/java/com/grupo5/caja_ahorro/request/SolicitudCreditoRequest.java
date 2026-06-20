package com.grupo5.caja_ahorro.request;

import com.grupo5.caja_ahorro.model.SistemaAmortizacion;

import java.math.BigDecimal;

public class SolicitudCreditoRequest {

    private String cedulaSocio;
    private String numeroCuentaDesembolso;
    private BigDecimal montoSolicitado;
    private Integer plazoMeses;
    private BigDecimal tasaInteresAnual;
    private BigDecimal seguroDesgravamen;
    private SistemaAmortizacion sistemaAmortizacion;
    private String comentarioOficial;

    public String getCedulaSocio() {
        return cedulaSocio;
    }

    public void setCedulaSocio(String cedulaSocio) {
        this.cedulaSocio = cedulaSocio;
    }

    public String getNumeroCuentaDesembolso() {
        return numeroCuentaDesembolso;
    }

    public void setNumeroCuentaDesembolso(String numeroCuentaDesembolso) {
        this.numeroCuentaDesembolso = numeroCuentaDesembolso;
    }

    public BigDecimal getMontoSolicitado() {
        return montoSolicitado;
    }

    public void setMontoSolicitado(BigDecimal montoSolicitado) {
        this.montoSolicitado = montoSolicitado;
    }

    public Integer getPlazoMeses() {
        return plazoMeses;
    }

    public void setPlazoMeses(Integer plazoMeses) {
        this.plazoMeses = plazoMeses;
    }

    public BigDecimal getTasaInteresAnual() {
        return tasaInteresAnual;
    }

    public void setTasaInteresAnual(BigDecimal tasaInteresAnual) {
        this.tasaInteresAnual = tasaInteresAnual;
    }

    public BigDecimal getSeguroDesgravamen() {
        return seguroDesgravamen;
    }

    public void setSeguroDesgravamen(BigDecimal seguroDesgravamen) {
        this.seguroDesgravamen = seguroDesgravamen;
    }

    public SistemaAmortizacion getSistemaAmortizacion() {
        return sistemaAmortizacion;
    }

    public void setSistemaAmortizacion(SistemaAmortizacion sistemaAmortizacion) {
        this.sistemaAmortizacion = sistemaAmortizacion;
    }

    public String getComentarioOficial() {
        return comentarioOficial;
    }

    public void setComentarioOficial(String comentarioOficial) {
        this.comentarioOficial = comentarioOficial;
    }
}