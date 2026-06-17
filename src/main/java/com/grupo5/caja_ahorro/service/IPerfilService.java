package com.grupo5.caja_ahorro.service;

import org.springframework.http.ResponseEntity;

import com.grupo5.caja_ahorro.model.Perfil;
import com.grupo5.caja_ahorro.response.ResponseRest;

public interface IPerfilService {
    ResponseEntity<ResponseRest<Perfil>> buscarTodos();
    ResponseEntity<ResponseRest<Perfil>> crear(Perfil perfil);
}