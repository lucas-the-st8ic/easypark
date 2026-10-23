package com.br.lucasthest8ic.easypark.model;

import com.br.lucasthest8ic.easypark.util.PlacaUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="carros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Carro {

    @Id
    @SequenceGenerator(
            name = "carro_seq_generator",      // Nome lógico interno no JPA/Hibernate
            sequenceName = "seq_carros_id",    // Nome da SEQUENCE criada no PostgreSQL
            initialValue = 1,                  // Começa em 1
            allocationSize = 1                 // Incrementa de 1 em 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "carro_seq_generator"  // Aponta exatamente para o name acima
    )
    private Integer idCarro;


    @NotBlank(message = "A placa é obrigatória")
    @Pattern(
            regexp = PlacaUtil.REGEX_PLACA,
            message = "A placa informada é inválida. Informe uma placa no padrão antigo (ex: ABC-1234) ou Mercosul (ex: ABC1D23) sem caracteres extras."
    )
    @Column(name = "placa", nullable = false, unique = true, length = 10)
    private String placa;

    @NotBlank(message = "O modelo é obrigatório.")
    @Column(name = "modelo", nullable = false, length = 50)
    private String modelo;

    @Column(name = "cor", length = 30)
    private String cor;

    @Column(name = "elegivel_vaga_idoso",
            nullable = false)
    private Boolean elegivelVagaIdoso = false;


    @PrePersist
    @PreUpdate
    public void padronizarPlaca() {
        this.placa = PlacaUtil.normalizePlaca(this.placa);
    }
}
