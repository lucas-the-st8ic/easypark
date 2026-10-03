package com.br.lucasthest8ic.easypark.exception;

public class CantBeNullOrBlankException extends RuntimeException {
    public CantBeNullOrBlankException(String message) {
        super(message);
    }
}
