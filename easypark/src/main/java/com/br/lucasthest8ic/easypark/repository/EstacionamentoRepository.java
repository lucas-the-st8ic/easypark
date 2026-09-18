package com.br.lucasthest8ic.easypark.repository;

import com.br.lucasthest8ic.easypark.model.Estacionamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstacionamentoRepository extends JpaRepository<Estacionamento, Integer> {

    Optional<Estacionamento> findByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCase(String nome);
}
