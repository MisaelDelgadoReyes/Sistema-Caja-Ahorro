package com.grupo5.caja_ahorro.request;

import com.grupo5.caja_ahorro.model.TipoCuenta;

public class CrearCuentaRequest {

    private String cedulaSocio;
    private TipoCuenta tipoCuenta;

    public String getCedulaSocio() {
        return cedulaSocio;
    }

    public void setCedulaSocio(String cedulaSocio) {
        this.cedulaSocio = cedulaSocio;
    }

    public TipoCuenta getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(TipoCuenta tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }
}