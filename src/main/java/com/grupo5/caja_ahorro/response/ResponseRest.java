package com.grupo5.caja_ahorro.response;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class ResponseRest<T> {
    private List<T> data = new ArrayList<>();
    private List<ErrorRest> errors = new ArrayList<>();

    public void addData(T item) {
        this.data.add(item);
    }

    public void addError(int codigo, String mensaje, String campo) {
        this.errors.add(new ErrorRest(codigo, mensaje, campo));
    }
}