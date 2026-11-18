package com.tecsup.exception;

/** Regla de negocio no cumplida (responde 409 Conflict). */
public class BusinessException extends RuntimeException {

    public BusinessException(String mensaje) {
        super(mensaje);
    }
}
