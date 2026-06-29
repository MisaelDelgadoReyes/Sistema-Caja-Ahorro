package com.grupo5.caja_ahorro.service;

import com.grupo5.caja_ahorro.model.AsientoDiario;
import com.grupo5.caja_ahorro.model.TransaccionVentanilla;
import com.grupo5.caja_ahorro.repository.AsientoDiarioRepository;
import com.grupo5.caja_ahorro.repository.TransaccionVentanillaRepository;
import com.grupo5.caja_ahorro.request.TransaccionVentanillaRequest;
import com.grupo5.caja_ahorro.response.TransaccionResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContabilidadServiceImplTest {

    @Mock
    private TransaccionVentanillaRepository ventanillaRepo;

    @Mock
    private AsientoDiarioRepository diarioRepo;

    @InjectMocks
    private ContabilidadServiceImpl contabilidadService;

    @Test
    void testProcesarTransaccionExitosa() {
        TransaccionVentanillaRequest request = new TransaccionVentanillaRequest();
        request.setSocioId(1L);
        request.setTipoTransaccion("DEPOSITO");
        request.setMonto(new BigDecimal("150.00"));
        request.setCuentaContable("CAJA");

        TransaccionVentanilla guardada = new TransaccionVentanilla();
        guardada.setId(10L);
        guardada.setFechaHora(LocalDateTime.now());

        when(ventanillaRepo.save(any(TransaccionVentanilla.class))).thenReturn(guardada);

        TransaccionResponse respuesta = contabilidadService.procesarTransaccion(request);

        assertNotNull(respuesta);
        assertEquals(10L, respuesta.getTransaccionId());
        assertEquals("Transacción y asientos contables registrados con éxito", respuesta.getMensaje());
        verify(ventanillaRepo, times(1)).save(any(TransaccionVentanilla.class));
        verify(diarioRepo, times(2)).save(any(AsientoDiario.class)); 
    }
}