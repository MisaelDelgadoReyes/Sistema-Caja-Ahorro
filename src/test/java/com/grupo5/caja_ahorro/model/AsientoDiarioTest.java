package com.grupo5.caja_ahorro.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class AsientoDiarioTest {

    @Test
    void testModeloAsiento() {
        AsientoDiario a = new AsientoDiario();
        a.setId(1L);
        a.setFecha(LocalDateTime.now());
        a.setDescripcion("Deposito inicial");
        a.setCuentaContable("101-CAJA");
        a.setDebe(new BigDecimal("100.00"));
        a.setHaber(BigDecimal.ZERO);

        TransaccionVentanilla tv = new TransaccionVentanilla();
        tv.setId(10L);
        a.setTransaccionVentanilla(tv);

        assertEquals(1L, a.getId());
        assertNotNull(a.getFecha());
        assertEquals("Deposito inicial", a.getDescripcion());
        assertEquals("101-CAJA", a.getCuentaContable());
        assertEquals(new BigDecimal("100.00"), a.getDebe());
        assertEquals(BigDecimal.ZERO, a.getHaber());
        assertEquals(10L, a.getTransaccionVentanilla().getId());
    }
}