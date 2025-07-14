package br.com.sisaudcon.saam.saam_sped_cnd.domain.service;

import br.com.sisaudcon.saam.saam_sped_cnd.domain.model.CndResultado;
import br.com.sisaudcon.saam.saam_sped_cnd.domain.repository.CndResultadoRepository;
import br.com.sisaudcon.saam.saam_sped_cnd.util.CndParserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CndResultadoSchedulerService {

    private final CndResultadoRepository repository;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Value("${cnd.resultado.scheduled.cron}")
    private String cron;

    public CndResultadoSchedulerService(CndResultadoRepository repository) {
        this.repository = repository;
    }

    @Scheduled(cron = "${cnd.resultado.scheduled.cron}")
    @Transactional
    public void processarNovosResultados() {
        log.info("Iniciando extração de dados de CND para resultados pendentes...");

        final String STATUS_CONCLUIDO = "concluido";
        List<CndResultado> pendentes = repository.findByStatusAndSituacaoIsNull(STATUS_CONCLUIDO);
        log.info("Encontrados {} registros pendentes", pendentes.size());

        List<CndResultado> atualizados = new ArrayList<>();

        for (CndResultado resultado : pendentes) {
            String base64 = resultado.getArquivo();
            if (base64 == null || base64.isBlank()) {
                log.warn("CND #{} ignorada: campo 'arquivo' vazio ou nulo.", resultado.getId());
                continue;
            }
            try {
                Map<String, String> dados = CndParserUtil.extrairDadosCndBase64(base64);

                String situacao = dados.get("situacao");
                if (situacao != null && !situacao.isEmpty()) {
                    resultado.setSituacao(situacao);
                } else {
                    log.warn("Situacao ausente para CND #{}", resultado.getId());
                }

                String emissaoRaw = dados.get("dataEmissao");
                if (emissaoRaw != null) {
                    String dataPart = extractDateFromString(emissaoRaw);
                    if (dataPart != null) {
                        LocalDate dataEmissao = LocalDate.parse(dataPart, dateFormatter);
                        resultado.setDataEmissao(dataEmissao);
                    } else {
                        log.warn("Data de emissão não encontrada no texto '{}' para CND #{}", emissaoRaw, resultado.getId());
                    }
                }

                String validadeRaw = dados.get("dataValidade");
                if (validadeRaw != null) {
                    LocalDate dataValidade = LocalDate.parse(validadeRaw, dateFormatter);
                    resultado.setDataValidade(dataValidade);
                }

                resultado.setCodigoControle(dados.get("codigoControle"));

                atualizados.add(resultado);

            } catch (Exception ex) {
                log.error("Falha ao processar CND #{}: ", resultado.getId(), ex);
            }
        }

        if (!atualizados.isEmpty()) {
            repository.saveAll(atualizados);
            log.info("{} registros CND atualizados com sucesso.", atualizados.size());
        } else {
            log.info("Nenhum registro CND atualizado.");
        }

        log.info("Finalizada extração de dados de CND.");
    }
    private String extractDateFromString(String raw) {
        if (raw == null || raw.isEmpty()) return null;
        String[] parts = raw.trim().split(" ");
        String possibleDate = parts[parts.length - 1];
        return possibleDate;
    }

}
