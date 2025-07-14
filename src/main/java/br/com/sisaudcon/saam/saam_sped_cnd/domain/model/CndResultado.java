package br.com.sisaudcon.saam.saam_sped_cnd.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "cnd_resultado")
public class CndResultado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_processamento", nullable = false)
    private OffsetDateTime dataProcessamento;

    @NotBlank
    @Column(name = "arquivo", columnDefinition = "TEXT", nullable = false)
    private String arquivo;

    @NotBlank
    @Column(name = "situacao", length = 100, nullable = false)
    private String situacao;

    @Column(name = "data_emissao")
    private LocalDate dataEmissao;

    @Column(name = "data_validade")
    private LocalDate dataValidade;

    @Column(name = "codigo_controle", length = 100)
    private String codigoControle;

    @Column(name = "status", length = 50)
    private String status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_cliente", nullable = false)
    private Cliente cliente;
}
