package com.br.lucasthest8ic.easypark.service;

import com.br.lucasthest8ic.easypark.dtos.carrodto.CarroDTO;
import com.br.lucasthest8ic.easypark.exception.CantBeNullOrBlankException;
import com.br.lucasthest8ic.easypark.exception.ResourceNotFoundException;
import com.br.lucasthest8ic.easypark.repository.CarroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CarroService {

    private final CarroRepository carroRepository;

    @Transactional(readOnly = true)
    public CarroDTO findCarroByPlaca(String placa) {

        var placaNormalizada = normalizePlaca(placa);

        return carroRepository.findByPlaca(placaNormalizada)
               .map(c -> new CarroDTO(c.getPlaca(),
                       c.getModelo(),c.isElegivelVagaIdoso())
               )
               .orElseThrow(()
                       -> new ResourceNotFoundException
                               ("Não foi possível encontrar o carro com a placa " + placaNormalizada)
               );
    }

     private String normalizePlaca(String placa) {
        if (placa == null || placa.isBlank()) {
            throw new CantBeNullOrBlankException
                    ("A placa do carro não pode ser nula ou vazia.");
        }
        return placa.trim().toUpperCase();
     }
}
