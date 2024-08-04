package com.ufcg.psoft.tccmanager.exception;

public class AreaDeEstudoAlreadyExistsException extends ManagerException {
    public AreaDeEstudoAlreadyExistsException(){
        super("Já existe uma área de estudo com esse nome!");
    }
}
