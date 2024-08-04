package com.ufcg.psoft.tccmanager.dto.solicitacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.tccmanager.model.TemaTCC;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoAprovacaoResponseDTO {

    @JsonProperty("professorId")
    private Long professorId;

    @JsonProperty("alunoId")
    private Long alunoId;

    @JsonProperty("temaTCC")
    private TemaTCC temaTCC;

    @JsonProperty("aprovacao")
    private Boolean aprovacao;

    @JsonProperty("notificacaoCoordenador")
    private String notificacaoCoordenador;

    @JsonProperty("mensagemDecisao")
    private String mensagemDecisao;

}
