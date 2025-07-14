package br.com.sisaudcon.saam.saam_sped_cnd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaDTO {
    @NotBlank(message = "Campo 'idEmpresa' é obrigatório.")
    @Size(max = 6, message = "Campo 'idEmpresa' deve ter no máximo 6 caracteres.")
    private String idEmpresa;

    @NotBlank(message = "Campo 'nomeEmpresa' é obrigatório.")
    @Size(max = 255, message = "Campo 'nomeEmpresa' deve ter no máximo 255 caracteres.")
    private String nomeEmpresa;

    @NotBlank(message = "Campo 'cnpj' é obrigatório.")
    @Size(max = 18, message = "Campo 'cnpj' deve ter no máximo 18 caracteres.")
    @Pattern(regexp = "^[0-9]{2}\\.[0-9]{3}\\.[0-9]{3}/[0-9]{4}-[0-9]{2}$", message = "Campo 'cnpj' deve estar no formato XX.XXX.XXX/XXXX-XX.")
    private String cnpj;
    //private String statusEmpresa;
}
