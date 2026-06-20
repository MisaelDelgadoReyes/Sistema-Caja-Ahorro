package com.grupo5.caja_ahorro.service;

import com.grupo5.caja_ahorro.model.Credito;
import com.grupo5.caja_ahorro.model.Cuota;
import com.grupo5.caja_ahorro.model.SistemaAmortizacion;
import com.grupo5.caja_ahorro.request.SolicitudCreditoRequest;
import com.grupo5.caja_ahorro.response.CuotaAmortizacionResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AmortizacionServiceImpl implements IAmortizacionService {

    private static final int ESCALA_MONETARIA = 2;
    private static final int ESCALA_CALCULO = 10;
    private static final BigDecimal CIEN = new BigDecimal("100");
    private static final BigDecimal DOCE = new BigDecimal("12");

    @Override
    public List<CuotaAmortizacionResponse> simular(SolicitudCreditoRequest request) {
        validarDatosBase(
                request.getMontoSolicitado(),
                request.getPlazoMeses(),
                request.getTasaInteresAnual(),
                request.getSeguroDesgravamen(),
                request.getSistemaAmortizacion()
        );

        if (SistemaAmortizacion.FRANCES.equals(request.getSistemaAmortizacion())) {
            return generarTablaFrancesa(
                    request.getMontoSolicitado(),
                    request.getPlazoMeses(),
                    request.getTasaInteresAnual(),
                    request.getSeguroDesgravamen(),
                    LocalDate.now()
            );
        }

        return generarTablaAlemana(
                request.getMontoSolicitado(),
                request.getPlazoMeses(),
                request.getTasaInteresAnual(),
                request.getSeguroDesgravamen(),
                LocalDate.now()
        );
    }

    @Override
    public List<Cuota> generarCuotas(Credito credito) {
        validarDatosBase(
                credito.getMontoSolicitado(),
                credito.getPlazoMeses(),
                credito.getTasaInteresAnual(),
                credito.getSeguroDesgravamen(),
                credito.getSistemaAmortizacion()
        );

        List<CuotaAmortizacionResponse> tabla;

        if (SistemaAmortizacion.FRANCES.equals(credito.getSistemaAmortizacion())) {
            tabla = generarTablaFrancesa(
                    credito.getMontoSolicitado(),
                    credito.getPlazoMeses(),
                    credito.getTasaInteresAnual(),
                    credito.getSeguroDesgravamen(),
                    LocalDate.now()
            );
        } else {
            tabla = generarTablaAlemana(
                    credito.getMontoSolicitado(),
                    credito.getPlazoMeses(),
                    credito.getTasaInteresAnual(),
                    credito.getSeguroDesgravamen(),
                    LocalDate.now()
            );
        }

        List<Cuota> cuotas = new ArrayList<>();

        for (CuotaAmortizacionResponse item : tabla) {
            Cuota cuota = new Cuota();
            cuota.setCredito(credito);
            cuota.setNumeroCuota(item.getNumeroCuota());
            cuota.setFechaVencimiento(item.getFechaVencimiento());
            cuota.setCapital(item.getCapital());
            cuota.setInteres(item.getInteres());
            cuota.setSeguroDesgravamen(item.getSeguroDesgravamen());
            cuota.setValorCuota(item.getValorCuota());
            cuota.setSaldoCapital(item.getSaldoCapital());

            cuotas.add(cuota);
        }

        return cuotas;
    }

    private List<CuotaAmortizacionResponse> generarTablaFrancesa(
            BigDecimal monto,
            Integer plazoMeses,
            BigDecimal tasaInteresAnual,
            BigDecimal seguroDesgravamen,
            LocalDate fechaInicio
    ) {
        List<CuotaAmortizacionResponse> tabla = new ArrayList<>();

        BigDecimal tasaMensual = calcularTasaMensual(tasaInteresAnual);
        BigDecimal saldo = monto;
        BigDecimal cuotaBase = calcularCuotaFrancesa(monto, tasaMensual, plazoMeses);

        for (int numeroCuota = 1; numeroCuota <= plazoMeses; numeroCuota++) {
            BigDecimal interes = saldo.multiply(tasaMensual).setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);
            BigDecimal capital = cuotaBase.subtract(interes).setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);

            if (numeroCuota == plazoMeses) {
                capital = saldo.setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);
            }

            BigDecimal seguro = calcularSeguroMensual(saldo, seguroDesgravamen);
            BigDecimal valorCuota = capital.add(interes).add(seguro).setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);

            saldo = saldo.subtract(capital).setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);

            if (saldo.compareTo(BigDecimal.ZERO) < 0) {
                saldo = BigDecimal.ZERO;
            }

            tabla.add(new CuotaAmortizacionResponse(
                    numeroCuota,
                    fechaInicio.plusMonths(numeroCuota),
                    capital,
                    interes,
                    seguro,
                    valorCuota,
                    saldo
            ));
        }

        return tabla;
    }

    private List<CuotaAmortizacionResponse> generarTablaAlemana(
            BigDecimal monto,
            Integer plazoMeses,
            BigDecimal tasaInteresAnual,
            BigDecimal seguroDesgravamen,
            LocalDate fechaInicio
    ) {
        List<CuotaAmortizacionResponse> tabla = new ArrayList<>();

        BigDecimal tasaMensual = calcularTasaMensual(tasaInteresAnual);
        BigDecimal saldo = monto;
        BigDecimal capitalFijo = monto.divide(BigDecimal.valueOf(plazoMeses), ESCALA_MONETARIA, RoundingMode.HALF_UP);

        for (int numeroCuota = 1; numeroCuota <= plazoMeses; numeroCuota++) {
            BigDecimal capital = capitalFijo;

            if (numeroCuota == plazoMeses) {
                capital = saldo.setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);
            }

            BigDecimal interes = saldo.multiply(tasaMensual).setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);
            BigDecimal seguro = calcularSeguroMensual(saldo, seguroDesgravamen);
            BigDecimal valorCuota = capital.add(interes).add(seguro).setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);

            saldo = saldo.subtract(capital).setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);

            if (saldo.compareTo(BigDecimal.ZERO) < 0) {
                saldo = BigDecimal.ZERO;
            }

            tabla.add(new CuotaAmortizacionResponse(
                    numeroCuota,
                    fechaInicio.plusMonths(numeroCuota),
                    capital,
                    interes,
                    seguro,
                    valorCuota,
                    saldo
            ));
        }

        return tabla;
    }

    private BigDecimal calcularCuotaFrancesa(BigDecimal monto, BigDecimal tasaMensual, Integer plazoMeses) {
        if (tasaMensual.compareTo(BigDecimal.ZERO) == 0) {
            return monto.divide(BigDecimal.valueOf(plazoMeses), ESCALA_MONETARIA, RoundingMode.HALF_UP);
        }

        double tasa = tasaMensual.doubleValue();
        double factor = Math.pow(1 + tasa, plazoMeses);
        double cuota = monto.doubleValue() * ((tasa * factor) / (factor - 1));

        return BigDecimal.valueOf(cuota).setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularTasaMensual(BigDecimal tasaInteresAnual) {
        return tasaInteresAnual
                .divide(CIEN, ESCALA_CALCULO, RoundingMode.HALF_UP)
                .divide(DOCE, ESCALA_CALCULO, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularSeguroMensual(BigDecimal saldo, BigDecimal seguroDesgravamen) {
        return saldo
                .multiply(seguroDesgravamen)
                .divide(CIEN, ESCALA_CALCULO, RoundingMode.HALF_UP)
                .setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);
    }

    private void validarDatosBase(
            BigDecimal monto,
            Integer plazoMeses,
            BigDecimal tasaInteresAnual,
            BigDecimal seguroDesgravamen,
            SistemaAmortizacion sistemaAmortizacion
    ) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto solicitado debe ser mayor a cero.");
        }

        if (plazoMeses == null || plazoMeses <= 0) {
            throw new IllegalArgumentException("El plazo en meses debe ser mayor a cero.");
        }

        if (tasaInteresAnual == null || tasaInteresAnual.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La tasa de interés anual no puede ser negativa.");
        }

        if (seguroDesgravamen == null || seguroDesgravamen.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El seguro de desgravamen no puede ser negativo.");
        }

        if (sistemaAmortizacion == null) {
            throw new IllegalArgumentException("Debe seleccionar un sistema de amortización.");
        }
    }
}