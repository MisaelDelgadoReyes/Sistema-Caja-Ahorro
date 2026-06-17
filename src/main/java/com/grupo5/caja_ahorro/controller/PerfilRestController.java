package com.grupo5.caja_ahorro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grupo5.caja_ahorro.model.Perfil;
import com.grupo5.caja_ahorro.response.ResponseRest;
import com.grupo5.caja_ahorro.service.IPerfilService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/perfiles")
@Tag(name = "Perfiles", description = "Operaciones relacionadas con los roles y perfiles del sistema")
public class PerfilRestController {

    @Autowired
    private IPerfilService perfilService;

    @GetMapping("/consultar")
    @Operation(summary = "Obtener todos los perfiles", description = "Devuelve una lista con todos los perfiles registrados en la base de datos.")
    public ResponseEntity<ResponseRest<Perfil>> consultarPerfiles() {
        return perfilService.buscarTodos();
    }

    @PostMapping("/crear")
    @Operation(summary = "Crear un nuevo perfil", description = "Crea un nuevo rol (ej. Administrador, Cajero) en el sistema.")
    public ResponseEntity<ResponseRest<Perfil>> crearPerfil(@RequestBody Perfil perfil) {
        return perfilService.crear(perfil);
    }
}