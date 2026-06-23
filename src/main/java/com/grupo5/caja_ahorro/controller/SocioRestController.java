package com.grupo5.caja_ahorro.controller;

import com.grupo5.caja_ahorro.model.Socio;
import com.grupo5.caja_ahorro.request.CrearSocioRequest;
import com.grupo5.caja_ahorro.response.ResponseRest;
import com.grupo5.caja_ahorro.service.ISocioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/socios")
@Tag(name = "Socios", description = "Gestión de socios")
public class SocioRestController {

    private final ISocioService socioService;

    public SocioRestController(ISocioService socioService) {
        this.socioService = socioService;
    }

    @GetMapping("/consultar")
    public ResponseEntity<ResponseRest<Socio>> consultarTodos() {

        ResponseRest<Socio> response = new ResponseRest<>();

        try {
            response.setData(socioService.consultarTodos());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.addError(500,"Error","No fue posible consultar socios");
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{idSocio}")
    public ResponseEntity<ResponseRest<Socio>> consultarPorId(
            @PathVariable Long idSocio
    ) {

        ResponseRest<Socio> response = new ResponseRest<>();

        try {
            response.addData(
                    socioService.consultarPorId(idSocio)
            );

            return new ResponseEntity<>(response,HttpStatus.OK);

        } catch (Exception e) {

            response.addError(
                    404,
                    "Socio no encontrado",
                    e.getMessage()
            );

            return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/cedula/{cedula}")
    public ResponseEntity<ResponseRest<Socio>> consultarPorCedula(
            @PathVariable String cedula
    ) {

        ResponseRest<Socio> response = new ResponseRest<>();

        try {

            response.addData(
                    socioService.consultarPorCedula(cedula)
            );

            return new ResponseEntity<>(response,HttpStatus.OK);

        } catch (Exception e) {

            response.addError(
                    404,
                    "Socio no encontrado",
                    e.getMessage()
            );

            return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<ResponseRest<Socio>> crear(
            @RequestBody CrearSocioRequest request
    ) {

        ResponseRest<Socio> response = new ResponseRest<>();

        try {

            response.addData(
                    socioService.crear(request)
            );

            return new ResponseEntity<>(response,HttpStatus.CREATED);

        } catch (Exception e) {

            response.addError(
                    400,
                    "Error al crear socio",
                    e.getMessage()
            );

            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
    }
}