package com.br.lucasthest8ic.easypark.repository;

import com.br.lucasthest8ic.easypark.model.Estadia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadiaRepository extends JpaRepository<Estadia, Long> {

    boolean existsByCarro_PlacaAndHorarioSaidaIsNull(String carroPlaca);


    Optional<Estadia> findByCarro_PlacaAndHorarioSaidaIsNull(String carroPlaca);
}
