package com.ufcg.psoft.tccmanager.repository;

import com.ufcg.psoft.tccmanager.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
}