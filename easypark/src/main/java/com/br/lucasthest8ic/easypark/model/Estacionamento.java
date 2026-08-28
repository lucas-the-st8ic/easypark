package com.br.lucasthest8ic.easypark.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private Integer idEstacionamento;


    private String nome;
    private List<Vaga> vagas;

}
