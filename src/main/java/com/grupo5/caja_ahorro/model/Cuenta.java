package com.grupo5.caja_ahorro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cuentas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuenta")
    private Long idCuenta;

    @Column(nullable = false, unique = true, length = 20)
    private String numeroCuenta;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal saldo;

    @Column(nullable = false)
    private LocalDate fechaApertura;

    @Column(nullable = false)
    private Boolean activa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_socio", nullable = false)
    private Socio socio;

    @PrePersist
    public void prePersist() {

        if (fechaApertura == null) {
            fechaApertura = LocalDate.now();
        }

        if (saldo == null) {
            saldo = BigDecimal.ZERO;
        }

        if (activa == null) {
            activa = true;
        }
    }
}