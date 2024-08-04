package com.ufcg.psoft.tccmanager.exception;

public class CoordenadorNotRegistered extends NotFoundException {
    public CoordenadorNotRegistered() {
        super("O coordenador ainda não foi cadastrado no sistema.");
    }
}
