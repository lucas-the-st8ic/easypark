package com.br.lucasthest8ic.easypark.repository;

import com.br.lucasthest8ic.easypark.model.Carro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CarroRepository extends JpaRepository<Carro, Integer> {


    Optional<Carro> findByPlaca(String placa);

    boolean existsByPlaca(String placa);
    /*Próximo passo: os outros 3 repositories

Agora que você entendeu o padrão
(interface, extends JpaRepository<Entidade, TipoDoId>,
Query Methods por convenção de nome),
os próximos devem ser bem mais rápidos.
Pensa em cada entidade e quais consultas extras
(além do CRUD básico que já vem de graça) fazem sentido:*/
}
