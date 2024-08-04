package com.ufcg.psoft.tccmanager.repository;

import com.ufcg.psoft.tccmanager.model.Coordenador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CoordenadorRepository extends JpaRepository<Coordenador, Long> {
    @Query("SELECT c FROM Coordenador c")
    Coordenador findCoordenador();
}
