package com.tecsup.exception;

/** Otro microservicio no respondio (responde 503 Service Unavailable). */
public class ServicioNoDisponibleException extends RuntimeException {

    public ServicioNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
