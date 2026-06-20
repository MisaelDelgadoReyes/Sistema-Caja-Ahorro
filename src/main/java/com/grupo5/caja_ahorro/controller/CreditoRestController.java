package com.grupo5.caja_ahorro.controller;

import com.grupo5.caja_ahorro.model.Credito;
import com.grupo5.caja_ahorro.model.Cuota;
import com.grupo5.caja_ahorro.request.AprobarCreditoRequest;
import com.grupo5.caja_ahorro.request.PagoCuotaRequest;
import com.grupo5.caja_ahorro.request.RechazarCreditoRequest;
import com.grupo5.caja_ahorro.request.SolicitudCreditoRequest;
import com.grupo5.caja_ahorro.response.CuotaAmortizacionResponse;
import com.grupo5.caja_ahorro.response.ResponseRest;
import com.grupo5.caja_ahorro.service.ICreditoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/creditos")
@Tag(name = "Creditos", description = "Operaciones del ciclo de vida del credito y amortizacion")
public class CreditoRestController {

    private final ICreditoService creditoService;

    public CreditoRestController(ICreditoService creditoService) {
        this.creditoService = creditoService;
    }

    @GetMapping("/consultar")
    @Operation(
            summary = "Consultar todos los creditos",
            description = "Devuelve todos los creditos registrados en el sistema."
    )
    public ResponseEntity<ResponseRest<Credito>> consultarTodos() {
        ResponseRest<Credito> response = new ResponseRest<>();

        try {
            response.setData(creditoService.consultarTodos());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return error(response, HttpStatus.INTERNAL_SERVER_ERROR, 500,
                    "Error al consultar los creditos", e.getMessage());
        }
    }

    @GetMapping("/consultar/{idCredito}")
    @Operation(
            summary = "Consultar credito por ID",
            description = "Busca un credito especifico mediante su identificador."
    )
    public ResponseEntity<ResponseRest<Credito>> consultarPorId(@PathVariable Long idCredito) {
        ResponseRest<Credito> response = new ResponseRest<>();

        try {
            response.addData(creditoService.consultarPorId(idCredito));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return error(response, HttpStatus.NOT_FOUND, 404,
                    "Credito no encontrado", e.getMessage());
        } catch (Exception e) {
            return error(response, HttpStatus.INTERNAL_SERVER_ERROR, 500,
                    "Error al consultar el credito", e.getMessage());
        }
    }

    @GetMapping("/socio/{cedulaSocio}")
    @Operation(
            summary = "Consultar creditos por socio",
            description = "Devuelve los creditos asociados a la cedula de un socio."
    )
    public ResponseEntity<ResponseRest<Credito>> consultarPorSocio(@PathVariable String cedulaSocio) {
        ResponseRest<Credito> response = new ResponseRest<>();

        try {
            response.setData(creditoService.consultarPorSocio(cedulaSocio));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return error(response, HttpStatus.BAD_REQUEST, 400,
                    "Datos invalidos", e.getMessage());
        } catch (Exception e) {
            return error(response, HttpStatus.INTERNAL_SERVER_ERROR, 500,
                    "Error al consultar creditos del socio", e.getMessage());
        }
    }

    @PostMapping("/simular")
    @Operation(
            summary = "Simular tabla de amortizacion",
            description = "Genera una tabla de amortizacion sin guardar el credito en la base de datos."
    )
    public ResponseEntity<ResponseRest<CuotaAmortizacionResponse>> simular(
            @RequestBody SolicitudCreditoRequest request
    ) {
        ResponseRest<CuotaAmortizacionResponse> response = new ResponseRest<>();

        try {
            response.setData(creditoService.simular(request));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return error(response, HttpStatus.BAD_REQUEST, 400,
                    "Datos invalidos para simular credito", e.getMessage());
        } catch (Exception e) {
            return error(response, HttpStatus.INTERNAL_SERVER_ERROR, 500,
                    "Error al simular el credito", e.getMessage());
        }
    }

    @PostMapping("/solicitar")
    @Operation(
            summary = "Registrar solicitud de credito",
            description = "Crea una solicitud de credito en estado PENDIENTE."
    )
    public ResponseEntity<ResponseRest<Credito>> solicitar(
            @RequestBody SolicitudCreditoRequest request
    ) {
        ResponseRest<Credito> response = new ResponseRest<>();

        try {
            response.addData(creditoService.solicitar(request));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return error(response, HttpStatus.BAD_REQUEST, 400,
                    "Datos invalidos para registrar la solicitud", e.getMessage());
        } catch (Exception e) {
            return error(response, HttpStatus.INTERNAL_SERVER_ERROR, 500,
                    "Error al registrar la solicitud de credito", e.getMessage());
        }
    }

    @PutMapping("/{idCredito}/aprobar")
    @Operation(
            summary = "Aprobar credito",
            description = "Aprueba una solicitud pendiente y genera su tabla de amortizacion."
    )
    public ResponseEntity<ResponseRest<Credito>> aprobar(
            @PathVariable Long idCredito,
            @RequestBody(required = false) AprobarCreditoRequest request
    ) {
        ResponseRest<Credito> response = new ResponseRest<>();

        try {
            response.addData(creditoService.aprobar(idCredito, request));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return error(response, HttpStatus.NOT_FOUND, 404,
                    "Credito no encontrado", e.getMessage());
        } catch (IllegalStateException e) {
            return error(response, HttpStatus.CONFLICT, 409,
                    "No se puede aprobar el credito", e.getMessage());
        } catch (Exception e) {
            return error(response, HttpStatus.INTERNAL_SERVER_ERROR, 500,
                    "Error al aprobar el credito", e.getMessage());
        }
    }

