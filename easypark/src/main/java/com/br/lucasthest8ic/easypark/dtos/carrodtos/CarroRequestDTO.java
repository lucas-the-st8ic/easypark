package com.br.lucasthest8ic.easypark.dtos.carrodtos;

import com.br.lucasthest8ic.easypark.util.PlacaUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CarroRequestDTO(

        @NotBlank(message = "A placa não pode ser vazia")
        @Pattern(
                regexp = PlacaUtil.REGEX_PLACA,
                message = "A placa informada é inválida. Informe uma placa no padrão antigo (ex: ABC-1234) ou Mercosul (ex: ABC1D23) sem caracteres extras."
        )
        String placa,
        @NotBlank(message = "O modelo é obrigatório.")
        String modelo,
        String cor,
        Boolean elegivelVagaIdoso) {

   public CarroRequestDTO {
        if (elegivelVagaIdoso == null) {
            elegivelVagaIdoso = false;
        }
   }
}
