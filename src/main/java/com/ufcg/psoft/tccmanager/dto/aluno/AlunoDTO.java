package com.ufcg.psoft.tccmanager.dto.aluno;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlunoDTO {

    @JsonProperty("matricula")
    @NotBlank(message = "Informar a matrícula é obrigatório.")
    private String matricula;

    @JsonProperty("nome")
    @NotBlank(message = "Informar o nome do aluno é obrigatório.")
    private String nome;

    @JsonProperty("periodoConclusao")
    @NotBlank(message = "Informar o período de conclusão é obrigatório.")
    private String periodoConclusao;

    @JsonProperty("email")
    @Email
    @NotBlank(message = "Informar o email e obrigatorio.")
    private String email;

}