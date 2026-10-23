package com.br.lucasthest8ic.easypark.dtos.carrodtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CarroDTO(

        String placa,
        String modelo,
        String cor,
        Boolean elegivelVagaIdoso) {

}





