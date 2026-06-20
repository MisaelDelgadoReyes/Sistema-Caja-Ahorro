package com.grupo5.caja_ahorro.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cuotas")
public class Cuota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCuota;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_credito", nullable = false)
    private Credito credito;

    @Column(nullable = false)
    private Integer numeroCuota;

    @Column(nullable = false)
    private LocalDate fechaVencimiento;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal capital;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal interes;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal seguroDesgravamen;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valorCuota;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoCapital;

    private LocalDate fechaPago;

    @Column(precision = 12, scale = 2)
    private BigDecimal montoPagado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCuota estado;

    @PrePersist
    public void prePersist() {
        if (estado == null) {
            estado = EstadoCuota.PENDIENTE;
        }

        if (montoPagado == null) {
            montoPagado = BigDecimal.ZERO;
        }
    }

    public Long getIdCuota() {
        return idCuota;
    }

    public void setIdCuota(Long idCuota) {
        this.idCuota = idCuota;
    }

    public Credito getCredito() {
        return credito;
    }

    public void setCredito(Credito credito) {
        this.credito = credito;
    }

    public Integer getNumeroCuota() {
        return numeroCuota;
    }

    public void setNumeroCuota(Integer numeroCuota) {
        this.numeroCuota = numeroCuota;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public BigDecimal getCapital() {
        return capital;
    }

    public void setCapital(BigDecimal capital) {
        this.capital = capital;
    }

    public BigDecimal getInteres() {
        return interes;
    }

    public void setInteres(BigDecimal interes) {
        this.interes = interes;
    }

    public BigDecimal getSeguroDesgravamen() {
        return seguroDesgravamen;
    }

    public void setSeguroDesgravamen(BigDecimal seguroDesgravamen) {
        this.seguroDesgravamen = seguroDesgravamen;
    }

    public BigDecimal getValorCuota() {
        return valorCuota;
    }

    public void setValorCuota(BigDecimal valorCuota) {
        this.valorCuota = valorCuota;
    }

    public BigDecimal getSaldoCapital() {
        return saldoCapital;
    }

    public void setSaldoCapital(BigDecimal saldoCapital) {
        this.saldoCapital = saldoCapital;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }

    public EstadoCuota getEstado() {
        return estado;
    }

    public void setEstado(EstadoCuota estado) {
        this.estado = estado;
    }
}