package com.br.lucasthest8ic.easypark.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="estacionamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Estacionamento {

    @Id
    @SequenceGenerator(
            name = "estacionamento_seq_generator",      // Nome lógico interno no JPA/Hibernate
            sequenceName = "seq_estacionamentos_id",             // Nome da SEQUENCE criada no PostgreSQL
            initialValue = 1,                           // Começa em 1
            allocationSize = 1  )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "estacionamento_seq_generator"  // Aponta exatamente para o name acima
    )
    private Integer idEstacionamento;

    @Column(name = "estacionamento", nullable = false,
            unique = true)
    private String nome;

    @OneToMany(mappedBy = "estacionamento",
            cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Vaga> vagas;

}
