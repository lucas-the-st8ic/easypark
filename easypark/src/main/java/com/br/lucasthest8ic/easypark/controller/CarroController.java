package com.br.lucasthest8ic.easypark.controller;

import com.br.lucasthest8ic.easypark.dtos.carrodtos.CarroDTO;
import com.br.lucasthest8ic.easypark.dtos.carrodtos.CarroRequestDTO;
import com.br.lucasthest8ic.easypark.service.CarroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/easypark")
@RequiredArgsConstructor
public class CarroController {

    private final CarroService carroService;


    @GetMapping("/carros/{placa}")
    public CarroDTO findCarroByPlaca(@PathVariable String placa) {
        return carroService.findCarroByPlaca(placa);
    }

    @PostMapping("/carros")
    public ResponseEntity<CarroDTO> saveCarro(@Valid @RequestBody CarroRequestDTO dadosCarro) {
        var carroASalvar = carroService.register(dadosCarro);

        return ResponseEntity.status(HttpStatus.CREATED).body(carroASalvar);
    }
}
