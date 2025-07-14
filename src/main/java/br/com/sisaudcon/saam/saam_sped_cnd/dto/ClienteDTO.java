package br.com.sisaudcon.saam.saam_sped_cnd.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ClienteDTO {

    @NotBlank(message = "Campo 'cnpj' é obrigatório.")
    @Size(max = 18, message = "Campo 'cnpj' deve ter no máximo 18 caracteres.")
    @Pattern(regexp = "^[0-9]{2}\\.[0-9]{3}\\.[0-9]{3}/[0-9]{4}-[0-9]{2}$", message = "Campo 'cnpj' deve estar no formato XX.XXX.XXX/XXXX-XX.")
    private String cnpj;

    @NotNull(message = "Campo 'periodicidade' é obrigatório.")
    @Positive(message = "Campo 'periodicidade' deve ser um número positivo.")
    @Digits(integer = 3, fraction = 0, message = "Campo 'periodicidade' deve ser um número inteiro.")
    private Integer periodicidade;

    @NotBlank(message = "Campo 'statusCliente' é obrigatório.")
    @Size(max = 50, message = "Campo 'statusCliente' deve ter no máximo 50 caracteres.")
    private String statusCliente;

    @NotNull(message = "Campo 'nacional' é obrigatório.")
    private Boolean nacional;

    @NotNull(message = "Campo 'municipal' é obrigatório.")
    private Boolean municipal;

    @NotNull(message = "Campo 'estadual' é obrigatório.")
    private Boolean estadual;

    @NotNull(message = "Campo 'empresa.idEmpresa' (fk_empresa) é obrigatório.")
    @Valid
    private EmpresaDTO empresa;
}