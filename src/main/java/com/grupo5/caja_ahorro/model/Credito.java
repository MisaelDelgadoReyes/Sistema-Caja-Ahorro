package com.grupo5.caja_ahorro.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "creditos")
public class Credito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCredito;

    @Column(nullable = false, length = 10)
    private String cedulaSocio;

    @Column(length = 30)
    private String numeroCuentaDesembolso;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montoSolicitado;

    @Column(nullable = false)
    private Integer plazoMeses;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal tasaInteresAnual;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal seguroDesgravamen;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SistemaAmortizacion sistemaAmortizacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCredito estado;

    @Column(nullable = false)
    private LocalDate fechaSolicitud;

    private LocalDate fechaCambioEstado;

    private LocalDate fechaDesembolso;

    @Column(precision = 12, scale = 2)
    private BigDecimal saldoPendiente;

    @Column(length = 255)
    private String comentarioOficial;

    @JsonManagedReference
    @OneToMany(mappedBy = "credito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cuota> cuotas = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (estado == null) {
            estado = EstadoCredito.PENDIENTE;
        }

        if (fechaSolicitud == null) {
            fechaSolicitud = LocalDate.now();
        }

        if (fechaCambioEstado == null) {
            fechaCambioEstado = LocalDate.now();
        }

        if (saldoPendiente == null) {
            saldoPendiente = montoSolicitado;
        }
    }

    public void agregarCuota(Cuota cuota) {
        cuotas.add(cuota);
        cuota.setCredito(this);
    }

    public Long getIdCredito() {
        return idCredito;
    }

    public void setIdCredito(Long idCredito) {
        this.idCredito = idCredito;
    }

    public String getCedulaSocio() {
        return cedulaSocio;
    }

    public void setCedulaSocio(String cedulaSocio) {
        this.cedulaSocio = cedulaSocio;
    }

    public String getNumeroCuentaDesembolso() {
        return numeroCuentaDesembolso;
    }

    public void setNumeroCuentaDesembolso(String numeroCuentaDesembolso) {
        this.numeroCuentaDesembolso = numeroCuentaDesembolso;
    }

    public BigDecimal getMontoSolicitado() {
        return montoSolicitado;
    }

    public void setMontoSolicitado(BigDecimal montoSolicitado) {
        this.montoSolicitado = montoSolicitado;
    }

    public Integer getPlazoMeses() {
        return plazoMeses;
    }

    public void setPlazoMeses(Integer plazoMeses) {
        this.plazoMeses = plazoMeses;
    }

    public BigDecimal getTasaInteresAnual() {
        return tasaInteresAnual;
    }

    public void setTasaInteresAnual(BigDecimal tasaInteresAnual) {
        this.tasaInteresAnual = tasaInteresAnual;
    }

    public BigDecimal getSeguroDesgravamen() {
        return seguroDesgravamen;
    }

    public void setSeguroDesgravamen(BigDecimal seguroDesgravamen) {
        this.seguroDesgravamen = seguroDesgravamen;
    }

    public SistemaAmortizacion getSistemaAmortizacion() {
        return sistemaAmortizacion;
    }

    public void setSistemaAmortizacion(SistemaAmortizacion sistemaAmortizacion) {
        this.sistemaAmortizacion = sistemaAmortizacion;
    }

    public EstadoCredito getEstado() {
        return estado;
    }

    public void setEstado(EstadoCredito estado) {
        this.estado = estado;
    }

    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDate fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDate getFechaCambioEstado() {
        return fechaCambioEstado;
    }

    public void setFechaCambioEstado(LocalDate fechaCambioEstado) {
        this.fechaCambioEstado = fechaCambioEstado;
    }

    public LocalDate getFechaDesembolso() {
        return fechaDesembolso;
    }

    public void setFechaDesembolso(LocalDate fechaDesembolso) {
        this.fechaDesembolso = fechaDesembolso;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(BigDecimal saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }

    public String getComentarioOficial() {
        return comentarioOficial;
    }

    public void setComentarioOficial(String comentarioOficial) {
        this.comentarioOficial = comentarioOficial;
    }

    public List<Cuota> getCuotas() {
        return cuotas;
    }

    public void setCuotas(List<Cuota> cuotas) {
        this.cuotas = cuotas;
    }
}