package com.grupo5.caja_ahorro.service;

import com.grupo5.caja_ahorro.model.Perfil;
import com.grupo5.caja_ahorro.repository.PerfilRepository;
import com.grupo5.caja_ahorro.response.ResponseRest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PerfilServiceImplTest {

    @Mock
    private PerfilRepository perfilRepository;

    @InjectMocks
    private PerfilServiceImpl perfilService;

    @Test
    void buscarTodosDebeRetornarLista() {

        when(perfilRepository.findAll())
                .thenReturn(Collections.singletonList(new Perfil()));

        ResponseEntity<ResponseRest<Perfil>> response =
                perfilService.buscarTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(perfilRepository).findAll();
    }

    @Test
    void buscarTodosDebeRetornarError() {

        when(perfilRepository.findAll())
                .thenThrow(new RuntimeException("Error"));

        ResponseEntity<ResponseRest<Perfil>> response =
                perfilService.buscarTodos();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode());

        verify(perfilRepository).findAll();
    }

    @Test
    void crearDebeGuardarPerfil() {

        Perfil perfil = new Perfil();

        when(perfilRepository.save(any(Perfil.class)))
                .thenReturn(perfil);

        ResponseEntity<ResponseRest<Perfil>> response =
                perfilService.crear(perfil);

        assertEquals(HttpStatus.CREATED,
                response.getStatusCode());

        verify(perfilRepository).save(perfil);
    }

    @Test
    void crearDebeRetornarError() {

        Perfil perfil = new Perfil();

        when(perfilRepository.save(any(Perfil.class)))
                .thenThrow(new RuntimeException("Error"));

        ResponseEntity<ResponseRest<Perfil>> response =
                perfilService.crear(perfil);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode());

        verify(perfilRepository).save(perfil);
    }
}