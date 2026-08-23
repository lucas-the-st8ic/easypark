package com.br.lucasthest8ic.easypark.model;

import jakarta.persistence.*;

@Entity
@Table(name="carros")
public class Carro {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer idCarro;


    private String modelo;
    @Column(unique = true)
    private String placa;
    private String cor;
    private boolean elegivelVagaIdoso;

}
