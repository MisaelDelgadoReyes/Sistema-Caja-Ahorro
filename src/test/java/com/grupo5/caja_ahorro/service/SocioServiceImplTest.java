package com.grupo5.caja_ahorro.service;

import com.grupo5.caja_ahorro.model.Socio;
import com.grupo5.caja_ahorro.repository.SocioRepository;
import com.grupo5.caja_ahorro.request.CrearSocioRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SocioServiceImplTest {

    private SocioRepository socioRepository;
    private SocioServiceImpl socioService;

    @BeforeEach
    void setUp() {
        socioRepository = Mockito.mock(SocioRepository.class);
        socioService = new SocioServiceImpl(socioRepository);
    }

    @Test
    void consultarTodosDebeRetornarLista() {

        Socio socio1 = new Socio();
        Socio socio2 = new Socio();

        when(socioRepository.findAll())
                .thenReturn(Arrays.asList(socio1, socio2));

        assertEquals(2, socioService.consultarTodos().size());
    }

    @Test
    void consultarPorIdDebeRetornarSocio() {

        Socio socio = new Socio();
        socio.setIdSocio(1L);

        when(socioRepository.findById(1L))
                .thenReturn(Optional.of(socio));

        Socio resultado = socioService.consultarPorId(1L);

        assertEquals(1L, resultado.getIdSocio());
    }

    @Test
    void consultarPorIdDebeLanzarExcepcion() {

        when(socioRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> socioService.consultarPorId(99L)
        );
    }

    @Test
    void consultarPorCedulaDebeRetornarSocio() {

        Socio socio = new Socio();
        socio.setCedula("1234567890");

        when(socioRepository.findByCedula("1234567890"))
                .thenReturn(Optional.of(socio));

        Socio resultado =
                socioService.consultarPorCedula("1234567890");

        assertEquals("1234567890", resultado.getCedula());
    }

    @Test
    void crearDebeGuardarSocio() {

        CrearSocioRequest request = new CrearSocioRequest();

        request.setCedula("1234567890");
        request.setNombres("Juan");
        request.setApellidos("Perez");
        request.setCorreo("juan@test.com");
        request.setTelefono("0999999999");
        request.setDireccion("Quito");

        when(socioRepository.existsByCedula("1234567890"))
                .thenReturn(false);

        when(socioRepository.save(any(Socio.class)))
                .thenAnswer(i -> i.getArgument(0));

        Socio socio = socioService.crear(request);

        assertEquals("Juan", socio.getNombres());

        verify(socioRepository).save(any(Socio.class));
    }
}