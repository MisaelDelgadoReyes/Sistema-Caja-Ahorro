package com.grupo5.caja_ahorro.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PagoCuotaRequest {

    private BigDecimal montoPagado;
    private LocalDate fechaPago;

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }
}