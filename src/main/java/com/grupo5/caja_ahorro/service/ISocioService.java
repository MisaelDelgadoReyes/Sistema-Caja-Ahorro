package com.grupo5.caja_ahorro.service;

import com.grupo5.caja_ahorro.model.Socio;
import com.grupo5.caja_ahorro.request.CrearSocioRequest;

import java.util.List;

public interface ISocioService {

    List<Socio> consultarTodos();

    Socio consultarPorId(Long idSocio);

    Socio consultarPorCedula(String cedula);

    Socio crear(CrearSocioRequest request);
}