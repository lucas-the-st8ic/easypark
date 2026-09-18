package com.br.lucasthest8ic.easypark.repository;

import com.br.lucasthest8ic.easypark.model.Carro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CarroRepository extends JpaRepository<Carro, Integer> {


    Optional<Carro> findByPlaca(String placa);

    boolean existsByPlaca(String placa);


}
