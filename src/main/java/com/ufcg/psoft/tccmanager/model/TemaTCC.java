package com.ufcg.psoft.tccmanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.tccmanager.model.enums.StatusTCCEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "temas_tcc")
public class TemaTCC {

    @Id
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("status")
    @Enumerated(EnumType.STRING)
    private StatusTCCEnum status;

    @JsonProperty("titulo")
    @Column(name = "titulo", nullable = false)
    private String titulo;

    @JsonProperty("descricao")
    @Column(name = "descricao")
    private String descricao;

    @JsonProperty("areasDeEstudo")
    @ManyToMany(
            cascade = CascadeType.MERGE,
            fetch = FetchType.EAGER
    )
    @JoinTable(
            name = "temas_tcc_areas_de_estudo",
            joinColumns = @JoinColumn(name = "tema_tcc_id"),
            inverseJoinColumns = @JoinColumn(name = "area_de_estudo_id")
    )
    private Set<AreaDeEstudo> areasDeEstudo;
}
