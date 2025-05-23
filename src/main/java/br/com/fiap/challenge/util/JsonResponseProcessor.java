package br.com.fiap.challenge.util;

import br.com.fiap.challenge.model.RecomendacaoTratamento;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class JsonResponseProcessor {

    private final ObjectMapper objectMapper;
    private final MeterRegistry meterRegistry;

    @Autowired
    public JsonResponseProcessor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }

    public List<RecomendacaoTratamento> processarRespostaJson(String response) {
        try {
            String json = limparResposta(response);

            RecomendacaoTratamento[] recomendacoes = objectMapper
                    .readValue(json, RecomendacaoTratamento[].class);

            LocalDateTime agora = LocalDateTime.now();
            Arrays.stream(recomendacoes)
                    .forEach(rec -> rec.setDataGeracao(agora));

            return Arrays.asList(recomendacoes);
        } catch (Exception e) {
            log.error("Erro ao processar resposta JSON: {}", e.getMessage(), e);
            meterRegistry.counter("recommendation.errors.json_parse").increment();
            return Collections.emptyList();
        }
    }

    private String limparResposta(String response) {
        if (response == null) {
            return "";
        }
        String r = response.trim();

        if (r.startsWith("```")) {
            int idx = r.indexOf('\n');
            if (idx >= 0) {
                r = r.substring(idx + 1);
            } else {
                r = r.substring(3);
            }
        }
        if (r.endsWith("```")) {
            r = r.substring(0, r.length() - 3);
        }

        return r.trim();
    }
}
