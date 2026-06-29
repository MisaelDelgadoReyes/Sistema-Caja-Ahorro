package com.grupo5.caja_ahorro.service;

import com.grupo5.caja_ahorro.model.Cuenta;
import com.grupo5.caja_ahorro.model.Socio;
import com.grupo5.caja_ahorro.model.TipoCuenta;
import com.grupo5.caja_ahorro.repository.CuentaRepository;
import com.grupo5.caja_ahorro.repository.SocioRepository;
import com.grupo5.caja_ahorro.request.CrearCuentaRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CuentaServiceImplTest {

    private CuentaRepository cuentaRepository;
    private SocioRepository socioRepository;
    private CuentaServiceImpl cuentaService;

    @BeforeEach
    void setUp() {
        cuentaRepository = mock(CuentaRepository.class);
        socioRepository = mock(SocioRepository.class);
        cuentaService = new CuentaServiceImpl(cuentaRepository, socioRepository);
    }

    @Test
    void consultarTodasDebeRetornarLista() {

        Cuenta c1 = new Cuenta();
        Cuenta c2 = new Cuenta();

        when(cuentaRepository.findAll())
                .thenReturn(Arrays.asList(c1, c2));

        assertEquals(2, cuentaService.consultarTodas().size());
    }

    @Test
    void consultarPorIdDebeRetornarCuenta() {

        Cuenta cuenta = new Cuenta();
        cuenta.setIdCuenta(1L);

        when(cuentaRepository.findById(1L))
                .thenReturn(Optional.of(cuenta));

        Cuenta resultado = cuentaService.consultarPorId(1L);

        assertEquals(1L, resultado.getIdCuenta());
    }

    @Test
    void consultarPorNumeroCuentaDebeRetornarCuenta() {

        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta("CTA-001");

        when(cuentaRepository.findByNumeroCuenta("CTA-001"))
                .thenReturn(Optional.of(cuenta));

        Cuenta resultado =
                cuentaService.consultarPorNumeroCuenta("CTA-001");

        assertEquals("CTA-001", resultado.getNumeroCuenta());
    }

    @Test
    void consultarPorSocioDebeRetornarLista() {

        Cuenta cuenta = new Cuenta();

        when(cuentaRepository.findBySocioCedula("1234567890"))
                .thenReturn(Arrays.asList(cuenta));

        assertEquals(
                1,
                cuentaService.consultarPorSocio("1234567890").size()
        );
    }

    @Test
    void crearDebeGuardarCuenta() {

        CrearCuentaRequest request = new CrearCuentaRequest();
        request.setCedulaSocio("1234567890");
        request.setTipoCuenta(TipoCuenta.AHORRO);

        Socio socio = new Socio();
        socio.setCedula("1234567890");

        when(socioRepository.findByCedula("1234567890"))
                .thenReturn(Optional.of(socio));

        when(cuentaRepository.save(any(Cuenta.class)))
                .thenAnswer(i -> i.getArgument(0));

        Cuenta cuenta = cuentaService.crear(request);

        assertEquals(TipoCuenta.AHORRO, cuenta.getTipoCuenta());
        assertEquals(socio, cuenta.getSocio());

        verify(cuentaRepository).save(any(Cuenta.class));
    }
}