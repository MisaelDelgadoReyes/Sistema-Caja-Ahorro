package com.grupo5.caja_ahorro.service;

import com.grupo5.caja_ahorro.model.Cuenta;
import com.grupo5.caja_ahorro.request.CrearCuentaRequest;

import java.util.List;

public interface ICuentaService {

    List<Cuenta> consultarTodas();

    Cuenta consultarPorId(Long idCuenta);

    Cuenta consultarPorNumeroCuenta(String numeroCuenta);

    List<Cuenta> consultarPorSocio(String cedula);

    Cuenta crear(CrearCuentaRequest request);
}