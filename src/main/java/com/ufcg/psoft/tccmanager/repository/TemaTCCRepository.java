package com.ufcg.psoft.tccmanager.repository;

import com.ufcg.psoft.tccmanager.model.TemaTCC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemaTCCRepository extends JpaRepository<TemaTCC, Long> {
}
