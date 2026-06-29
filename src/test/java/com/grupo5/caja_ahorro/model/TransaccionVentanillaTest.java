package com.grupo5.caja_ahorro.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class TransaccionVentanillaTest {

    @Test
    void testModeloTransaccion() {
        TransaccionVentanilla t = new TransaccionVentanilla();
        t.setId(1L);
        t.setSocioId(100L);
        t.setTipoTransaccion("DEPOSITO");
        t.setMonto(new BigDecimal("250.00"));
        t.setFechaHora(LocalDateTime.now());

        assertEquals(1L, t.getId());
        assertEquals(100L, t.getSocioId());
        assertEquals("DEPOSITO", t.getTipoTransaccion());
        assertEquals(new BigDecimal("250.00"), t.getMonto());
        assertNotNull(t.getFechaHora());
    }
}