    @PutMapping("/{idCredito}/rechazar")
    @Operation(
            summary = "Rechazar credito",
            description = "Rechaza una solicitud de credito que se encuentre en estado PENDIENTE."
    )
    public ResponseEntity<ResponseRest<Credito>> rechazar(
            @PathVariable Long idCredito,
            @RequestBody(required = false) RechazarCreditoRequest request
    ) {
        ResponseRest<Credito> response = new ResponseRest<>();

        try {
            response.addData(creditoService.rechazar(idCredito, request));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return error(response, HttpStatus.NOT_FOUND, 404,
                    "Credito no encontrado", e.getMessage());
        } catch (IllegalStateException e) {
            return error(response, HttpStatus.CONFLICT, 409,
                    "No se puede rechazar el credito", e.getMessage());
        } catch (Exception e) {
            return error(response, HttpStatus.INTERNAL_SERVER_ERROR, 500,
                    "Error al rechazar el credito", e.getMessage());
        }
    }

    @PutMapping("/{idCredito}/desembolsar")
    @Operation(
            summary = "Desembolsar credito",
            description = "Cambia un credito APROBADO a VIGENTE, simulando la entrega del dinero al socio."
    )
    public ResponseEntity<ResponseRest<Credito>> desembolsar(@PathVariable Long idCredito) {
        ResponseRest<Credito> response = new ResponseRest<>();

        try {
            response.addData(creditoService.desembolsar(idCredito));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return error(response, HttpStatus.NOT_FOUND, 404,
                    "Credito no encontrado", e.getMessage());
        } catch (IllegalStateException e) {
            return error(response, HttpStatus.CONFLICT, 409,
                    "No se puede desembolsar el credito", e.getMessage());
        } catch (Exception e) {
            return error(response, HttpStatus.INTERNAL_SERVER_ERROR, 500,
                    "Error al desembolsar el credito", e.getMessage());
        }
    }

    @GetMapping("/{idCredito}/amortizacion")
    @Operation(
            summary = "Consultar amortizacion de credito",
            description = "Devuelve la tabla de cuotas generada para un credito."
    )
    public ResponseEntity<ResponseRest<Cuota>> consultarAmortizacion(@PathVariable Long idCredito) {
        ResponseRest<Cuota> response = new ResponseRest<>();

        try {
            response.setData(creditoService.consultarAmortizacion(idCredito));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return error(response, HttpStatus.NOT_FOUND, 404,
                    "Credito no encontrado", e.getMessage());
        } catch (Exception e) {
            return error(response, HttpStatus.INTERNAL_SERVER_ERROR, 500,
                    "Error al consultar la amortizacion", e.getMessage());
        }
    }

    @PostMapping("/cuotas/{idCuota}/pagar")
    @Operation(
            summary = "Pagar cuota",
            description = "Registra el pago total de una cuota de un credito vigente o en mora."
    )
    public ResponseEntity<ResponseRest<Cuota>> pagarCuota(
            @PathVariable Long idCuota,
            @RequestBody PagoCuotaRequest request
    ) {
        ResponseRest<Cuota> response = new ResponseRest<>();

        try {
            response.addData(creditoService.pagarCuota(idCuota, request));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return error(response, HttpStatus.BAD_REQUEST, 400,
                    "Datos invalidos para pagar la cuota", e.getMessage());
        } catch (IllegalStateException e) {
            return error(response, HttpStatus.CONFLICT, 409,
                    "No se puede pagar la cuota", e.getMessage());
        } catch (Exception e) {
            return error(response, HttpStatus.INTERNAL_SERVER_ERROR, 500,
                    "Error al pagar la cuota", e.getMessage());
        }
    }

    @PutMapping("/{idCredito}/marcar-mora")
    @Operation(
            summary = "Marcar credito en mora",
            description = "Marca como vencidas las cuotas atrasadas y cambia el credito a estado EN_MORA."
    )
    public ResponseEntity<ResponseRest<Credito>> marcarMora(@PathVariable Long idCredito) {
        ResponseRest<Credito> response = new ResponseRest<>();

        try {
            response.addData(creditoService.marcarMora(idCredito));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return error(response, HttpStatus.NOT_FOUND, 404,
                    "Credito no encontrado", e.getMessage());
        } catch (IllegalStateException e) {
            return error(response, HttpStatus.CONFLICT, 409,
                    "No se puede marcar en mora", e.getMessage());
        } catch (Exception e) {
            return error(response, HttpStatus.INTERNAL_SERVER_ERROR, 500,
                    "Error al marcar en mora el credito", e.getMessage());
        }
    }

    private <T> ResponseEntity<ResponseRest<T>> error(
            ResponseRest<T> response,
            HttpStatus status,
            int codigo,
            String mensaje,
            String campo
    ) {
        response.addError(codigo, mensaje, campo);
        return new ResponseEntity<>(response, status);
    }
}