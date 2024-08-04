package com.ufcg.psoft.tccmanager.dto.professor;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorDTO {

    @JsonProperty("nome")
    @NotBlank(message = "Informar o nome do professor é obrigatório.")
    private String nome;

    @JsonProperty("email")
    @Email
    @NotBlank(message = "Informar o email do professor é obrigatório.")
    private String email;

    @JsonProperty("quota")
    @NotNull(message = "Informar a quota é obrigatório.")
    private int quota;

    @JsonProperty("labs")
    @Builder.Default
    private List<String> labs = new ArrayList<>();

}