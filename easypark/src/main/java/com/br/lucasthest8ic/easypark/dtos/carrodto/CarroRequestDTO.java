package com.br.lucasthest8ic.easypark.dtos.carrodto;

public record CarroRequestDTO(String placa,
                              String modelo,
                              String cor,
                              boolean elegivelVagaIdoso) {
}
