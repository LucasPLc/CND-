package br.com.sisaudcon.saam.saam_sped_cnd.domain.service;

import br.com.sisaudcon.saam.saam_sped_cnd.domain.exception.ClienteNotFoundException;
import br.com.sisaudcon.saam.saam_sped_cnd.domain.exception.ClienteVinculadoResultadoException;
import br.com.sisaudcon.saam.saam_sped_cnd.domain.model.Cliente;
import br.com.sisaudcon.saam.saam_sped_cnd.domain.model.Empresa;
import br.com.sisaudcon.saam.saam_sped_cnd.domain.repository.ClienteRepository;
import br.com.sisaudcon.saam.saam_sped_cnd.domain.repository.CndResultadoRepository;
import br.com.sisaudcon.saam.saam_sped_cnd.domain.repository.EmpresaRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@AllArgsConstructor
@Service
public class RegistroClienteService {

    private static final Logger logger = LoggerFactory.getLogger(RegistroClienteService.class);

    private final ClienteRepository clienteRepository;
    private final EmpresaRepository empresaRepository;
    private final CndResultadoRepository cndResultadoRepository;
    private final SituacaoValidationService situacaoValidationService;

    @Transactional
    public Cliente salvarClienteComEmpresa(Cliente cliente) {
        logger.info("Iniciando processo de salvar cliente com empresa. Cliente: {}", cliente.getCnpj());
        int situacao = situacaoValidationService.validarAutorizacaoEmpresa(cliente.getEmpresa().getIdEmpresa());
        String statusEmpresa = String.valueOf(situacao);
        logger.debug("Situação da empresa validada: {}", statusEmpresa);

        Empresa empresaSalva = salvarOuAtualizarEmpresa(cliente.getEmpresa(), statusEmpresa);
        cliente.setEmpresa(empresaSalva);
        logger.debug("Empresa salva ou atualizada: {}", empresaSalva.getIdEmpresa());

        verificarDuplicidadeCliente(cliente, empresaSalva.getIdEmpresa());

        Cliente clienteSalvo = clienteRepository.save(cliente);
        logger.info("Cliente salvo com sucesso. ID: {}", clienteSalvo.getId());
        return clienteSalvo;
    }

    private Empresa salvarOuAtualizarEmpresa(Empresa empresa, String statusEmpresa) {
        return empresaRepository.findByIdEmpresa(empresa.getIdEmpresa())
                .map(e -> atualizarEmpresa(e, empresa, statusEmpresa))
                .orElseGet(() -> criarNovaEmpresa(empresa, statusEmpresa));
    }

    private Empresa atualizarEmpresa(Empresa existente, Empresa nova, String statusEmpresa) {
        existente.setNomeEmpresa(nova.getNomeEmpresa());
        existente.setCnpj(nova.getCnpj());
        existente.setStatusEmpresa(statusEmpresa);
        return empresaRepository.save(existente);
    }

    private Empresa criarNovaEmpresa(Empresa dados, String statusEmpresa) {
        Empresa nova = new Empresa();
        nova.setIdEmpresa(dados.getIdEmpresa());
        nova.setNomeEmpresa(dados.getNomeEmpresa());
        nova.setCnpj(dados.getCnpj());
        nova.setStatusEmpresa(statusEmpresa);
        return empresaRepository.save(nova);
    }

    private void verificarDuplicidadeCliente(Cliente cliente, String idEmpresa) {
        Optional<Cliente> duplicado = (cliente.getId() == null)
                ? clienteRepository.findByCnpjAndEmpresa_IdEmpresa(cliente.getCnpj(), idEmpresa)
                : clienteRepository.findByCnpjAndEmpresa_IdEmpresaAndIdNot(cliente.getCnpj(), idEmpresa, cliente.getId());

        duplicado.ifPresent(c -> {
            throw new DataIntegrityViolationException("Já existe esse CNPJ para essa empresa.");
        });
    }

    // busca cliente para pegar idEmpresa
    public void excluir(Integer clienteId) {
        logger.info("Iniciando processo de exclusão do cliente. ID: {}", clienteId);

        Cliente c = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado"));
        logger.debug("Cliente encontrado para exclusão: {}", c.getCnpj());
        situacaoValidationService.validarAutorizacaoEmpresa(c.getEmpresa().getIdEmpresa());

        boolean existeVinculo = cndResultadoRepository.existsByCliente_Id(clienteId);
        logger.debug("Verificação de vínculo com resultado: {}", existeVinculo);

        if (existeVinculo) {
            logger.warn("Tentativa de exclusão de cliente com vínculo. Cliente ID: {}", clienteId);
            throw new ClienteVinculadoResultadoException("Não é possível excluir o cliente. Existem resultados vinculados.");
        }

        clienteRepository.deleteById(clienteId);
        logger.info("Cliente excluído com sucesso. ID: {}", clienteId);
    }
}