package com.ufcg.psoft.tccmanager.exception;

public class ManagerException extends RuntimeException {
    public ManagerException() {
        super("Erro inesperado no TCC Manager!");
    }

    public ManagerException(String message) {
        super(message);
    }
}
