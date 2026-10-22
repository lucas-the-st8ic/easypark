package com.br.lucasthest8ic.easypark.dtos.carrodto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CarroRequestDTO(

        @NotBlank(message = "A placa não pode ser vazia")
        @Pattern(
                regexp = "^[A-Z]{3}-?([0-9]{4}|[0-9][A-Z][0-9]{2})$",
                message = "A placa informada é inválida. \nInforme uma placa no padrão antigo (ex: ABC-1234) ou \nMercosul (ex: ABC1D23) sem caracteres extras."
        )
        String placa,
        String modelo,
        String cor,
        Boolean elegivelVagaIdoso) {

   public CarroRequestDTO {
        if (elegivelVagaIdoso == null) {
            elegivelVagaIdoso = false;
        }
   }
}
