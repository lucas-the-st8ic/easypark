package com.br.lucasthest8ic.easypark.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
@Table(name="estadias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Estadia {

    @Id
    @SequenceGenerator(
            name = "estadia_seq_generator",      // Nome lógico interno no JPA/Hibernate
            sequenceName = "seq_estadias_id",    // Nome da SEQUENCE criada no PostgreSQL
            initialValue = 1,                  // Começa em 1
            allocationSize = 1                 // Incrementa de 1 em 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "estadia_seq_generator"  // Aponta exatamente para o name acima
    )
    private Long idEstadia;


    @ManyToOne
    @JoinColumn(name = "id_vaga", nullable = false)
    private Vaga vaga;

    @ManyToOne
    @JoinColumn(name = "id_carro", nullable = false)
    private Carro carro;

    @Column(name = "horario_entrada", nullable = false)
    private LocalDateTime horarioEntrada;
    
    @Column(name = "valor", precision = 10, scale = 2)
    private Double valor;

    @Column(name = "horario_saida")
    private LocalDateTime horarioSaida;
}
