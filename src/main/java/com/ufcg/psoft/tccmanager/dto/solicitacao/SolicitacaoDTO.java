package com.ufcg.psoft.tccmanager.dto.solicitacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SolicitacaoDTO {

    @JsonProperty("alunoId")
    @NotNull(message = "id do aluno não pode ser nulo")
    private Long alunoId;

    @JsonProperty("professorId")
    @NotNull(message = "id do professor não pode ser nulo")
    private Long professorId;

    @JsonProperty("temaTCCId")
    @NotNull(message = "id do tcc não pode ser nulo")
    private Long temaTCCId;

}
