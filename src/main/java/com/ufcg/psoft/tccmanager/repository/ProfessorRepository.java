package com.ufcg.psoft.tccmanager.repository;

import com.ufcg.psoft.tccmanager.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
}