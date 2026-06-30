package com.grupo5.caja_ahorro.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo5.caja_ahorro.model.Perfil;
import com.grupo5.caja_ahorro.response.ResponseRest;
import com.grupo5.caja_ahorro.service.IPerfilService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class PerfilRestControllerTest {

    private MockMvc mockMvc;

    private IPerfilService perfilService;

    @BeforeEach
    void setUp() {
        perfilService = Mockito.mock(IPerfilService.class);

        PerfilRestController controller = new PerfilRestController();

        try {
            var field = PerfilRestController.class.getDeclaredField("perfilService");
            field.setAccessible(true);
            field.set(controller, perfilService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void consultarPerfilesDebeRetornarOk() throws Exception {

        ResponseRest<Perfil> response = new ResponseRest<>();

        when(perfilService.buscarTodos())
                .thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        mockMvc.perform(get("/api/v1/perfiles/consultar"))
                .andExpect(status().isOk());

        verify(perfilService).buscarTodos();
    }

    @Test
    void crearPerfilDebeRetornarCreated() throws Exception {

        Perfil perfil = new Perfil();
        perfil.setNombre("Administrador");

        ResponseRest<Perfil> response = new ResponseRest<>();

        when(perfilService.crear(Mockito.any(Perfil.class)))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.CREATED));

        mockMvc.perform(post("/api/v1/perfiles/crear")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(perfil)))
                .andExpect(status().isCreated());

        verify(perfilService).crear(Mockito.any(Perfil.class));
    }
}