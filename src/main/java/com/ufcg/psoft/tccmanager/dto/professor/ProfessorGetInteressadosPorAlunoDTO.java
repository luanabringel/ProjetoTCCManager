package com.ufcg.psoft.tccmanager.dto.professor;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorGetInteressadosPorAlunoDTO {
    @JsonProperty("nome")
    @NotBlank(message = "Informar o nome é obrigatório.")
    private String nome;

    @JsonProperty("email")
    @Email
    @NotBlank(message = "Informar o email é obrigatório.")
    private String email;
}
