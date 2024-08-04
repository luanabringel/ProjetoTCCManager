package com.ufcg.psoft.tccmanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "professores")
public class Professor {

    @JsonProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("nome")
    @Column(name = "nome", nullable = false)
    private String nome;

    @JsonProperty("email")
    @Column(name = "email", nullable = false)
    private String email;

    @JsonProperty("quota")
    @Column(name = "quota", nullable = false)
    private int quota;

    @JsonProperty("labs")
    @ElementCollection
    @CollectionTable(name = "labs")
    private List<String> labs;

    @JsonProperty("areasDeEstudoInteressadas")
    @ManyToMany(
            cascade = CascadeType.MERGE,
            fetch = FetchType.EAGER
    )
    @JoinTable(
            name = "professores_areas_de_interesse",
            joinColumns = @JoinColumn(name = "professor_id"),
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

    @JsonProperty("temasTCCOrientados")
    @OneToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    @Builder.Default
    private Set<TemaTCC> temasTCCOrientados = new HashSet<>();

    @Builder.Default
    private List<String> notificacoes = new ArrayList<>();

}
