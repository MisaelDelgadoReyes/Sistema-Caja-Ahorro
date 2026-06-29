package com.grupo5.caja_ahorro.controller;

import com.grupo5.caja_ahorro.request.TransaccionVentanillaRequest;
import com.grupo5.caja_ahorro.response.TransaccionResponse;
import com.grupo5.caja_ahorro.service.ContabilidadService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContabilidadControllerTest {

    @Mock
    private ContabilidadService contabilidadService;

    @InjectMocks
    private ContabilidadController contabilidadController;

    @Test
    void testEndpointTransaccion() {
        TransaccionVentanillaRequest req = new TransaccionVentanillaRequest();
        TransaccionResponse res = new TransaccionResponse(1L, "Exito", LocalDateTime.now());

        when(contabilidadService.procesarTransaccion(any())).thenReturn(res);

        // NOTA: Si esta línea falla, cambia "registrarTransaccion" por "procesarTransaccion"
        ResponseEntity<TransaccionResponse> responseEntity = contabilidadController.registrarTransaccion(req);

        assertNotNull(responseEntity);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assertEquals("Exito", responseEntity.getBody().getMensaje());
    }
}