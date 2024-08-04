package com.ufcg.psoft.tccmanager.exception;

public class CoordenadorAlreadyRegistered extends ManagerException {
    public CoordenadorAlreadyRegistered() {
        super("Só pode haver um coordenador cadastrado no sistema.");
    }
}
