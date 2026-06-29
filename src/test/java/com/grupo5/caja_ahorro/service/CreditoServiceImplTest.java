package com.grupo5.caja_ahorro.service;

import com.grupo5.caja_ahorro.model.Credito;
import com.grupo5.caja_ahorro.model.EstadoCredito;
import com.grupo5.caja_ahorro.model.SistemaAmortizacion;
import com.grupo5.caja_ahorro.repository.CreditoRepository;
import com.grupo5.caja_ahorro.repository.CuotaRepository;
import com.grupo5.caja_ahorro.request.SolicitudCreditoRequest;
import com.grupo5.caja_ahorro.response.CuotaAmortizacionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreditoServiceImplTest {

    @Mock
    private CreditoRepository creditoRepository;

    @Mock
    private CuotaRepository cuotaRepository;

    @Mock
    private IAmortizacionService amortizacionService;

    @InjectMocks
    private CreditoServiceImpl creditoService;

    private SolicitudCreditoRequest request;

    @BeforeEach
    void setUp() {

        request = new SolicitudCreditoRequest();
        request.setCedulaSocio("1234567890");
        request.setNumeroCuentaDesembolso("CTA-001");
        request.setMontoSolicitado(new BigDecimal("1000"));
        request.setPlazoMeses(12);
        request.setTasaInteresAnual(new BigDecimal("12"));
        request.setSeguroDesgravamen(new BigDecimal("5"));
        request.setSistemaAmortizacion(SistemaAmortizacion.FRANCES);
        request.setComentarioOficial("Solicitud");
    }

    @Test
    void consultarTodosDebeRetornarLista() {

        when(creditoRepository.findAll())
                .thenReturn(Collections.singletonList(new Credito()));

        List<Credito> resultado = creditoService.consultarTodos();

        assertEquals(1, resultado.size());
        verify(creditoRepository).findAll();
    }

    @Test
    void consultarPorSocioDebeRetornarCreditos() {

        when(creditoRepository.findByCedulaSocioOrderByFechaSolicitudDesc("1234567890"))
                .thenReturn(Collections.singletonList(new Credito()));

        List<Credito> resultado =
                creditoService.consultarPorSocio("1234567890");

        assertEquals(1, resultado.size());
    }

    @Test
    void consultarPorSocioDebeLanzarExcepcionSiCedulaEsVacia() {

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> creditoService.consultarPorSocio("")
        );

        assertEquals(
                "La cédula del socio es obligatoria.",
                ex.getMessage()
        );
    }

    @Test
    void consultarPorIdDebeRetornarCredito() {

        Credito credito = new Credito();

        when(creditoRepository.findById(1L))
                .thenReturn(Optional.of(credito));

        Credito resultado = creditoService.consultarPorId(1L);

        assertNotNull(resultado);
    }

    @Test
    void consultarPorIdDebeLanzarExcepcionCuandoNoExiste() {

        when(creditoRepository.findById(1L))
                .thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> creditoService.consultarPorId(1L)
        );

        assertEquals(
                "No existe un crédito con el ID indicado.",
                ex.getMessage()
        );
    }

    @Test
    void solicitarDebeCrearCredito() {

        when(creditoRepository.save(any(Credito.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Credito credito = creditoService.solicitar(request);

        assertNotNull(credito);
        assertEquals(EstadoCredito.PENDIENTE, credito.getEstado());
        assertEquals(request.getCedulaSocio(), credito.getCedulaSocio());

        verify(creditoRepository).save(any(Credito.class));
    }

    @Test
    void solicitarDebeLanzarExcepcionSiRequestEsNull() {

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> creditoService.solicitar(null)
        );

        assertEquals(
                "Los datos de la solicitud son obligatorios.",
                ex.getMessage()
        );
    }

    @Test
    void simularDebeRetornarTablaAmortizacion() {

        when(amortizacionService.simular(request))
                .thenReturn(Collections.<CuotaAmortizacionResponse>emptyList());

        List<CuotaAmortizacionResponse> resultado =
                creditoService.simular(request);

        assertNotNull(resultado);

        verify(amortizacionService).simular(request);
    }

}