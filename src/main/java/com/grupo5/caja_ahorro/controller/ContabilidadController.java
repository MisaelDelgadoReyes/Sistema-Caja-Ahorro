package com.grupo5.caja_ahorro.controller;

import com.grupo5.caja_ahorro.request.TransaccionVentanillaRequest;
import com.grupo5.caja_ahorro.response.TransaccionResponse;
import com.grupo5.caja_ahorro.service.ContabilidadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contabilidad")
@Tag(name = "Contabilidad y Ventanilla", description = "Módulo de transacciones de caja y libro diario")
public class ContabilidadController {

    private final ContabilidadService contabilidadService;

    public ContabilidadController(ContabilidadService contabilidadService) {
        this.contabilidadService = contabilidadService;
    }

    @PostMapping("/ventanilla/transaccion")
    @Operation(summary = "Registrar nueva transacción de ventanilla", description = "Procesa un depósito/retiro y genera automáticamente los asientos en el libro diario por partida doble.")
    public ResponseEntity<TransaccionResponse> registrarTransaccion(@RequestBody TransaccionVentanillaRequest request) {
        
        TransaccionResponse response = contabilidadService.procesarTransaccion(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}