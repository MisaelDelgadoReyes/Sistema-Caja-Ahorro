package com.grupo5.caja_ahorro.service;

import com.grupo5.caja_ahorro.model.AsientoDiario;
import com.grupo5.caja_ahorro.model.TransaccionVentanilla;
import com.grupo5.caja_ahorro.repository.AsientoDiarioRepository;
import com.grupo5.caja_ahorro.repository.TransaccionVentanillaRepository;
import com.grupo5.caja_ahorro.request.TransaccionVentanillaRequest;
import com.grupo5.caja_ahorro.response.TransaccionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ContabilidadServiceImpl implements ContabilidadService {

    private final TransaccionVentanillaRepository ventanillaRepo;
    private final AsientoDiarioRepository diarioRepo;

    // Constructor para inyección de dependencias
    public ContabilidadServiceImpl(TransaccionVentanillaRepository ventanillaRepo, AsientoDiarioRepository diarioRepo) {
        this.ventanillaRepo = ventanillaRepo;
        this.diarioRepo = diarioRepo;
    }

    @Override
    @Transactional // ¡Súper importante para consistencia financiera!
    public TransaccionResponse procesarTransaccion(TransaccionVentanillaRequest request) {
        LocalDateTime ahora = LocalDateTime.now();

        
        TransaccionVentanilla transaccion = new TransaccionVentanilla();
        transaccion.setSocioId(request.getSocioId());
        transaccion.setTipoTransaccion(request.getTipoTransaccion());
        transaccion.setMonto(request.getMonto());
        transaccion.setFechaHora(ahora);
        
        TransaccionVentanilla transaccionGuardada = ventanillaRepo.save(transaccion);

        
        
        AsientoDiario asientoDebe = new AsientoDiario();
        asientoDebe.setFecha(ahora);
        asientoDebe.setDescripcion(request.getTipoTransaccion() + " - Socio: " + request.getSocioId());
        asientoDebe.setCuentaContable(request.getCuentaContable()); 
        asientoDebe.setDebe(request.getMonto());
        asientoDebe.setHaber(BigDecimal.ZERO);
        asientoDebe.setTransaccionVentanilla(transaccionGuardada);
        diarioRepo.save(asientoDebe);

        
        AsientoDiario asientoHaber = new AsientoDiario();
        asientoHaber.setFecha(ahora);
        asientoHaber.setDescripcion("Contrapartida " + request.getTipoTransaccion() + " - Socio: " + request.getSocioId());
        
        asientoHaber.setCuentaContable("APORTACIONES_SOCIOS"); 
        asientoHaber.setDebe(BigDecimal.ZERO);
        asientoHaber.setHaber(request.getMonto());
        asientoHaber.setTransaccionVentanilla(transaccionGuardada);
        diarioRepo.save(asientoHaber);

        // 3. Retornar respuesta exitosa
        return new TransaccionResponse(transaccionGuardada.getId(), "Transacción y asientos contables registrados con éxito", ahora);
    }
}