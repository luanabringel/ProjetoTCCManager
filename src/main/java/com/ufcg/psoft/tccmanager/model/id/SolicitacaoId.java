package com.ufcg.psoft.tccmanager.model.id;

import com.ufcg.psoft.tccmanager.model.Aluno;
import com.ufcg.psoft.tccmanager.model.Professor;
import com.ufcg.psoft.tccmanager.model.TemaTCC;

import java.io.Serializable;

public class SolicitacaoId implements Serializable {

    private Aluno aluno;

    private Professor professor;

    private TemaTCC temaTCC;

}
