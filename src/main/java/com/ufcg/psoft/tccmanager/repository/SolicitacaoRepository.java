package com.ufcg.psoft.tccmanager.repository;

import com.ufcg.psoft.tccmanager.model.Aluno;
import com.ufcg.psoft.tccmanager.model.Professor;
import com.ufcg.psoft.tccmanager.model.Solicitacao;
import com.ufcg.psoft.tccmanager.model.TemaTCC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {
    Solicitacao findByProfessorAndAlunoAndTemaTCC(Professor professor, Aluno aluno, TemaTCC temaTCC);
}