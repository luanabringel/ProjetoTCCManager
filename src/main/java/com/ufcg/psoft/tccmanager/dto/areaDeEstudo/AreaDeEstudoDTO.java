package com.ufcg.psoft.tccmanager.dto.areaDeEstudo;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AreaDeEstudoDTO {

    @JsonProperty("nome")
    @NotBlank(message = "Informar o nome da área de estudo é obrigatório.")
    private String nome;

}