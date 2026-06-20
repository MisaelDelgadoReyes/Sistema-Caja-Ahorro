package com.grupo5.caja_ahorro.service;

import com.grupo5.caja_ahorro.model.Credito;
import com.grupo5.caja_ahorro.model.Cuota;
import com.grupo5.caja_ahorro.model.EstadoCredito;
import com.grupo5.caja_ahorro.model.EstadoCuota;
import com.grupo5.caja_ahorro.repository.CreditoRepository;
import com.grupo5.caja_ahorro.repository.CuotaRepository;
import com.grupo5.caja_ahorro.request.AprobarCreditoRequest;
import com.grupo5.caja_ahorro.request.PagoCuotaRequest;
import com.grupo5.caja_ahorro.request.RechazarCreditoRequest;
import com.grupo5.caja_ahorro.request.SolicitudCreditoRequest;
import com.grupo5.caja_ahorro.response.CuotaAmortizacionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class CreditoServiceImpl implements ICreditoService {

    private final CreditoRepository creditoRepository;
    private final CuotaRepository cuotaRepository;
    private final IAmortizacionService amortizacionService;

    public CreditoServiceImpl(
            CreditoRepository creditoRepository,
            CuotaRepository cuotaRepository,
            IAmortizacionService amortizacionService
    ) {
        this.creditoRepository = creditoRepository;
        this.cuotaRepository = cuotaRepository;
        this.amortizacionService = amortizacionService;
    }

    @Override
    public List<Credito> consultarTodos() {
        return creditoRepository.findAll();
    }

    @Override
    public List<Credito> consultarPorSocio(String cedulaSocio) {
        if (cedulaSocio == null || cedulaSocio.isBlank()) {
            throw new IllegalArgumentException("La cédula del socio es obligatoria.");
        }

        return creditoRepository.findByCedulaSocioOrderByFechaSolicitudDesc(cedulaSocio);
    }

    @Override
    public Credito consultarPorId(Long idCredito) {
        return obtenerCredito(idCredito);
    }

    @Override
    @Transactional
    public Credito solicitar(SolicitudCreditoRequest request) {
        validarSolicitud(request);

        Credito credito = new Credito();
        credito.setCedulaSocio(request.getCedulaSocio());
        credito.setNumeroCuentaDesembolso(request.getNumeroCuentaDesembolso());
        credito.setMontoSolicitado(request.getMontoSolicitado());
        credito.setPlazoMeses(request.getPlazoMeses());
        credito.setTasaInteresAnual(request.getTasaInteresAnual());
        credito.setSeguroDesgravamen(request.getSeguroDesgravamen());
        credito.setSistemaAmortizacion(request.getSistemaAmortizacion());
        credito.setComentarioOficial(request.getComentarioOficial());
        credito.setEstado(EstadoCredito.PENDIENTE);
        credito.setFechaSolicitud(LocalDate.now());
        credito.setFechaCambioEstado(LocalDate.now());
        credito.setSaldoPendiente(request.getMontoSolicitado());

        return creditoRepository.save(credito);
    }

    @Override
    public List<CuotaAmortizacionResponse> simular(SolicitudCreditoRequest request) {
        validarSolicitud(request);
        return amortizacionService.simular(request);
    }

    @Override
    @Transactional
    public Credito aprobar(Long idCredito, AprobarCreditoRequest request) {
        Credito credito = obtenerCredito(idCredito);

        if (!EstadoCredito.PENDIENTE.equals(credito.getEstado())) {
            throw new IllegalStateException("Solo se puede aprobar un crédito en estado PENDIENTE.");
        }

        credito.setEstado(EstadoCredito.APROBADO);
        credito.setFechaCambioEstado(LocalDate.now());

        if (request != null && request.getComentarioOficial() != null) {
            credito.setComentarioOficial(request.getComentarioOficial());
        }

        credito.getCuotas().clear();

        List<Cuota> cuotasGeneradas = amortizacionService.generarCuotas(credito);

        for (Cuota cuota : cuotasGeneradas) {
            credito.agregarCuota(cuota);
        }

        return creditoRepository.save(credito);
    }

    @Override
    @Transactional
    public Credito rechazar(Long idCredito, RechazarCreditoRequest request) {
        Credito credito = obtenerCredito(idCredito);

        if (!EstadoCredito.PENDIENTE.equals(credito.getEstado())) {
            throw new IllegalStateException("Solo se puede rechazar un crédito en estado PENDIENTE.");
        }

        credito.setEstado(EstadoCredito.RECHAZADO);
        credito.setFechaCambioEstado(LocalDate.now());

        if (request != null && request.getMotivoRechazo() != null) {
            credito.setComentarioOficial(request.getMotivoRechazo());
        }

        return creditoRepository.save(credito);
    }

    @Override
    @Transactional
    public Credito desembolsar(Long idCredito) {
        Credito credito = obtenerCredito(idCredito);

        if (!EstadoCredito.APROBADO.equals(credito.getEstado())) {
            throw new IllegalStateException("Solo se puede desembolsar un crédito en estado APROBADO.");
        }

        if (credito.getCuotas() == null || credito.getCuotas().isEmpty()) {
            throw new IllegalStateException("No se puede desembolsar un crédito sin tabla de amortización.");
        }

        credito.setEstado(EstadoCredito.VIGENTE);
        credito.setFechaDesembolso(LocalDate.now());
        credito.setFechaCambioEstado(LocalDate.now());

        return creditoRepository.save(credito);
    }

    @Override
    public List<Cuota> consultarAmortizacion(Long idCredito) {
        obtenerCredito(idCredito);
        return cuotaRepository.findByCredito_IdCreditoOrderByNumeroCuotaAsc(idCredito);
    }

    @Override
    @Transactional
    public Cuota pagarCuota(Long idCuota, PagoCuotaRequest request) {
        Cuota cuota = cuotaRepository.findById(idCuota)
                .orElseThrow(() -> new IllegalArgumentException("No existe una cuota con el ID indicado."));

        Credito credito = cuota.getCredito();

        if (!EstadoCredito.VIGENTE.equals(credito.getEstado())
                && !EstadoCredito.EN_MORA.equals(credito.getEstado())) {
            throw new IllegalStateException("Solo se pueden pagar cuotas de créditos VIGENTES o EN_MORA.");
        }

        if (EstadoCuota.PAGADA.equals(cuota.getEstado())) {
            throw new IllegalStateException("La cuota ya se encuentra pagada.");
        }

        validarPago(request, cuota);

        LocalDate fechaPago = request.getFechaPago() != null ? request.getFechaPago() : LocalDate.now();

        cuota.setEstado(EstadoCuota.PAGADA);
        cuota.setFechaPago(fechaPago);
        cuota.setMontoPagado(request.getMontoPagado());

        BigDecimal nuevoSaldo = credito.getSaldoPendiente().subtract(cuota.getCapital());

        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            nuevoSaldo = BigDecimal.ZERO;
        }

        credito.setSaldoPendiente(nuevoSaldo);
        credito.setFechaCambioEstado(LocalDate.now());

        boolean existenCuotasPendientes = cuotaRepository.existsByCredito_IdCreditoAndEstado(
                credito.getIdCredito(),
                EstadoCuota.PENDIENTE
        );

        boolean existenCuotasVencidas = cuotaRepository.existsByCredito_IdCreditoAndEstado(
                credito.getIdCredito(),
                EstadoCuota.VENCIDA
        );

        if (!existenCuotasPendientes && !existenCuotasVencidas) {
            credito.setEstado(EstadoCredito.LIQUIDADO);
            credito.setFechaCambioEstado(LocalDate.now());
        } else if (EstadoCredito.EN_MORA.equals(credito.getEstado()) && !existenCuotasVencidas) {
            credito.setEstado(EstadoCredito.VIGENTE);
            credito.setFechaCambioEstado(LocalDate.now());
        }

        creditoRepository.save(credito);
        return cuotaRepository.save(cuota);
    }

    @Override
    @Transactional
    public Credito marcarMora(Long idCredito) {
        Credito credito = obtenerCredito(idCredito);

        if (!EstadoCredito.VIGENTE.equals(credito.getEstado())) {
            throw new IllegalStateException("Solo se puede marcar en mora un crédito VIGENTE.");
        }

        List<Cuota> cuotasPendientes = cuotaRepository.findByCredito_IdCreditoAndEstadoOrderByNumeroCuotaAsc(
                idCredito,
                EstadoCuota.PENDIENTE
        );

        boolean tieneCuotasVencidas = false;
        LocalDate fechaActual = LocalDate.now();

        for (Cuota cuota : cuotasPendientes) {
            if (cuota.getFechaVencimiento().isBefore(fechaActual)) {
                cuota.setEstado(EstadoCuota.VENCIDA);
                cuotaRepository.save(cuota);
                tieneCuotasVencidas = true;
            }
        }

        if (!tieneCuotasVencidas) {
            throw new IllegalStateException("El crédito no tiene cuotas vencidas.");
        }

        credito.setEstado(EstadoCredito.EN_MORA);
        credito.setFechaCambioEstado(LocalDate.now());

        return creditoRepository.save(credito);
    }

    private Credito obtenerCredito(Long idCredito) {
        if (idCredito == null) {
            throw new IllegalArgumentException("El ID del crédito es obligatorio.");
        }

        return creditoRepository.findById(idCredito)
                .orElseThrow(() -> new IllegalArgumentException("No existe un crédito con el ID indicado."));
    }

    private void validarSolicitud(SolicitudCreditoRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Los datos de la solicitud son obligatorios.");
        }

        if (request.getCedulaSocio() == null || request.getCedulaSocio().isBlank()) {
            throw new IllegalArgumentException("La cédula del socio es obligatoria.");
        }

        if (request.getCedulaSocio().length() != 10) {
            throw new IllegalArgumentException("La cédula del socio debe tener 10 dígitos.");
        }

        if (request.getMontoSolicitado() == null
                || request.getMontoSolicitado().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto solicitado debe ser mayor a cero.");
        }

        if (request.getPlazoMeses() == null || request.getPlazoMeses() <= 0) {
            throw new IllegalArgumentException("El plazo en meses debe ser mayor a cero.");
        }

        if (request.getTasaInteresAnual() == null
                || request.getTasaInteresAnual().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La tasa de interés anual no puede ser negativa.");
        }

        if (request.getSeguroDesgravamen() == null
                || request.getSeguroDesgravamen().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El seguro de desgravamen no puede ser negativo.");
        }

        if (request.getSistemaAmortizacion() == null) {
            throw new IllegalArgumentException("El sistema de amortización es obligatorio.");
        }
    }

    private void validarPago(PagoCuotaRequest request, Cuota cuota) {
        if (request == null) {
            throw new IllegalArgumentException("Los datos del pago son obligatorios.");
        }

        if (request.getMontoPagado() == null
                || request.getMontoPagado().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto pagado debe ser mayor a cero.");
        }

        if (request.getMontoPagado().compareTo(cuota.getValorCuota()) < 0) {
            throw new IllegalArgumentException("El monto pagado debe cubrir el valor total de la cuota.");
        }
    }
}