package com.ufcg.psoft.tccmanager.dto.professor;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorAtualizaQuotaDto {

    @JsonProperty("quota")
    @NotNull(message = "Informar a quota é obrigatório.")
    private Integer quota;
}
