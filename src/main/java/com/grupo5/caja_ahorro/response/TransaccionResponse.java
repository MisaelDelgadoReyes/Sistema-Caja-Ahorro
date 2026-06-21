package com.grupo5.caja_ahorro.response;

import java.time.LocalDateTime;

public class TransaccionResponse {
    
    private Long transaccionId;
    private String mensaje;
    private LocalDateTime fecha;

    
    public TransaccionResponse(Long transaccionId, String mensaje, LocalDateTime fecha) {
        this.transaccionId = transaccionId;
        this.mensaje = mensaje;
        this.fecha = fecha;
    }

    
    public Long getTransaccionId() { return transaccionId; }
    public String getMensaje() { return mensaje; }
    public LocalDateTime getFecha() { return fecha; }
}