package com.br.lucasthest8ic.easypark.service;

import com.br.lucasthest8ic.easypark.dtos.carrodto.CarroDTO;
import com.br.lucasthest8ic.easypark.dtos.carrodto.CarroRequestDTO;
import com.br.lucasthest8ic.easypark.exception.exceptions.CantBeNullOrBlankException;
import com.br.lucasthest8ic.easypark.exception.exceptions.ResourceAlreadyExistsException;
import com.br.lucasthest8ic.easypark.exception.exceptions.ResourceNotFoundException;
import com.br.lucasthest8ic.easypark.model.Carro;
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
               .map(this::toDTO
               )
               .orElseThrow(()
                       -> new ResourceNotFoundException
                               ("Não foi possível encontrar o carro com a placa " + placaNormalizada)
               );
    }

    @Transactional
     public CarroDTO register (CarroRequestDTO requestDTO) {
            var placaNormalizada = normalizePlaca(requestDTO.placa());
            if (carroRepository.existsByPlaca(placaNormalizada)) {
                throw new ResourceAlreadyExistsException("Já existe um carro com a placa: " +placaNormalizada+
                        " cadastrada.");
            }

            var novoCarro = new Carro(null,
                    placaNormalizada,
                    requestDTO.modelo(),
                    requestDTO.cor(),
                    requestDTO.elegivelVagaIdoso());

           var carroSalvo = carroRepository.save(novoCarro);
           return  toDTO(carroSalvo);
     }

    private CarroDTO toDTO(Carro carro) {
        return new CarroDTO(carro.getPlaca(),
                carro.getModelo(),
                carro.getCor(), carro.isElegivelVagaIdoso());
    }

    private String normalizePlaca(String placa) {
        if (placa == null || placa.isBlank()) {
            throw new CantBeNullOrBlankException
                    ("A placa do carro não pode ser nula ou vazia.");
        }
        return placa.trim().toUpperCase();
    }
}
