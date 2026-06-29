package com.grupo5.caja_ahorro.service;

import com.grupo5.caja_ahorro.model.Credito;
import com.grupo5.caja_ahorro.model.Cuota;
import com.grupo5.caja_ahorro.model.EstadoCredito;
import com.grupo5.caja_ahorro.model.EstadoCuota;
import com.grupo5.caja_ahorro.model.SistemaAmortizacion;
import com.grupo5.caja_ahorro.repository.CreditoRepository;
import com.grupo5.caja_ahorro.repository.CuotaRepository;
import com.grupo5.caja_ahorro.request.AprobarCreditoRequest;
import com.grupo5.caja_ahorro.request.PagoCuotaRequest;
import com.grupo5.caja_ahorro.request.RechazarCreditoRequest;
import com.grupo5.caja_ahorro.request.SolicitudCreditoRequest;
import com.grupo5.caja_ahorro.response.CuotaAmortizacionResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditoServiceImplTest {

    @Mock
    private CreditoRepository creditoRepository;

    @Mock
    private CuotaRepository cuotaRepository;

    @Mock
    private IAmortizacionService amortizacionService;

    @InjectMocks
    private CreditoServiceImpl creditoService;

    @Test
    void consultarTodosDebeRetornarCreditosRegistrados() {
        Credito credito = crearCredito(EstadoCredito.PENDIENTE);
        when(creditoRepository.findAll()).thenReturn(List.of(credito));

        List<Credito> resultado = creditoService.consultarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstado()).isEqualTo(EstadoCredito.PENDIENTE);
        verify(creditoRepository).findAll();
    }

    @Test
    void consultarPorSocioDebeValidarCedulaObligatoria() {
        assertThatThrownBy(() -> creditoService.consultarPorSocio(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cédula");

        verify(creditoRepository, never()).findByCedulaSocioOrderByFechaSolicitudDesc(any());
    }

    @Test
    void consultarPorIdDebeLanzarErrorCuandoNoExisteCredito() {
        when(creditoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> creditoService.consultarPorId(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No existe un crédito");
    }

    @Test
    void solicitarDebeCrearCreditoEnEstadoPendiente() {
        SolicitudCreditoRequest request = crearSolicitud();
        when(creditoRepository.save(any(Credito.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Credito resultado = creditoService.solicitar(request);

        assertThat(resultado.getCedulaSocio()).isEqualTo("0923456789");
        assertThat(resultado.getEstado()).isEqualTo(EstadoCredito.PENDIENTE);
        assertThat(resultado.getSaldoPendiente()).isEqualByComparingTo("1000.00");
        verify(creditoRepository).save(any(Credito.class));
    }

    @Test
    void solicitarDebeLanzarErrorCuandoCedulaTieneLongitudIncorrecta() {
        SolicitudCreditoRequest request = crearSolicitud();
        request.setCedulaSocio("123");

        assertThatThrownBy(() -> creditoService.solicitar(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("10 dígitos");

        verify(creditoRepository, never()).save(any(Credito.class));
    }

    @Test
    void simularDebeDelegarLaGeneracionDeTablaAlServicioDeAmortizacion() {
        SolicitudCreditoRequest request = crearSolicitud();
        CuotaAmortizacionResponse cuota = new CuotaAmortizacionResponse(
                1,
                LocalDate.now().plusMonths(1),
                new BigDecimal("160.00"),
                new BigDecimal("10.00"),
                new BigDecimal("5.00"),
                new BigDecimal("175.00"),
                new BigDecimal("840.00")
        );

        when(amortizacionService.simular(request)).thenReturn(List.of(cuota));

        List<CuotaAmortizacionResponse> resultado = creditoService.simular(request);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNumeroCuota()).isEqualTo(1);
        verify(amortizacionService).simular(request);
    }

    @Test
    void aprobarDebeCambiarEstadoYGenerarCuotas() {
        Credito credito = crearCredito(EstadoCredito.PENDIENTE);
        AprobarCreditoRequest request = new AprobarCreditoRequest();
        request.setComentarioOficial("Aprobado en prueba unitaria");

        Cuota cuota = crearCuota(credito, EstadoCuota.PENDIENTE);

        when(creditoRepository.findById(1L)).thenReturn(Optional.of(credito));
        when(amortizacionService.generarCuotas(credito)).thenReturn(List.of(cuota));
        when(creditoRepository.save(any(Credito.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Credito resultado = creditoService.aprobar(1L, request);

        assertThat(resultado.getEstado()).isEqualTo(EstadoCredito.APROBADO);
        assertThat(resultado.getComentarioOficial()).isEqualTo("Aprobado en prueba unitaria");
        assertThat(resultado.getCuotas()).hasSize(1);
        verify(creditoRepository).save(credito);
    }

    @Test
    void aprobarDebeLanzarErrorSiCreditoNoEstaPendiente() {
        Credito credito = crearCredito(EstadoCredito.VIGENTE);
        when(creditoRepository.findById(1L)).thenReturn(Optional.of(credito));

        assertThatThrownBy(() -> creditoService.aprobar(1L, new AprobarCreditoRequest()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("PENDIENTE");

        verify(amortizacionService, never()).generarCuotas(any());
        verify(creditoRepository, never()).save(any(Credito.class));
    }

    @Test
    void rechazarDebeCambiarEstadoDelCreditoPendiente() {
        Credito credito = crearCredito(EstadoCredito.PENDIENTE);
        RechazarCreditoRequest request = new RechazarCreditoRequest();
        request.setMotivoRechazo("No cumple requisitos");

        when(creditoRepository.findById(1L)).thenReturn(Optional.of(credito));
        when(creditoRepository.save(any(Credito.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Credito resultado = creditoService.rechazar(1L, request);

        assertThat(resultado.getEstado()).isEqualTo(EstadoCredito.RECHAZADO);
        assertThat(resultado.getComentarioOficial()).isEqualTo("No cumple requisitos");
        verify(creditoRepository).save(credito);
    }

    @Test
    void desembolsarDebeCambiarCreditoAEstadoVigente() {
        Credito credito = crearCredito(EstadoCredito.APROBADO);
        credito.agregarCuota(crearCuota(credito, EstadoCuota.PENDIENTE));

        when(creditoRepository.findById(1L)).thenReturn(Optional.of(credito));
        when(creditoRepository.save(any(Credito.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Credito resultado = creditoService.desembolsar(1L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoCredito.VIGENTE);
        assertThat(resultado.getFechaDesembolso()).isNotNull();
        verify(creditoRepository).save(credito);
    }

    @Test
    void desembolsarDebeLanzarErrorSiNoTieneTablaDeAmortizacion() {
        Credito credito = crearCredito(EstadoCredito.APROBADO);
        when(creditoRepository.findById(1L)).thenReturn(Optional.of(credito));

        assertThatThrownBy(() -> creditoService.desembolsar(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("tabla de amortización");

        verify(creditoRepository, never()).save(any(Credito.class));
    }

    @Test
    void consultarAmortizacionDebeRetornarCuotasDelCredito() {
        Credito credito = crearCredito(EstadoCredito.APROBADO);
        Cuota cuota = crearCuota(credito, EstadoCuota.PENDIENTE);

        when(creditoRepository.findById(1L)).thenReturn(Optional.of(credito));
        when(cuotaRepository.findByCredito_IdCreditoOrderByNumeroCuotaAsc(1L)).thenReturn(List.of(cuota));

        List<Cuota> resultado = creditoService.consultarAmortizacion(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNumeroCuota()).isEqualTo(1);
    }

    @Test
    void pagarCuotaDebeRegistrarPagoYLiquidarCreditoCuandoNoHayPendientes() {
        Credito credito = crearCredito(EstadoCredito.VIGENTE);
        credito.setSaldoPendiente(new BigDecimal("100.00"));

        Cuota cuota = crearCuota(credito, EstadoCuota.PENDIENTE);
        cuota.setCapital(new BigDecimal("100.00"));
        cuota.setValorCuota(new BigDecimal("110.00"));

        PagoCuotaRequest request = new PagoCuotaRequest();
        request.setMontoPagado(new BigDecimal("110.00"));
        request.setFechaPago(LocalDate.now());

        when(cuotaRepository.findById(1L)).thenReturn(Optional.of(cuota));
        when(cuotaRepository.existsByCredito_IdCreditoAndEstado(1L, EstadoCuota.PENDIENTE)).thenReturn(false);
        when(cuotaRepository.existsByCredito_IdCreditoAndEstado(1L, EstadoCuota.VENCIDA)).thenReturn(false);
        when(cuotaRepository.save(any(Cuota.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(creditoRepository.save(any(Credito.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Cuota resultado = creditoService.pagarCuota(1L, request);

        assertThat(resultado.getEstado()).isEqualTo(EstadoCuota.PAGADA);
        assertThat(resultado.getMontoPagado()).isEqualByComparingTo("110.00");
        assertThat(credito.getSaldoPendiente()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(credito.getEstado()).isEqualTo(EstadoCredito.LIQUIDADO);
        verify(creditoRepository).save(credito);
        verify(cuotaRepository).save(cuota);
    }

    @Test
    void pagarCuotaDebeLanzarErrorSiMontoNoCubreValorTotal() {
        Credito credito = crearCredito(EstadoCredito.VIGENTE);
        Cuota cuota = crearCuota(credito, EstadoCuota.PENDIENTE);

        PagoCuotaRequest request = new PagoCuotaRequest();
        request.setMontoPagado(new BigDecimal("50.00"));

        when(cuotaRepository.findById(1L)).thenReturn(Optional.of(cuota));

        assertThatThrownBy(() -> creditoService.pagarCuota(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("valor total");

        verify(cuotaRepository, never()).save(any(Cuota.class));
    }

    @Test
    void marcarMoraDebeVencerCuotasAtrasadasYCambiarEstado() {
        Credito credito = crearCredito(EstadoCredito.VIGENTE);

        Cuota cuotaVencida = crearCuota(credito, EstadoCuota.PENDIENTE);
        cuotaVencida.setFechaVencimiento(LocalDate.now().minusDays(1));

        when(creditoRepository.findById(1L)).thenReturn(Optional.of(credito));
        when(cuotaRepository.findByCredito_IdCreditoAndEstadoOrderByNumeroCuotaAsc(
                1L,
                EstadoCuota.PENDIENTE
        )).thenReturn(List.of(cuotaVencida));

        when(creditoRepository.save(any(Credito.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(cuotaRepository.save(any(Cuota.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Credito resultado = creditoService.marcarMora(1L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoCredito.EN_MORA);
        assertThat(cuotaVencida.getEstado()).isEqualTo(EstadoCuota.VENCIDA);
        verify(cuotaRepository).save(cuotaVencida);
        verify(creditoRepository).save(credito);
    }

    @Test
    void marcarMoraDebeLanzarErrorSiNoExistenCuotasVencidas() {
        Credito credito = crearCredito(EstadoCredito.VIGENTE);

        Cuota cuotaPendiente = crearCuota(credito, EstadoCuota.PENDIENTE);
        cuotaPendiente.setFechaVencimiento(LocalDate.now().plusDays(5));

        when(creditoRepository.findById(1L)).thenReturn(Optional.of(credito));
        when(cuotaRepository.findByCredito_IdCreditoAndEstadoOrderByNumeroCuotaAsc(
                1L,
                EstadoCuota.PENDIENTE
        )).thenReturn(List.of(cuotaPendiente));

        assertThatThrownBy(() -> creditoService.marcarMora(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no tiene cuotas vencidas");

        verify(creditoRepository, never()).save(any(Credito.class));
    }

    @Test
    void consultarPorSocioDebeRetornarCreditosCuandoCedulaEsValida() {
        Credito credito = crearCredito(EstadoCredito.VIGENTE);

        when(creditoRepository.findByCedulaSocioOrderByFechaSolicitudDesc("0923456789"))
                .thenReturn(List.of(credito));

        List<Credito> resultado = creditoService.consultarPorSocio("0923456789");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCedulaSocio()).isEqualTo("0923456789");
        verify(creditoRepository).findByCedulaSocioOrderByFechaSolicitudDesc("0923456789");
    }

    @Test
    void obtenerCreditoDebeLanzarErrorCuandoIdEsNulo() {
        assertThatThrownBy(() -> creditoService.consultarPorId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID del crédito");

        verify(creditoRepository, never()).findById(any());
    }

    @Test
    void rechazarDebeLanzarErrorSiCreditoNoEstaPendiente() {
        Credito credito = crearCredito(EstadoCredito.APROBADO);
        when(creditoRepository.findById(1L)).thenReturn(Optional.of(credito));

        assertThatThrownBy(() -> creditoService.rechazar(1L, new RechazarCreditoRequest()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("PENDIENTE");

        verify(creditoRepository, never()).save(any(Credito.class));
    }

    @Test
    void desembolsarDebeLanzarErrorSiCreditoNoEstaAprobado() {
        Credito credito = crearCredito(EstadoCredito.PENDIENTE);
        when(creditoRepository.findById(1L)).thenReturn(Optional.of(credito));

        assertThatThrownBy(() -> creditoService.desembolsar(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("APROBADO");

        verify(creditoRepository, never()).save(any(Credito.class));
    }

    @Test
    void pagarCuotaDebeLanzarErrorSiCreditoNoEstaVigenteNiEnMora() {
        Credito credito = crearCredito(EstadoCredito.PENDIENTE);
        Cuota cuota = crearCuota(credito, EstadoCuota.PENDIENTE);

        PagoCuotaRequest request = new PagoCuotaRequest();
        request.setMontoPagado(new BigDecimal("175.00"));

        when(cuotaRepository.findById(1L)).thenReturn(Optional.of(cuota));

        assertThatThrownBy(() -> creditoService.pagarCuota(1L, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("VIGENTES o EN_MORA");

        verify(cuotaRepository, never()).save(any(Cuota.class));
    }

    @Test
    void pagarCuotaDebeLanzarErrorSiCuotaYaEstaPagada() {
        Credito credito = crearCredito(EstadoCredito.VIGENTE);
        Cuota cuota = crearCuota(credito, EstadoCuota.PAGADA);

        PagoCuotaRequest request = new PagoCuotaRequest();
        request.setMontoPagado(new BigDecimal("175.00"));

        when(cuotaRepository.findById(1L)).thenReturn(Optional.of(cuota));

        assertThatThrownBy(() -> creditoService.pagarCuota(1L, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ya se encuentra pagada");

        verify(cuotaRepository, never()).save(any(Cuota.class));
    }

    @Test
    void pagarCuotaDebeLanzarErrorCuandoRequestEsNulo() {
        Credito credito = crearCredito(EstadoCredito.VIGENTE);
        Cuota cuota = crearCuota(credito, EstadoCuota.PENDIENTE);

        when(cuotaRepository.findById(1L)).thenReturn(Optional.of(cuota));

        assertThatThrownBy(() -> creditoService.pagarCuota(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("datos del pago");

        verify(cuotaRepository, never()).save(any(Cuota.class));
    }

    @Test
    void pagarCuotaDebeLanzarErrorCuandoMontoEsCero() {
        Credito credito = crearCredito(EstadoCredito.VIGENTE);
        Cuota cuota = crearCuota(credito, EstadoCuota.PENDIENTE);

        PagoCuotaRequest request = new PagoCuotaRequest();
        request.setMontoPagado(BigDecimal.ZERO);

        when(cuotaRepository.findById(1L)).thenReturn(Optional.of(cuota));

        assertThatThrownBy(() -> creditoService.pagarCuota(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("monto pagado");

        verify(cuotaRepository, never()).save(any(Cuota.class));
    }

    @Test
    void marcarMoraDebeLanzarErrorSiCreditoNoEstaVigente() {
        Credito credito = crearCredito(EstadoCredito.APROBADO);
        when(creditoRepository.findById(1L)).thenReturn(Optional.of(credito));

        assertThatThrownBy(() -> creditoService.marcarMora(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("VIGENTE");

        verify(cuotaRepository, never()).findByCredito_IdCreditoAndEstadoOrderByNumeroCuotaAsc(any(), any());
        verify(creditoRepository, never()).save(any(Credito.class));
    }

    @Test
    void solicitarDebeLanzarErrorCuandoRequestEsNulo() {
        assertThatThrownBy(() -> creditoService.solicitar(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("datos de la solicitud");

        verify(creditoRepository, never()).save(any(Credito.class));
    }

    @Test
    void solicitarDebeLanzarErrorCuandoMontoEsInvalido() {
        SolicitudCreditoRequest request = crearSolicitud();
        request.setMontoSolicitado(BigDecimal.ZERO);

        assertThatThrownBy(() -> creditoService.solicitar(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("monto solicitado");

        verify(creditoRepository, never()).save(any(Credito.class));
    }

    @Test
    void solicitarDebeLanzarErrorCuandoSistemaAmortizacionEsNulo() {
        SolicitudCreditoRequest request = crearSolicitud();
        request.setSistemaAmortizacion(null);

        assertThatThrownBy(() -> creditoService.solicitar(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sistema de amortización");

        verify(creditoRepository, never()).save(any(Credito.class));
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
        request.setComentarioOficial("Prueba unitaria de crédito");
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
}