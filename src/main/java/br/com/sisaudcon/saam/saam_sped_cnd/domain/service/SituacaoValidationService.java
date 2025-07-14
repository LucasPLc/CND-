package br.com.sisaudcon.saam.saam_sped_cnd.domain.service;

import br.com.sisaudcon.saam.saam_sped_cnd.domain.exception.*;
import br.com.sisaudcon.saam.saam_sped_cnd.domain.model.Cliente;
import br.com.sisaudcon.saam.saam_sped_cnd.domain.model.Empresa;
import br.com.sisaudcon.saam.saam_sped_cnd.domain.repository.ClienteRepository;
import br.com.sisaudcon.saam.saam_sped_cnd.domain.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Component
public class SituacaoValidationService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final EmpresaRepository empresaRepository;

    @Value("${saam.validation.url}")
    private String validationUrl;

    public SituacaoValidationService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    public int validarAutorizacaoEmpresa(String idEmpresa) {
        // 0) idEmpresa ausente ou mal formatado
        if (idEmpresa == null || idEmpresa.isBlank()) {
            throw new ClienteIdInvalidoException("IDCLIENTE inválido ou não informado.");
        }

        int situacao;
        try {
            // 1) chamada externa
            ResponseEntity<Map> resp = restTemplate.getForEntity(
                    validationUrl, Map.class, idEmpresa
            );

            // 2) se 404 ou 400, trata como ID inválido
            HttpStatus status = HttpStatus.valueOf(resp.getStatusCode().value());
            if (status == HttpStatus.BAD_REQUEST || status == HttpStatus.NOT_FOUND) {
                throw new ClienteIdInvalidoException("IDCLIENTE inválido ou não informado.");
            }

            if (!status.is2xxSuccessful() || resp.getBody() == null) {
                throw new RestClientException("Resposta inválida");
            }

            Object sit = resp.getBody().get("situacao");
            if (sit == null) {
                throw new RestClientException("Campo 'situacao' ausente");
            }
            situacao = Integer.parseInt(sit.toString());

        } catch (HttpClientErrorException.NotFound | HttpClientErrorException.BadRequest ex) {
            // Erro 404 / 400 da API externa
            throw new ClienteIdInvalidoException("IDCLIENTE inválido ou não informado.");
        } catch (RestClientException ex) {
            // Timeout, 5xx ou falha de comunicação → fallback
            Optional<Empresa> empLocalOpt = empresaRepository.findByIdEmpresa(idEmpresa);
            Empresa empLocal = empLocalOpt.orElseThrow(() ->
                    new ClienteIdInvalidoException("IDCLIENTE inválido ou não informado.")
            );

            String statusLocal = empLocal.getStatusEmpresa();
            if (statusLocal == null) {
                throw new ServicoValidacaoIndisponivelException(
                        "Serviço de validação indisponível. Tente novamente mais tarde."
                );
            }
            situacao = Integer.parseInt(statusLocal);
        }

        final int situacaoFinal = situacao;

        empresaRepository.findByIdEmpresa(idEmpresa)
                .ifPresent(emp -> {
                    emp.setStatusEmpresa(String.valueOf(situacaoFinal));
                    empresaRepository.save(emp);
                });

        // 5) se não for “1”, nega o acesso
        if (situacao != 1) {
            throw new ClienteNaoAutorizadoException("Acesso negado. Cliente sem autorização ativa.");
        }

        return situacao;
    }

    public void validarPorCliente(Integer clienteId, ClienteRepository clienteRepo) {
        Cliente cliente = clienteRepo.findById(clienteId)
                .orElseThrow(() -> new ClienteNotFoundException(
                        "Cliente não encontrado para o ID informado."
                ));
        validarAutorizacaoEmpresa(cliente.getEmpresa().getIdEmpresa());
    }
}