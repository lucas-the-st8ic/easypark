package com.br.lucasthest8ic.easypark.repository;

import com.br.lucasthest8ic.easypark.enums.TipoVaga;
import com.br.lucasthest8ic.easypark.model.Vaga;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VagaRepository extends JpaRepository<Vaga, Integer> {


    List<Vaga> findByEstacionamento_IdEstacionamento(Integer idEstacionamento,
                                                               Pageable pageable);


    List<Vaga> findByEstacionamento_IdEstacionamentoAndTipoVaga(Integer idEstacionamento,
                                                                TipoVaga tipoVaga,
                                                                Pageable pageable);


}
