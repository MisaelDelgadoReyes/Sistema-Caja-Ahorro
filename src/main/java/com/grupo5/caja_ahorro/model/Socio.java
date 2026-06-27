package com.grupo5.caja_ahorro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "socios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Socio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_socio")
    private Long idSocio;

    @Column(nullable = false, unique = true, length = 10)
    private String cedula;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    @Column(length = 20)
    private String telefono;

    @Column(length = 200)
    private String direccion;

    @Column(nullable = false)
    private LocalDate fechaIngreso;

    @Column(nullable = false)
    private Boolean activo;

    @PrePersist
    public void prePersist() {
        if (fechaIngreso == null) {
            fechaIngreso = LocalDate.now();
        }

        if (activo == null) {
            activo = true;
        }
    }
}