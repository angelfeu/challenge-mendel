package com.prueba.mendel.exception;

public class CircularReferenceException extends RuntimeException {

    public CircularReferenceException(Long id) {
        super("Circular reference detected for transaction id: " + id);
    }
}
