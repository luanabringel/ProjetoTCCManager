package com.ufcg.psoft.tccmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "areas_de_estudo")
public class AreaDeEstudo {

    @Id
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("nome")
    @Column(name = "nome", nullable = false)
    private String nome;

    @JsonIgnore
    @ManyToMany(
            mappedBy = "areasDeEstudoInteressadas",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST}
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Aluno> alunosInteressados;

    @JsonIgnore
    @ManyToMany(
            mappedBy = "areasDeEstudoInteressadas",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST}
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Professor> professoresInteressados;

    @JsonIgnore
    @ManyToMany(
            mappedBy = "areasDeEstudo",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST}
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<TemaTCC> temasTccComAreaDeEstudo;
}