package com.grupo5.caja_ahorro.controller;

import com.grupo5.caja_ahorro.model.Credito;
import com.grupo5.caja_ahorro.model.Cuota;
import com.grupo5.caja_ahorro.model.EstadoCredito;
import com.grupo5.caja_ahorro.model.EstadoCuota;
import com.grupo5.caja_ahorro.model.SistemaAmortizacion;
import com.grupo5.caja_ahorro.request.AprobarCreditoRequest;
import com.grupo5.caja_ahorro.request.PagoCuotaRequest;
import com.grupo5.caja_ahorro.request.RechazarCreditoRequest;
import com.grupo5.caja_ahorro.request.SolicitudCreditoRequest;
import com.grupo5.caja_ahorro.response.CuotaAmortizacionResponse;
import com.grupo5.caja_ahorro.response.ResponseRest;
import com.grupo5.caja_ahorro.service.ICreditoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditoRestControllerTest {

    @Mock
    private ICreditoService creditoService;

    @InjectMocks
    private CreditoRestController creditoRestController;

    @Test
    void consultarTodosDebeRetornarOkConListaDeCreditos() {
        Credito credito = crearCredito(EstadoCredito.PENDIENTE);
        when(creditoService.consultarTodos()).thenReturn(List.of(credito));

        ResponseEntity<ResponseRest<Credito>> response = creditoRestController.consultarTodos();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).hasSize(1);
        assertThat(response.getBody().getErrors()).isEmpty();
    }

    @Test
    void consultarTodosDebeRetornarErrorInternoCuandoFallaServicio() {
        when(creditoService.consultarTodos()).thenThrow(new RuntimeException("Error inesperado"));

        ResponseEntity<ResponseRest<Credito>> response = creditoRestController.consultarTodos();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrors()).hasSize(1);
        assertThat(response.getBody().getErrors().get(0).getCodigo()).isEqualTo(500);
    }

    @Test
    void consultarPorIdDebeRetornarOkCuandoExisteCredito() {
        Credito credito = crearCredito(EstadoCredito.APROBADO);
        when(creditoService.consultarPorId(1L)).thenReturn(credito);

        ResponseEntity<ResponseRest<Credito>> response = creditoRestController.consultarPorId(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).hasSize(1);
        assertThat(response.getBody().getData().get(0).getEstado()).isEqualTo(EstadoCredito.APROBADO);
    }

    @Test
    void consultarPorIdDebeRetornarNotFoundCuandoNoExisteCredito() {
        when(creditoService.consultarPorId(99L))
                .thenThrow(new IllegalArgumentException("No existe un crédito con el ID indicado."));

        ResponseEntity<ResponseRest<Credito>> response = creditoRestController.consultarPorId(99L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrors()).hasSize(1);
        assertThat(response.getBody().getErrors().get(0).getCodigo()).isEqualTo(404);
    }

    @Test
    void consultarPorSocioDebeRetornarOkConCreditosDelSocio() {
        Credito credito = crearCredito(EstadoCredito.VIGENTE);
        when(creditoService.consultarPorSocio("0923456789")).thenReturn(List.of(credito));

        ResponseEntity<ResponseRest<Credito>> response = creditoRestController.consultarPorSocio("0923456789");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).hasSize(1);
    }

    @Test
    void consultarPorSocioDebeRetornarBadRequestCuandoCedulaEsInvalida() {
        when(creditoService.consultarPorSocio(" "))
                .thenThrow(new IllegalArgumentException("La cédula del socio es obligatoria."));

        ResponseEntity<ResponseRest<Credito>> response = creditoRestController.consultarPorSocio(" ");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrors()).hasSize(1);
    }

    @Test
    void simularDebeRetornarOkConTablaDeAmortizacion() {
        SolicitudCreditoRequest request = crearSolicitud();
        CuotaAmortizacionResponse cuota = crearCuotaResponse();

        when(creditoService.simular(request)).thenReturn(List.of(cuota));

        ResponseEntity<ResponseRest<CuotaAmortizacionResponse>> response =
                creditoRestController.simular(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).hasSize(1);
        assertThat(response.getBody().getData().get(0).getNumeroCuota()).isEqualTo(1);
    }

    @Test
    void simularDebeRetornarBadRequestCuandoDatosSonInvalidos() {
        SolicitudCreditoRequest request = crearSolicitud();
        when(creditoService.simular(request))
                .thenThrow(new IllegalArgumentException("El monto solicitado debe ser mayor a cero."));

        ResponseEntity<ResponseRest<CuotaAmortizacionResponse>> response =
                creditoRestController.simular(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrors()).hasSize(1);
    }

    @Test
    void solicitarDebeRetornarCreatedCuandoSolicitudEsValida() {
        SolicitudCreditoRequest request = crearSolicitud();
        Credito credito = crearCredito(EstadoCredito.PENDIENTE);

        when(creditoService.solicitar(request)).thenReturn(credito);

        ResponseEntity<ResponseRest<Credito>> response = creditoRestController.solicitar(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).hasSize(1);
        assertThat(response.getBody().getData().get(0).getEstado()).isEqualTo(EstadoCredito.PENDIENTE);
    }

    @Test
    void aprobarDebeRetornarOkCuandoCreditoPuedeAprobarse() {
        AprobarCreditoRequest request = new AprobarCreditoRequest();
        request.setComentarioOficial("Aprobado");

        Credito credito = crearCredito(EstadoCredito.APROBADO);
        when(creditoService.aprobar(1L, request)).thenReturn(credito);

        ResponseEntity<ResponseRest<Credito>> response = creditoRestController.aprobar(1L, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).hasSize(1);
        assertThat(response.getBody().getData().get(0).getEstado()).isEqualTo(EstadoCredito.APROBADO);
    }

    @Test
    void aprobarDebeRetornarConflictCuandoEstadoNoPermiteAprobar() {
        AprobarCreditoRequest request = new AprobarCreditoRequest();

        when(creditoService.aprobar(1L, request))
                .thenThrow(new IllegalStateException("Solo se puede aprobar un crédito en estado PENDIENTE."));

        ResponseEntity<ResponseRest<Credito>> response = creditoRestController.aprobar(1L, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrors()).hasSize(1);
        assertThat(response.getBody().getErrors().get(0).getCodigo()).isEqualTo(409);
    }

    @Test
    void rechazarDebeRetornarOkCuandoCreditoPuedeRechazarse() {
        RechazarCreditoRequest request = new RechazarCreditoRequest();
        request.setMotivoRechazo("No cumple requisitos");

        Credito credito = crearCredito(EstadoCredito.RECHAZADO);
        when(creditoService.rechazar(1L, request)).thenReturn(credito);

        ResponseEntity<ResponseRest<Credito>> response = creditoRestController.rechazar(1L, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().get(0).getEstado()).isEqualTo(EstadoCredito.RECHAZADO);
    }

    @Test
    void desembolsarDebeRetornarOkCuandoCreditoEstaAprobado() {
        Credito credito = crearCredito(EstadoCredito.VIGENTE);
        when(creditoService.desembolsar(1L)).thenReturn(credito);

        ResponseEntity<ResponseRest<Credito>> response = creditoRestController.desembolsar(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().get(0).getEstado()).isEqualTo(EstadoCredito.VIGENTE);
    }

    @Test
    void consultarAmortizacionDebeRetornarOkConCuotas() {
        Credito credito = crearCredito(EstadoCredito.VIGENTE);
        Cuota cuota = crearCuota(credito, EstadoCuota.PENDIENTE);

        when(creditoService.consultarAmortizacion(1L)).thenReturn(List.of(cuota));

        ResponseEntity<ResponseRest<Cuota>> response = creditoRestController.consultarAmortizacion(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).hasSize(1);
    }

    @Test
    void pagarCuotaDebeRetornarOkCuandoPagoEsValido() {
        Credito credito = crearCredito(EstadoCredito.VIGENTE);
        Cuota cuota = crearCuota(credito, EstadoCuota.PAGADA);

        PagoCuotaRequest request = new PagoCuotaRequest();
        request.setMontoPagado(new BigDecimal("175.00"));
        request.setFechaPago(LocalDate.now());

        when(creditoService.pagarCuota(1L, request)).thenReturn(cuota);

        ResponseEntity<ResponseRest<Cuota>> response = creditoRestController.pagarCuota(1L, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().get(0).getEstado()).isEqualTo(EstadoCuota.PAGADA);
    }

    @Test
    void pagarCuotaDebeRetornarConflictCuandoNoSePuedePagar() {
        PagoCuotaRequest request = new PagoCuotaRequest();
        request.setMontoPagado(new BigDecimal("175.00"));

        when(creditoService.pagarCuota(1L, request))
                .thenThrow(new IllegalStateException("La cuota ya se encuentra pagada."));

        ResponseEntity<ResponseRest<Cuota>> response = creditoRestController.pagarCuota(1L, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrors()).hasSize(1);
    }

    @Test
    void marcarMoraDebeRetornarOkCuandoCreditoTieneCuotasVencidas() {
        Credito credito = crearCredito(EstadoCredito.EN_MORA);
        when(creditoService.marcarMora(1L)).thenReturn(credito);

        ResponseEntity<ResponseRest<Credito>> response = creditoRestController.marcarMora(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().get(0).getEstado()).isEqualTo(EstadoCredito.EN_MORA);
    }

    @Test
    void marcarMoraDebeRetornarConflictCuandoNoTieneCuotasVencidas() {
        when(creditoService.marcarMora(1L))
                .thenThrow(new IllegalStateException("El crédito no tiene cuotas vencidas."));

        ResponseEntity<ResponseRest<Credito>> response = creditoRestController.marcarMora(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrors()).hasSize(1);
    }

    private SolicitudCreditoRequest crearSolicitud() {
        SolicitudCreditoRequest request = new SolicitudCreditoRequest();
        request.setCedulaSocio("0923456789");
        request.setNumeroCuentaDesembolso("AHO-0001");
        request.setMontoSolicitado(new BigDecimal("1000.00"));
        request.setPlazoMeses(6);
        request.setTasaInteresAnual(new BigDecimal("12.00"));
        request.setSeguroDesgravamen(new BigDecimal("0.50"));
        request.setSistemaAmortizacion(SistemaAmortizacion.FRANCES);
        request.setComentarioOficial("Prueba unitaria desde controlador");
        return request;
    }

    private Credito crearCredito(EstadoCredito estado) {
        Credito credito = new Credito();
        credito.setIdCredito(1L);
        credito.setCedulaSocio("0923456789");
        credito.setNumeroCuentaDesembolso("AHO-0001");
        credito.setMontoSolicitado(new BigDecimal("1000.00"));
        credito.setPlazoMeses(6);
        credito.setTasaInteresAnual(new BigDecimal("12.00"));
        credito.setSeguroDesgravamen(new BigDecimal("0.50"));
        credito.setSistemaAmortizacion(SistemaAmortizacion.FRANCES);
        credito.setEstado(estado);
        credito.setFechaSolicitud(LocalDate.now());
        credito.setFechaCambioEstado(LocalDate.now());
        credito.setSaldoPendiente(new BigDecimal("1000.00"));
        return credito;
    }

    private Cuota crearCuota(Credito credito, EstadoCuota estado) {
        Cuota cuota = new Cuota();
        cuota.setIdCuota(1L);
        cuota.setCredito(credito);
        cuota.setNumeroCuota(1);
        cuota.setFechaVencimiento(LocalDate.now().plusMonths(1));
        cuota.setCapital(new BigDecimal("160.00"));
        cuota.setInteres(new BigDecimal("10.00"));
        cuota.setSeguroDesgravamen(new BigDecimal("5.00"));
        cuota.setValorCuota(new BigDecimal("175.00"));
        cuota.setSaldoCapital(new BigDecimal("840.00"));
        cuota.setEstado(estado);
        cuota.setMontoPagado(BigDecimal.ZERO);
        return cuota;
    }

    private CuotaAmortizacionResponse crearCuotaResponse() {
        return new CuotaAmortizacionResponse(
                1,
                LocalDate.now().plusMonths(1),
                new BigDecimal("160.00"),
                new BigDecimal("10.00"),
                new BigDecimal("5.00"),
                new BigDecimal("175.00"),
                new BigDecimal("840.00")
        );
    }
}