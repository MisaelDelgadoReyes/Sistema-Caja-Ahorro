package com.grupo5.caja_ahorro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo5.caja_ahorro.model.Cuenta;
import com.grupo5.caja_ahorro.model.TipoCuenta;
import com.grupo5.caja_ahorro.request.CrearCuentaRequest;
import com.grupo5.caja_ahorro.service.ICuentaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CuentaRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class CuentaRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ICuentaService cuentaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Cuenta crearCuenta() {
        Cuenta cuenta = new Cuenta();
        cuenta.setIdCuenta(1L);
        cuenta.setNumeroCuenta("001000001");
        cuenta.setSaldo(BigDecimal.valueOf(500));
        cuenta.setTipoCuenta(TipoCuenta.AHORRO);
        return cuenta;
    }

    @Test
    @DisplayName("Consultar todas las cuentas OK")
    void consultarTodasOk() throws Exception {

        when(cuentaService.consultarTodas())
                .thenReturn(List.of(crearCuenta()));

        mockMvc.perform(get("/api/v1/cuentas/consultar"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Consultar todas las cuentas ERROR")
    void consultarTodasError() throws Exception {

        when(cuentaService.consultarTodas())
                .thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/v1/cuentas/consultar"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Consultar cuenta por id OK")
    void consultarPorIdOk() throws Exception {

        when(cuentaService.consultarPorId(1L))
                .thenReturn(crearCuenta());

        mockMvc.perform(get("/api/v1/cuentas/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Consultar cuenta por id ERROR")
    void consultarPorIdError() throws Exception {

        when(cuentaService.consultarPorId(1L))
                .thenThrow(new RuntimeException("No existe"));

        mockMvc.perform(get("/api/v1/cuentas/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Consultar por número OK")
    void consultarNumeroOk() throws Exception {

        when(cuentaService.consultarPorNumeroCuenta("001"))
                .thenReturn(crearCuenta());

        mockMvc.perform(get("/api/v1/cuentas/numero/001"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Consultar por número ERROR")
    void consultarNumeroError() throws Exception {

        when(cuentaService.consultarPorNumeroCuenta("001"))
                .thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/v1/cuentas/numero/001"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Consultar cuentas por socio OK")
    void consultarSocioOk() throws Exception {

        when(cuentaService.consultarPorSocio("123"))
                .thenReturn(List.of(crearCuenta()));

        mockMvc.perform(get("/api/v1/cuentas/socio/123"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Consultar cuentas por socio ERROR")
    void consultarSocioError() throws Exception {

        when(cuentaService.consultarPorSocio("123"))
                .thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/v1/cuentas/socio/123"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Crear cuenta OK")
    void crearCuentaOk() throws Exception {

        CrearCuentaRequest request = new CrearCuentaRequest();
        request.setCedulaSocio("1234567890");
        request.setTipoCuenta(TipoCuenta.AHORRO); 

        when(cuentaService.crear(any(CrearCuentaRequest.class)))
                .thenReturn(crearCuenta());

        mockMvc.perform(post("/api/v1/cuentas/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Crear cuenta ERROR")
    void crearCuentaError() throws Exception {

        CrearCuentaRequest request = new CrearCuentaRequest();
        request.setCedulaSocio("1234567890");
        request.setTipoCuenta(TipoCuenta.AHORRO);

        doThrow(new RuntimeException())
                .when(cuentaService)
                .crear(any(CrearCuentaRequest.class));

        mockMvc.perform(post("/api/v1/cuentas/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}