package com.grupo5.caja_ahorro.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SocioTest {

    @Test
    void deberiaCrearSocioConConstructorVacio() {
        Socio socio = new Socio();

        assertNotNull(socio);
    }

    @Test
    void deberiaAsignarYObtenerDatos() {

        Socio socio = new Socio();

        socio.setIdSocio(1L);
        socio.setCedula("0102030405");
        socio.setNombres("Juan");
        socio.setApellidos("Perez");
        socio.setCorreo("juan@test.com");
        socio.setTelefono("0999999999");
        socio.setDireccion("Cuenca");
        socio.setFechaIngreso(LocalDate.of(2025,1,1));
        socio.setActivo(true);

        assertEquals(1L, socio.getIdSocio());
        assertEquals("0102030405", socio.getCedula());
        assertEquals("Juan", socio.getNombres());
        assertEquals("Perez", socio.getApellidos());
        assertEquals("juan@test.com", socio.getCorreo());
        assertEquals("0999999999", socio.getTelefono());
        assertEquals("Cuenca", socio.getDireccion());
        assertEquals(LocalDate.of(2025,1,1), socio.getFechaIngreso());
        assertTrue(socio.getActivo());
    }

    @Test
    void deberiaCrearSocioConConstructorCompleto() {

        LocalDate fecha = LocalDate.of(2025,1,1);

        Socio socio = new Socio(
                1L,
                "0102030405",
                "Juan",
                "Perez",
                "juan@test.com",
                "0999999999",
                "Cuenca",
                fecha,
                true
        );

        assertEquals(1L, socio.getIdSocio());
        assertEquals("Juan", socio.getNombres());
        assertEquals(fecha, socio.getFechaIngreso());
    }

    @Test
    void prePersistDebeAsignarValoresPorDefecto() {

        Socio socio = new Socio();

        socio.prePersist();

        assertNotNull(socio.getFechaIngreso());
        assertTrue(socio.getActivo());
    }

    @Test
    void prePersistNoDebeModificarValoresExistentes() {

        LocalDate fecha = LocalDate.of(2024,5,10);

        Socio socio = new Socio();

        socio.setFechaIngreso(fecha);
        socio.setActivo(false);

        socio.prePersist();

        assertEquals(fecha, socio.getFechaIngreso());
        assertFalse(socio.getActivo());
    }

}