package br.com.sisaudcon.saam.saam_sped_cnd.domain.service;

import br.com.sisaudcon.saam.saam_sped_cnd.domain.model.Empresa;
import br.com.sisaudcon.saam.saam_sped_cnd.domain.repository.EmpresaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@AllArgsConstructor
@Service
public class CadastroEmpresaService {

    private final EmpresaRepository empresaRepository;

    @Transactional
    public Empresa buscarOuCadastrarEmpresa(Empresa empresaDTO) {
        Optional<Empresa> empresaExistenteOpt = empresaRepository.findByIdEmpresa(empresaDTO.getIdEmpresa());

        if (empresaExistenteOpt.isPresent()) {
            Empresa empresaExistente = empresaExistenteOpt.get();

            empresaExistente.setCnpj(empresaDTO.getCnpj());
            empresaExistente.setNomeEmpresa(empresaDTO.getNomeEmpresa());
            empresaExistente.setStatusEmpresa(empresaDTO.getStatusEmpresa()); // Opcional

            return empresaRepository.save(empresaExistente);
        } else {
            Empresa novaEmpresa = new Empresa();
            novaEmpresa.setIdEmpresa(empresaDTO.getIdEmpresa());
            novaEmpresa.setCnpj(empresaDTO.getCnpj());
            novaEmpresa.setNomeEmpresa(empresaDTO.getNomeEmpresa());
            novaEmpresa.setStatusEmpresa(empresaDTO.getStatusEmpresa()); // Opcional

            return empresaRepository.save(novaEmpresa);
        }
    }
}