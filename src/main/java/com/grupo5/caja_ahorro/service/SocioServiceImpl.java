package com.grupo5.caja_ahorro.service;

import com.grupo5.caja_ahorro.model.Socio;
import com.grupo5.caja_ahorro.repository.SocioRepository;
import com.grupo5.caja_ahorro.request.CrearSocioRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SocioServiceImpl implements ISocioService {

    private final SocioRepository socioRepository;

    public SocioServiceImpl(SocioRepository socioRepository) {
        this.socioRepository = socioRepository;
    }

    @Override
    public List<Socio> consultarTodos() {
        return socioRepository.findAll();
    }

    @Override
    public Socio consultarPorId(Long idSocio) {

        return socioRepository.findById(idSocio)
                .orElseThrow(() ->
                        new IllegalArgumentException("No existe un socio con el ID indicado."));
    }

    @Override
    public Socio consultarPorCedula(String cedula) {

        return socioRepository.findByCedula(cedula)
                .orElseThrow(() ->
                        new IllegalArgumentException("No existe un socio con la cédula indicada."));
    }

    @Override
    public Socio crear(CrearSocioRequest request) {

        validar(request);

        if (socioRepository.findByCedula(request.getCedula()).isPresent()) {
            throw new IllegalStateException("Ya existe un socio con esa cédula.");
        }

        Socio socio = new Socio();

        socio.setCedula(request.getCedula());
        socio.setNombres(request.getNombres());
        socio.setApellidos(request.getApellidos());
        socio.setCorreo(request.getCorreo());
        socio.setTelefono(request.getTelefono());
        socio.setDireccion(request.getDireccion());

        return socioRepository.save(socio);
    }

    private void validar(CrearSocioRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Los datos del socio son obligatorios.");
        }

        if (request.getCedula() == null || request.getCedula().isBlank()) {
            throw new IllegalArgumentException("La cédula es obligatoria.");
        }

        if (request.getNombres() == null || request.getNombres().isBlank()) {
            throw new IllegalArgumentException("Los nombres son obligatorios.");
        }

        if (request.getApellidos() == null || request.getApellidos().isBlank()) {
            throw new IllegalArgumentException("Los apellidos son obligatorios.");
        }
    }
}