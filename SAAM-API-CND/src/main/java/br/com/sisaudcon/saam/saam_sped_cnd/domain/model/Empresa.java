package br.com.sisaudcon.saam.saam_sped_cnd.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Table(name = "cnd_empresa")
@Entity
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @NotBlank
    @Column(length = 18)
    private String cnpj;

    @Size(max = 255)
    @Column(name = "nome_empresa")
    private String nomeEmpresa;

    @Column(name = "id_empresa", length = 6)
    private String idEmpresa;

    @Size(max = 50)
    @Column(name = "status_empresa")
    private String statusEmpresa;

}