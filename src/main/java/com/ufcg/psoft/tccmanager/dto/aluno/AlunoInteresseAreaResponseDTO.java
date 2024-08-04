package com.ufcg.psoft.tccmanager.dto.aluno;

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
public class AlunoInteresseAreaResponseDTO {

    private Set<AreaDeEstudo> areasDeEstudoInteressadas;

}
