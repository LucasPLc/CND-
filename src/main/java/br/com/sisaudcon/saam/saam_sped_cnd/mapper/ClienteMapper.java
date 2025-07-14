package br.com.sisaudcon.saam.saam_sped_cnd.mapper;

import br.com.sisaudcon.saam.saam_sped_cnd.domain.model.Cliente;
import br.com.sisaudcon.saam.saam_sped_cnd.domain.model.Empresa;
import br.com.sisaudcon.saam.saam_sped_cnd.dto.ClienteDTO;
import br.com.sisaudcon.saam.saam_sped_cnd.dto.EmpresaDTO;

public class ClienteMapper {

    // Converte DTO → Entidade
    public static Cliente toEntity(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setCnpj(dto.getCnpj());
        cliente.setPeriodicidade(dto.getPeriodicidade());
        cliente.setStatusCliente(dto.getStatusCliente());
        cliente.setNacional(dto.getNacional());
        cliente.setMunicipal(dto.getMunicipal());
        cliente.setEstadual(dto.getEstadual());

        // Mapeia a empresa
        if (dto.getEmpresa() != null) {
            Empresa empresa = new Empresa();
            empresa.setIdEmpresa(dto.getEmpresa().getIdEmpresa());
            empresa.setNomeEmpresa(dto.getEmpresa().getNomeEmpresa());
            empresa.setCnpj(dto.getEmpresa().getCnpj());
            cliente.setEmpresa(empresa);
        }

        return cliente;
    }

    // Converte Entidade → DTO
    public static ClienteDTO toDTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setCnpj(cliente.getCnpj());
        dto.setPeriodicidade(cliente.getPeriodicidade());
        dto.setStatusCliente(cliente.getStatusCliente());
        dto.setNacional(cliente.getNacional());
        dto.setMunicipal(cliente.getMunicipal());
        dto.setEstadual(cliente.getEstadual());

        // Mapeia a empresa
        if (cliente.getEmpresa() != null) {
            EmpresaDTO empresaDTO = new EmpresaDTO();
            empresaDTO.setIdEmpresa(cliente.getEmpresa().getIdEmpresa());
            empresaDTO.setNomeEmpresa(cliente.getEmpresa().getNomeEmpresa());
            empresaDTO.setCnpj(cliente.getEmpresa().getCnpj());

            dto.setEmpresa(empresaDTO);
        }

        return dto;
    }
}
