package com.grupo5.caja_ahorro.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "libro_diario")
public class AsientoDiario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false, length = 255)
    private String descripcion;

    @Column(name = "cuenta_contable", nullable = false)
    private String cuentaContable; 

    @Column(nullable = false)
    private BigDecimal debe;

    @Column(nullable = false)
    private BigDecimal haber;

    
    @ManyToOne
    @JoinColumn(name = "transaccion_ventanilla_id")
    private TransaccionVentanilla transaccionVentanilla;

    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCuentaContable() { return cuentaContable; }
    public void setCuentaContable(String cuentaContable) { this.cuentaContable = cuentaContable; }

    public BigDecimal getDebe() { return debe; }
    public void setDebe(BigDecimal debe) { this.debe = debe; }

    public BigDecimal getHaber() { return haber; }
    public void setHaber(BigDecimal haber) { this.haber = haber; }

    public TransaccionVentanilla getTransaccionVentanilla() { return transaccionVentanilla; }
    public void setTransaccionVentanilla(TransaccionVentanilla transaccionVentanilla) { this.transaccionVentanilla = transaccionVentanilla; }
}