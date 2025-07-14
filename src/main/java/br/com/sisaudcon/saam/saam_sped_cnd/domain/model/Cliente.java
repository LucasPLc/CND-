package br.com.sisaudcon.saam.saam_sped_cnd.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cnd_cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(length = 18,name = "cnpj", unique = true, nullable = false)
    private String cnpj;

    @NotNull
    private Integer periodicidade;

    @NotNull
    @Size(max = 50)
    private String statusCliente;

    private Boolean nacional;

    private Boolean municipal;

    private Boolean estadual;

    @ManyToOne
    @JoinColumn(name = "fk_empresa", referencedColumnName = "id")
    private Empresa empresa;
}
