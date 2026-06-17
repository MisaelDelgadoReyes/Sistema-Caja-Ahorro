package com.grupo5.caja_ahorro.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorRest {
    
    private int codigo;
    private String mensaje;
    private String campo;
    
}
