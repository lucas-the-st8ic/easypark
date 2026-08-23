package com.br.lucasthest8ic.easypark.model;

import java.time.LocalDateTime;

public class Estadia {

    private Long idEstadia;


    private Vaga vaga;
    private Carro carro;
    private Double valor;
    private LocalDateTime horarioEntrada;
    private LocalDateTime horarioSaida;
}
