package com.br.lucasthest8ic.easypark.dtos.carrodto;

import jakarta.persistence.Column;

public record CarroDTO(String placa,
                       String modelo,
                       boolean elegivelVagaIdoso) {
}





