package com.grupo5.caja_ahorro.controller;

import com.grupo5.caja_ahorro.model.Cuenta;
import com.grupo5.caja_ahorro.request.CrearCuentaRequest;
import com.grupo5.caja_ahorro.response.ResponseRest;
import com.grupo5.caja_ahorro.service.ICuentaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cuentas")
@Tag(name = "Cuentas", description = "Gestión de cuentas")
public class CuentaRestController {

    private final ICuentaService cuentaService;

    public CuentaRestController(ICuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @GetMapping("/consultar")
    public ResponseEntity<ResponseRest<Cuenta>> consultarTodas() {

        ResponseRest<Cuenta> response = new ResponseRest<>();

        try {

            response.setData(
                    cuentaService.consultarTodas()
            );

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {

            response.addError(
                    500,
                    "Error",
                    "No fue posible consultar las cuentas"
            );

            return new ResponseEntity<>(
                    response,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/{idCuenta}")
    public ResponseEntity<ResponseRest<Cuenta>> consultarPorId(
            @PathVariable Long idCuenta
    ) {

        ResponseRest<Cuenta> response = new ResponseRest<>();

        try {

            response.addData(
                    cuentaService.consultarPorId(idCuenta)
            );

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {

            response.addError(
                    404,
                    "Cuenta no encontrada",
                    e.getMessage()
            );

            return new ResponseEntity<>(
                    response,
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @GetMapping("/numero/{numeroCuenta}")
    public ResponseEntity<ResponseRest<Cuenta>> consultarPorNumeroCuenta(
            @PathVariable String numeroCuenta
    ) {

        ResponseRest<Cuenta> response = new ResponseRest<>();

        try {

            response.addData(
                    cuentaService.consultarPorNumeroCuenta(numeroCuenta)
            );

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {

            response.addError(
                    404,
                    "Cuenta no encontrada",
                    e.getMessage()
            );

            return new ResponseEntity<>(
                    response,
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @GetMapping("/socio/{cedula}")
    public ResponseEntity<ResponseRest<Cuenta>> consultarPorSocio(
            @PathVariable String cedula
    ) {

        ResponseRest<Cuenta> response = new ResponseRest<>();

        try {

            response.setData(
                    cuentaService.consultarPorSocio(cedula)
            );

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {

            response.addError(
                    400,
                    "Error",
                    e.getMessage()
            );

            return new ResponseEntity<>(
                    response,
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<ResponseRest<Cuenta>> crear(
            @RequestBody CrearCuentaRequest request
    ) {

        ResponseRest<Cuenta> response = new ResponseRest<>();

        try {

            response.addData(
                    cuentaService.crear(request)
            );

            return new ResponseEntity<>(
                    response,
                    HttpStatus.CREATED
            );

        } catch (Exception e) {

            response.addError(
                    400,
                    "Error al crear cuenta",
                    e.getMessage()
            );

            return new ResponseEntity<>(
                    response,
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}