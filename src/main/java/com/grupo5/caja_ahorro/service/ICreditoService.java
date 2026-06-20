package com.grupo5.caja_ahorro.service;

import com.grupo5.caja_ahorro.model.Credito;
import com.grupo5.caja_ahorro.model.Cuota;
import com.grupo5.caja_ahorro.request.AprobarCreditoRequest;
import com.grupo5.caja_ahorro.request.PagoCuotaRequest;
import com.grupo5.caja_ahorro.request.RechazarCreditoRequest;
import com.grupo5.caja_ahorro.request.SolicitudCreditoRequest;
import com.grupo5.caja_ahorro.response.CuotaAmortizacionResponse;

import java.util.List;

public interface ICreditoService {

    List<Credito> consultarTodos();

    List<Credito> consultarPorSocio(String cedulaSocio);

    Credito consultarPorId(Long idCredito);

    Credito solicitar(SolicitudCreditoRequest request);

    List<CuotaAmortizacionResponse> simular(SolicitudCreditoRequest request);

    Credito aprobar(Long idCredito, AprobarCreditoRequest request);

    Credito rechazar(Long idCredito, RechazarCreditoRequest request);

    Credito desembolsar(Long idCredito);

    List<Cuota> consultarAmortizacion(Long idCredito);

    Cuota pagarCuota(Long idCuota, PagoCuotaRequest request);

    Credito marcarMora(Long idCredito);
}