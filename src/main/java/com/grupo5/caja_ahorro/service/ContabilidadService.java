package com.grupo5.caja_ahorro.service;

import com.grupo5.caja_ahorro.request.TransaccionVentanillaRequest;
import com.grupo5.caja_ahorro.response.TransaccionResponse;

public interface ContabilidadService {
    TransaccionResponse procesarTransaccion(TransaccionVentanillaRequest request);
}