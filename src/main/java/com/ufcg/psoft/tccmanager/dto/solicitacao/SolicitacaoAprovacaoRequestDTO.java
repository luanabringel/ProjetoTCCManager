package com.ufcg.psoft.tccmanager.dto.solicitacao;

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
public class SolicitacaoAprovacaoRequestDTO {

    @JsonProperty("professorId")
    @NotNull(message = "Informar o ID do professor é obrigatório")
    private Long professorId;

    @JsonProperty("alunoId")
    @NotNull(message = "Informar o ID do aluno é obrigatório")
    private Long alunoId;

    @JsonProperty("temaTccId")
    @NotNull(message = "Informar o ID do tema de TCC é obrigatório")
    private Long temaTccId;

    @JsonProperty("aprovacao")
    @NotNull(message = "Informar a aprovação do tema de TCC é obrigatório")
    private Boolean aprovacao;

    @JsonProperty("mensagemDecisao")
    @NotBlank(message = "Informar uma mensagem a respeito da sua decisão é obrigatório")
    private String mensagemDecisao;

}
