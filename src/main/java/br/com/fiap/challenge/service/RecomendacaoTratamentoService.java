package br.com.fiap.challenge.service;

import br.com.fiap.challenge.client.AzureOpenAiClient;
import br.com.fiap.challenge.model.AtendimentoUsuarioOdontoprev;
import br.com.fiap.challenge.model.RecomendacaoTratamento;
import br.com.fiap.challenge.model.UsuarioOdontoprev;
import br.com.fiap.challenge.repository.AtendimentoUsuarioRepository;
import br.com.fiap.challenge.repository.UsuarioRepository;
import br.com.fiap.challenge.util.AtendimentoFormatter;
import br.com.fiap.challenge.util.JsonResponseProcessor;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviço que gera recomendações de tratamentos usando Azure OpenAI
 */
@Service
@Slf4j
public class RecomendacaoTratamentoService {

    private final AzureOpenAiClient aiClient;
    private final UsuarioRepository usuarioRepository;
    private final AtendimentoUsuarioRepository atendimentoRepository;
    private final CacheManager cacheManager;
    private final MeterRegistry meterRegistry;
    private final AtendimentoFormatter formatter;
    private final JsonResponseProcessor jsonProcessor;

    private static final String SYSTEM_PROMPT_TEMPLATE = """
            Você é um especialista em odontologia preventiva da clínica ParrotTech, com ampla experiência clínica.
            
            Sua tarefa é analisar o histórico odontológico do paciente e recomendar tratamentos preventivos
            ou procedimentos que seriam benéficos com base em seus antecedentes clínicos.
            
            Para cada recomendação, forneça:
            1. O tratamento específico recomendado
            2. Uma justificativa clínica clara baseada no histórico do paciente
            3. Um nível de prioridade (alta, média ou baixa)
            
            DIRETRIZES IMPORTANTES:
            - Priorize tratamentos preventivos quando apropriado
            - Considere o histórico de procedimentos anteriores para suas recomendações
            - Base suas sugestões em evidências científicas e melhores práticas odontológicas
            - Formate sua resposta exclusivamente como um array JSON válido
            
            Exemplo de formato esperado:
            [
              {
                "tratamento": "Aplicação de selante nos molares",
                "justificativa": "Prevenção de cáries em pacientes com histórico de restaurações",
                "prioridade": "média"
              },
              {
                "tratamento": "Terapia com flúor",
                "justificativa": "Fortalecimento do esmalte para prevenir novas cáries",
                "prioridade": "alta"
              }
            ]
            
            Responda APENAS no formato JSON especificado, sem texto adicional antes ou depois.
            """;

    @Autowired
    public RecomendacaoTratamentoService(
            AzureOpenAiClient aiClient,
            UsuarioRepository usuarioRepository,
            AtendimentoUsuarioRepository atendimentoRepository,
            CacheManager cacheManager,
            MeterRegistry meterRegistry,
            AtendimentoFormatter formatter,
            JsonResponseProcessor jsonProcessor) {
        this.aiClient = aiClient;
        this.usuarioRepository = usuarioRepository;
        this.atendimentoRepository = atendimentoRepository;
        this.cacheManager = cacheManager;
        this.meterRegistry = meterRegistry;
        this.formatter = formatter;
        this.jsonProcessor = jsonProcessor;
    }

    /**
     * Gera recomendações de tratamento odontológico
     */
    public List<RecomendacaoTratamento> gerarRecomendacoes(Long usuarioId) {
        log.debug("Gerando recomendações para usuário ID: {}", usuarioId);

        Timer timer = Timer.builder("recommendation.generation.time")
                .description("Tempo para gerar recomendações de tratamento")
                .tag("service", "azure-openai")
                .register(meterRegistry);

        // Verificar cache
        String cacheKey = "recomendacao_" + usuarioId;
        Cache cache = cacheManager.getCache("recomendacoes");
        List<RecomendacaoTratamento> cachedResponse = cache != null ?
                cache.get(cacheKey, List.class) : null;

        if (cachedResponse != null) {
            log.debug("Recomendações encontradas no cache para usuário ID: {}", usuarioId);
            meterRegistry.counter("recommendation.cache.hits").increment();
            return cachedResponse;
        }

        meterRegistry.counter("recommendation.cache.misses").increment();

        return timer.record(() -> {
            try {
                UsuarioOdontoprev usuario = usuarioRepository.findById(usuarioId)
                        .orElseThrow(() -> {
                            log.error("Usuário não encontrado: {}", usuarioId);
                            meterRegistry.counter("recommendation.errors.user_not_found").increment();
                            return new RuntimeException("Usuário não encontrado: " + usuarioId);
                        });

                List<AtendimentoUsuarioOdontoprev> atendimentos =
                        atendimentoRepository.findByUsuario(usuario);

                if (atendimentos.isEmpty()) {
                    log.info("Usuário {} não possui histórico de atendimentos", usuarioId);
                    meterRegistry.counter("recommendation.no_history").increment();
                }

                // Formatar dados e enviar para a IA
                String historicoTexto = formatter.formatarHistorico(atendimentos);
                String userPrompt = formatter.criarPromptUsuario(usuario, historicoTexto);

                log.debug("Enviando prompt para Azure OpenAI");
                String responseText = aiClient.enviarPrompt(SYSTEM_PROMPT_TEMPLATE, userPrompt);
                log.debug("Resposta obtida do Azure OpenAI: {}", responseText);

                List<RecomendacaoTratamento> recomendacoes = jsonProcessor.processarRespostaJson(responseText);

                // Armazenar no cache
                if (cache != null && !recomendacoes.isEmpty()) {
                    cache.put(cacheKey, recomendacoes);
                }

                meterRegistry.counter("recommendation.success").increment();
                return recomendacoes;

            } catch (Exception e) {
                log.error("Erro ao gerar recomendações para usuário {}: {}", usuarioId, e.getMessage(), e);
                meterRegistry.counter("recommendation.errors").increment();
                throw new RuntimeException("Erro ao gerar recomendações: " + e.getMessage(), e);
            }
        });
    }
}
