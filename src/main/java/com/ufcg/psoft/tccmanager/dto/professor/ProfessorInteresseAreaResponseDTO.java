package com.ufcg.psoft.tccmanager.dto.professor;

import com.ufcg.psoft.tccmanager.model.AreaDeEstudo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfessorInteresseAreaResponseDTO {

    private Set<AreaDeEstudo> areasDeEstudoInteressadas;

}
