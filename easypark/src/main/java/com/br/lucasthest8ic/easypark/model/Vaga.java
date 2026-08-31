package com.br.lucasthest8ic.easypark.model;

import com.br.lucasthest8ic.easypark.enums.TipoVaga;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="vagas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vaga {

    @Id
    @SequenceGenerator(
            name = "vaga_seq_generator",      // Nome lógico interno no JPA/Hibernate
            sequenceName = "seq_vagas_id",    // Nome da SEQUENCE criada no PostgreSQL
            initialValue = 1,                  // Começa em 1
            allocationSize = 1                 // Incrementa de 1 em 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "vaga_seq_generator"  // Aponta exatamente para o name acima
    )
    private Integer idVaga;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estacionamento_id")
    private Estacionamento estacionamento;

    @Enumerated(EnumType.STRING)
    private TipoVaga tipoVaga;

}
