package com.ufcg.psoft.tccmanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.tccmanager.model.enums.SolicitacaoEnum;
import com.ufcg.psoft.tccmanager.model.id.SolicitacaoId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "solicitacoes")
@IdClass(SolicitacaoId.class)
public class Solicitacao {

    @Id
    @JsonProperty("alunoId")
    @ManyToOne
    @JoinColumn(nullable = false)
    private Aluno aluno;

    @Id
    @JsonProperty("professorId")
    @ManyToOne
    @JoinColumn(nullable = false)
    private Professor professor;

    @Id
    @JsonProperty("tccId")
    @OneToOne
    @JoinColumn(nullable = false)
    private TemaTCC temaTCC;

    @JsonProperty("status")
    @Enumerated(EnumType.STRING)
    private SolicitacaoEnum status;

}
