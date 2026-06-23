package com.grupo5.caja_ahorro.service;

import com.grupo5.caja_ahorro.model.Cuenta;
import com.grupo5.caja_ahorro.model.Socio;
import com.grupo5.caja_ahorro.repository.CuentaRepository;
import com.grupo5.caja_ahorro.repository.SocioRepository;
import com.grupo5.caja_ahorro.request.CrearCuentaRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CuentaServiceImpl implements ICuentaService {

    private final CuentaRepository cuentaRepository;
    private final SocioRepository socioRepository;

    public CuentaServiceImpl(
            CuentaRepository cuentaRepository,
            SocioRepository socioRepository
    ) {
        this.cuentaRepository = cuentaRepository;
        this.socioRepository = socioRepository;
    }

    @Override
    public List<Cuenta> consultarTodas() {
        return cuentaRepository.findAll();
    }

    @Override
    public Cuenta consultarPorId(Long idCuenta) {

        return cuentaRepository.findById(idCuenta)
                .orElseThrow(() ->
                        new IllegalArgumentException("No existe una cuenta con el ID indicado."));
    }

    @Override
    public Cuenta consultarPorNumeroCuenta(String numeroCuenta) {

        return cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() ->
                        new IllegalArgumentException("No existe una cuenta con el número indicado."));
    }

    @Override
    public List<Cuenta> consultarPorSocio(String cedula) {

        return cuentaRepository.findBySocioCedula(cedula);
    }

    @Override
    public Cuenta crear(CrearCuentaRequest request) {

    validar(request);

    Socio socio = socioRepository.findByCedula(request.getCedulaSocio())
            .orElseThrow(() ->
                    new IllegalArgumentException("No existe un socio con la cédula indicada."));

    Cuenta cuenta = new Cuenta();

    cuenta.setTipoCuenta(request.getTipoCuenta());

    cuenta.setNumeroCuenta(
            "CTA-" + System.currentTimeMillis()
    );

    cuenta.setSaldo(BigDecimal.ZERO);

    cuenta.setSocio(socio);

    return cuentaRepository.save(cuenta);
    }

    private void validar(CrearCuentaRequest request) {

    if (request == null) {
        throw new IllegalArgumentException("Los datos de la cuenta son obligatorios.");
    }

    if (request.getCedulaSocio() == null
            || request.getCedulaSocio().isBlank()) {
        throw new IllegalArgumentException("La cédula del socio es obligatoria.");
    }

    if (request.getTipoCuenta() == null) {
    throw new IllegalArgumentException("El tipo de cuenta es obligatorio.");
    }
}   

}