package com.ufcg.psoft.tccmanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "alunos")
public class Aluno {

    @Id
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @JsonProperty("nome")
    @Column(name = "nome", nullable = false)
    private String nome;

    @JsonProperty("matricula")
    @Column(name = "matricula", nullable = false)
    private String matricula;

    @JsonProperty("email")
    @Column(name = "email", nullable = false)
    private String email;

    @JsonProperty("periodoConclusao")
    @Column(name = "periodoConclusao", nullable = false)
    private String periodoConclusao;

    @JsonProperty("areasDeEstudoInteressadas")
    @ManyToMany(
            cascade = CascadeType.MERGE,
            fetch = FetchType.EAGER
    )
    @JoinTable(
            name = "alunos_areas_de_interesse",
            joinColumns = @JoinColumn(name = "aluno_id"),
            inverseJoinColumns = @JoinColumn(name = "area_de_estudo_id")
    )
    @Builder.Default
    private Set<AreaDeEstudo> areasDeEstudoInteressadas = new HashSet<>();

    @JsonProperty("temasTCCCadastrados")
    @OneToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    @Builder.Default
    private Set<TemaTCC> temasTCCCadastrados = new HashSet<>();

    @Builder.Default
    private List<String> notificacoes = new ArrayList<>();

}
