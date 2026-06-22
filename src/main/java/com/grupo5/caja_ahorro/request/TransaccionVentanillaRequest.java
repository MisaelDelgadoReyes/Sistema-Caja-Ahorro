package com.grupo5.caja_ahorro.request;

import java.math.BigDecimal;

public class TransaccionVentanillaRequest {
    
    private Long socioId;
    private String tipoTransaccion; 
    private BigDecimal monto;
    private String cuentaContable; 

   
    public Long getSocioId() { return socioId; }
    public void setSocioId(Long socioId) { this.socioId = socioId; }

    public String getTipoTransaccion() { return tipoTransaccion; }
    public void setTipoTransaccion(String tipoTransaccion) { this.tipoTransaccion = tipoTransaccion; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getCuentaContable() { return cuentaContable; }
    public void setCuentaContable(String cuentaContable) { this.cuentaContable = cuentaContable; }
}