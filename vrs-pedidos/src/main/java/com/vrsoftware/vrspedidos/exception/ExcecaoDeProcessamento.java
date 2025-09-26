package com.vrsoftware.vrspedidos.exception;

public class ExcecaoDeProcessamento extends RuntimeException {
    
    public ExcecaoDeProcessamento(String message) {
        super(message);
    }
    
    public ExcecaoDeProcessamento(String message, Throwable cause) {
        super(message, cause);
    }
}