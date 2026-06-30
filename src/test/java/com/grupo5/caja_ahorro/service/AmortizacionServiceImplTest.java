package com.grupo5.caja_ahorro.service;

import com.grupo5.caja_ahorro.model.Credito;
import com.grupo5.caja_ahorro.model.Cuota;
import com.grupo5.caja_ahorro.model.SistemaAmortizacion;
import com.grupo5.caja_ahorro.request.SolicitudCreditoRequest;
import com.grupo5.caja_ahorro.response.CuotaAmortizacionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AmortizacionServiceImplTest {

    private AmortizacionServiceImpl amortizacionService;

    @BeforeEach
    void setUp() {
        amortizacionService = new AmortizacionServiceImpl();
    }

    @Test
    void simularCreditoConSistemaFrancesDebeGenerarTablaDeAmortizacion() {
        SolicitudCreditoRequest request = crearSolicitud(SistemaAmortizacion.FRANCES);

        List<CuotaAmortizacionResponse> resultado = amortizacionService.simular(request);

        assertThat(resultado).hasSize(6);
        assertThat(resultado.get(0).getNumeroCuota()).isEqualTo(1);
        assertThat(resultado.get(0).getCapital()).isGreaterThan(BigDecimal.ZERO);
        assertThat(resultado.get(0).getInteres()).isGreaterThan(BigDecimal.ZERO);
        assertThat(resultado.get(0).getSeguroDesgravamen()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        assertThat(resultado.get(0).getValorCuota()).isGreaterThan(BigDecimal.ZERO);
        assertThat(resultado.get(5).getSaldoCapital()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void simularCreditoConSistemaAlemanDebeGenerarTablaDeAmortizacion() {
        SolicitudCreditoRequest request = crearSolicitud(SistemaAmortizacion.ALEMAN);

        List<CuotaAmortizacionResponse> resultado = amortizacionService.simular(request);

        assertThat(resultado).hasSize(6);
        assertThat(resultado.get(0).getNumeroCuota()).isEqualTo(1);
        assertThat(resultado.get(0).getCapital()).isGreaterThan(BigDecimal.ZERO);
        assertThat(resultado.get(0).getInteres()).isGreaterThan(BigDecimal.ZERO);
        assertThat(resultado.get(5).getSaldoCapital()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void generarCuotasDebeConvertirTablaDeAmortizacionEnEntidadesCuota() {
        Credito credito = new Credito();
        credito.setCedulaSocio("0923456789");
        credito.setNumeroCuentaDesembolso("AHO-0001");
        credito.setMontoSolicitado(new BigDecimal("1000.00"));
        credito.setPlazoMeses(6);
        credito.setTasaInteresAnual(new BigDecimal("12.00"));
        credito.setSeguroDesgravamen(new BigDecimal("0.50"));
        credito.setSistemaAmortizacion(SistemaAmortizacion.FRANCES);

        List<Cuota> cuotas = amortizacionService.generarCuotas(credito);

        assertThat(cuotas).hasSize(6);
        assertThat(cuotas.get(0).getCredito()).isEqualTo(credito);
        assertThat(cuotas.get(0).getNumeroCuota()).isEqualTo(1);
        assertThat(cuotas.get(0).getValorCuota()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    void simularDebeLanzarErrorCuandoMontoEsCero() {
        SolicitudCreditoRequest request = crearSolicitud(SistemaAmortizacion.FRANCES);
        request.setMontoSolicitado(BigDecimal.ZERO);

        assertThatThrownBy(() -> amortizacionService.simular(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("monto solicitado");
    }

    @Test
    void simularDebeLanzarErrorCuandoPlazoEsCero() {
        SolicitudCreditoRequest request = crearSolicitud(SistemaAmortizacion.FRANCES);
        request.setPlazoMeses(0);

        assertThatThrownBy(() -> amortizacionService.simular(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("plazo");
    }

    @Test
    void simularDebeLanzarErrorCuandoTasaEsNegativa() {
        SolicitudCreditoRequest request = crearSolicitud(SistemaAmortizacion.FRANCES);
        request.setTasaInteresAnual(new BigDecimal("-1.00"));

        assertThatThrownBy(() -> amortizacionService.simular(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("tasa");
    }

    @Test
    void simularDebeLanzarErrorCuandoSistemaAmortizacionEsNulo() {
        SolicitudCreditoRequest request = crearSolicitud(null);

        assertThatThrownBy(() -> amortizacionService.simular(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sistema de amortización");
    }

    private SolicitudCreditoRequest crearSolicitud(SistemaAmortizacion sistemaAmortizacion) {
        SolicitudCreditoRequest request = new SolicitudCreditoRequest();
        request.setCedulaSocio("0923456789");
        request.setNumeroCuentaDesembolso("AHO-0001");
        request.setMontoSolicitado(new BigDecimal("1000.00"));
        request.setPlazoMeses(6);
        request.setTasaInteresAnual(new BigDecimal("12.00"));
        request.setSeguroDesgravamen(new BigDecimal("0.50"));
        request.setSistemaAmortizacion(sistemaAmortizacion);
        request.setComentarioOficial("Prueba unitaria de amortización");
        return request;

    }
}