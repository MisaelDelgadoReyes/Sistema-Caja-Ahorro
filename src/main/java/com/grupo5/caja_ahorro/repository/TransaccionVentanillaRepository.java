package com.grupo5.caja_ahorro.repository;

import com.grupo5.caja_ahorro.model.TransaccionVentanilla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransaccionVentanillaRepository extends JpaRepository<TransaccionVentanilla, Long> {
    
}