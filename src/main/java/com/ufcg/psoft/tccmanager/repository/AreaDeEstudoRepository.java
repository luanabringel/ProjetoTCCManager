package com.ufcg.psoft.tccmanager.repository;

import com.ufcg.psoft.tccmanager.model.AreaDeEstudo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaDeEstudoRepository extends JpaRepository<AreaDeEstudo, Long> {
    boolean existsByNome(String nome);
}
