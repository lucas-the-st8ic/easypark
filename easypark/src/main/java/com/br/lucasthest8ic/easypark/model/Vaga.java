package com.br.lucasthest8ic.easypark.model;

import com.br.lucasthest8ic.easypark.enums.TipoVaga;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class Vaga {

    private Integer idVaga;


    private Estacionamento estacionamento;

    @Enumerated(EnumType.STRING)
    private TipoVaga tipoVaga;

}
