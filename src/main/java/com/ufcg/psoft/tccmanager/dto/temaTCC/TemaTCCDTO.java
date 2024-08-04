package com.ufcg.psoft.tccmanager.dto.temaTCC;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.tccmanager.model.AreaDeEstudo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemaTCCDTO {

    @JsonProperty("titulo")
    @NotBlank(message = "Informar o titulo é obrigatório")
    private String titulo;

    @JsonProperty("descricao")
    private String descricao;

    @JsonProperty("areasDeEstudo")
    @NotNull(message = "Informar as áreas de estudo é obrigatório")
    private Set<AreaDeEstudo> areasDeEstudo;

}
