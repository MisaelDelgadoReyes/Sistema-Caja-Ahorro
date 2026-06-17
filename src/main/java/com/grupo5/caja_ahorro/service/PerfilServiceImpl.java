package com.grupo5.caja_ahorro.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo5.caja_ahorro.model.Perfil;
import com.grupo5.caja_ahorro.repository.PerfilRepository;
import com.grupo5.caja_ahorro.response.ResponseRest;

@Service
public class PerfilServiceImpl implements IPerfilService {

    @Autowired
    private PerfilRepository perfilRepository;

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseRest<Perfil>> buscarTodos() {
        ResponseRest<Perfil> response = new ResponseRest<>();
        try {
            List<Perfil> perfiles = perfilRepository.findAll();
            response.setData(perfiles);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.addError(500, "Error al consultar la base de datos", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseRest<Perfil>> crear(Perfil perfil) {
        ResponseRest<Perfil> response = new ResponseRest<>();
        try {
            Perfil perfilGuardado = perfilRepository.save(perfil);
            response.addData(perfilGuardado);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.addError(500, "Error al guardar el perfil", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}