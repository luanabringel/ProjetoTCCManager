package com.ufcg.psoft.tccmanager.dto.temaTCC;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.tccmanager.model.AreaDeEstudo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TemaTCCProfessorAlunoGetAllDTO {
    @JsonProperty("titulo")
    @NotBlank(message = "Informar o titulo é obrigatório")
    private String titulo;

    @JsonProperty("areasDeEstudo")
    @NotNull(message = "Informar as áreas de estudo é obrigatório")
    @Builder.Default
    private Set<AreaDeEstudo> areasDeEstudo = new HashSet<>();

    @JsonProperty("professorAutor")
    @NotBlank(message = "Informar o professor responsável é obrigatório")
    private String professorAutor;
}
