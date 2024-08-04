package com.ufcg.psoft.tccmanager.dto.coordenador;

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
public class CoordenadorDTO {

    @JsonProperty("nome")
    @NotBlank(message = "Informar o nome do coordenador é obrigatório.")
    private String nome;

    @JsonProperty("email")
    @Email
    @NotBlank(message = "Informar o email é obrigatório.")
    private String email;

    @JsonProperty("senha")
    @NotBlank(message = "Informar a senha é obrigatório.")
    private String senha;

}
