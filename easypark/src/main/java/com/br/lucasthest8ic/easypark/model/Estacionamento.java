package com.br.lucasthest8ic.easypark.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.List;

@Entity
public class Estacionamento {

    @Id
    private Integer idEstacionamento;


    private String nome;
    private List<Vaga> vagas;

}
