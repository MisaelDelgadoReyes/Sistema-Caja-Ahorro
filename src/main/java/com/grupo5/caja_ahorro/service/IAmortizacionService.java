package com.grupo5.caja_ahorro.service;

import com.grupo5.caja_ahorro.model.Credito;
import com.grupo5.caja_ahorro.model.Cuota;
import com.grupo5.caja_ahorro.request.SolicitudCreditoRequest;
import com.grupo5.caja_ahorro.response.CuotaAmortizacionResponse;

import java.util.List;

public interface IAmortizacionService {

    List<CuotaAmortizacionResponse> simular(SolicitudCreditoRequest request);

    List<Cuota> generarCuotas(Credito credito);
}