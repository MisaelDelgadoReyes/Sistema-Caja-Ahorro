package com.grupo5.caja_ahorro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo5.caja_ahorro.model.Socio;
import com.grupo5.caja_ahorro.request.CrearSocioRequest;
import com.grupo5.caja_ahorro.service.ISocioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SocioRestControllerTest {

    private MockMvc mockMvc;
    private ISocioService socioService;

    @BeforeEach
    void setUp() {
        socioService = Mockito.mock(ISocioService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new SocioRestController(socioService))
                .build();
    }

    @Test
    void consultarTodosDebeRetornarOk() throws Exception {

        when(socioService.consultarTodos()).thenReturn(List.of(new Socio()));

        mockMvc.perform(get("/api/v1/socios/consultar"))
                .andExpect(status().isOk());
    }

    @Test
    void consultarTodosDebeRetornarError() throws Exception {

        when(socioService.consultarTodos())
                .thenThrow(new RuntimeException("error"));

        mockMvc.perform(get("/api/v1/socios/consultar"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void consultarPorIdDebeRetornarOk() throws Exception {

        when(socioService.consultarPorId(anyLong()))
                .thenReturn(new Socio());

        mockMvc.perform(get("/api/v1/socios/1"))
                .andExpect(status().isOk());
    }

    @Test
    void consultarPorIdDebeRetornarNotFound() throws Exception {

        when(socioService.consultarPorId(anyLong()))
                .thenThrow(new RuntimeException("No existe"));

        mockMvc.perform(get("/api/v1/socios/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void consultarPorCedulaDebeRetornarOk() throws Exception {

        when(socioService.consultarPorCedula(anyString()))
                .thenReturn(new Socio());

        mockMvc.perform(get("/api/v1/socios/cedula/0102030405"))
                .andExpect(status().isOk());
    }

    @Test
    void consultarPorCedulaDebeRetornarNotFound() throws Exception {

        when(socioService.consultarPorCedula(anyString()))
                .thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/v1/socios/cedula/0102030405"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crearDebeRetornarCreated() throws Exception {

        when(socioService.crear(any(CrearSocioRequest.class)))
                .thenReturn(new Socio());

        mockMvc.perform(post("/api/v1/socios/crear")
                        .contentType(APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new CrearSocioRequest())))
                .andExpect(status().isCreated());
    }

    @Test
    void crearDebeRetornarBadRequest() throws Exception {

        when(socioService.crear(any(CrearSocioRequest.class)))
                .thenThrow(new RuntimeException());

        mockMvc.perform(post("/api/v1/socios/crear")
                        .contentType(APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new CrearSocioRequest())))
                .andExpect(status().isBadRequest());
    }
}