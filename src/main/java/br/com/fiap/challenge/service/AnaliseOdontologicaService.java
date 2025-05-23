package br.com.fiap.challenge.service;

import br.com.fiap.challenge.model.PerguntasOdontoprev;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Serviço responsável pela análise e resposta de perguntas odontológicas
 * utilizando inteligência artificial com Azure OpenAI.
 */
@Service
@Slf4j
public class AnaliseOdontologicaService {
    private final AzureOpenAiChatModel chatModel;
    private final CacheManager cacheManager;
    private final MeterRegistry meterRegistry;

    private static final String TEMPLATE_SISTEMA = """
            Você é um assistente virtual para uma clínica odontológica da Odontoprev chamada ParrotTech.
            Seu objetivo é fornecer respostas precisas, informativas e amigáveis para pacientes.
            
            Informações sobre a clínica:
            - Horário de funcionamento: Segunda a Sexta, das 8h às 18h
            - Número para contato: (11) 5555-1234
            - Endereço: Av. Paulista, 1000, São Paulo - SP
            - Site: www.parrottech.com.br
            - Email: contato@parrottech.com.br
            
            Especialidades oferecidas:
            - Ortodontia: aparelhos convencionais e alinhadores transparentes
            - Implantodontia: implantes dentários de titânio com alta taxa de sucesso
            - Endodontia: tratamento de canal com tecnologia de ponta
            - Odontopediatria: atendimento especializado para crianças
            - Prótese Dentária: soluções fixas e removíveis de alta qualidade
            
            Convênios aceitos:
            - Amil, Bradesco Saúde, SulAmérica, Unimed, Porto Seguro, e planos Odontoprev
            
            Diretrizes para suas respostas:
            1. Seja profissional, claro e acolhedor em suas explicações
            2. Priorize informações baseadas em evidências científicas
            3. Responda apenas sobre temas odontológicos ou relacionados à clínica
            4. Para emergências, sempre recomende contato imediato com a clínica
            5. Caso não tenha certeza sobre algo, sugira consultar diretamente um profissional da clínica
            
            Lembre-se: sua função é informar e orientar, não fornecer diagnósticos definitivos.
            """;

    @Autowired
    public AnaliseOdontologicaService(
            AzureOpenAiChatModel chatModel,
            CacheManager cacheManager,
            MeterRegistry meterRegistry) {
        this.chatModel = chatModel;
        this.cacheManager = cacheManager;
        this.meterRegistry = meterRegistry;
    }

    /**
     * Método de inicialização que verifica a conexão com o Azure OpenAI
     * e registra o resultado no log.
     */
    @PostConstruct
    public void init() {
        try {
            String testResponse = chatModel.call(new Prompt(
                    new SystemMessage("Responda apenas com 'OK'"),
                    new UserMessage("Teste de conexão")
            )).toString();

            log.info("Conexão com Azure OpenAI estabelecida com sucesso");

            // Registra métrica de disponibilidade
            meterRegistry.gauge("ai.service.available", 1);
        } catch (Exception e) {
            log.error("Falha ao conectar com Azure OpenAI: {}", e.getMessage());

            // Registra métrica de disponibilidade como 0 (indisponível)
            meterRegistry.gauge("ai.service.available", 0);
        }
    }

    /**
     * Processa uma pergunta do paciente e retorna uma resposta gerada pela IA
     * contextualizada para o domínio odontológico.
     *
     * @param pergunta A pergunta do paciente a ser respondida
     * @return Objeto PerguntasOdontoprev contendo a pergunta e resposta
     */
    public PerguntasOdontoprev obterResposta(String pergunta) {
        log.debug("Processando pergunta: {}", pergunta);

        // Cria um timer para medir o tempo de resposta
        Timer timer = Timer.builder("ai.response.time")
                .description("Tempo para obter resposta da IA")
                .tag("service", "azure-openai")
                .register(meterRegistry);

        // Verifica no cache primeiro
        Cache cache = cacheManager.getCache("respostas");
        PerguntasOdontoprev cachedResponse = cache != null ?
                cache.get(pergunta, PerguntasOdontoprev.class) : null;

        if (cachedResponse != null) {
            log.debug("Resposta encontrada no cache");

            // Incrementa contador de cache hits
            meterRegistry.counter("ai.cache.hits").increment();

            return cachedResponse;
        }

        // Incrementa contador de cache misses
        meterRegistry.counter("ai.cache.misses").increment();

        // Timer para registrar o tempo de execução
        return timer.record(() -> {
            try {
                SystemMessage systemMessage = new SystemMessage(TEMPLATE_SISTEMA);
                UserMessage userMessage = new UserMessage(pergunta);
                Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

                // Obtém a resposta como string
                String fullResponse = chatModel.call(prompt).toString();
                log.debug("Resposta completa: {}", fullResponse);

                // Extrai o conteúdo usando expressões regulares
                String resposta = extrairConteudoResposta(fullResponse);
                log.debug("Conteúdo extraído: {}", resposta);

                // Cria objeto de resposta
                PerguntasOdontoprev resultado = new PerguntasOdontoprev(pergunta, resposta);
                resultado.setDataProcessamento(LocalDateTime.now());

                // Armazena no cache se disponível
                if (cache != null) {
                    cache.put(pergunta, resultado);
                }

                // Incrementa contador de respostas bem-sucedidas
                meterRegistry.counter("ai.responses.success").increment();

                return resultado;

            } catch (Exception e) {
                log.error("Erro ao processar pergunta: {}", pergunta, e);

                // Incrementa contador de erros
                meterRegistry.counter("ai.responses.error").increment();

                return criarRespostaDeErro(pergunta);
            }
        });
    }

    /**
     * Extrai o conteúdo da resposta a partir da string completa
     * usando expressões regulares adaptativas.
     */
    private String extrairConteudoResposta(String fullResponse) {
        // Tentativa 1: Procura por content='texto'
        java.util.regex.Pattern pattern1 = java.util.regex.Pattern.compile("content='([^']*)'");
        java.util.regex.Matcher matcher1 = pattern1.matcher(fullResponse);
        if (matcher1.find()) {
            return matcher1.group(1);
        }

        // Tentativa 2: Procura por content="texto"
        java.util.regex.Pattern pattern2 = java.util.regex.Pattern.compile("content=\"([^\"]*)\"");
        java.util.regex.Matcher matcher2 = pattern2.matcher(fullResponse);
        if (matcher2.find()) {
            return matcher2.group(1);
        }

        // Tentativa 3: Padrão mais genérico para encontrar o conteúdo
        java.util.regex.Pattern pattern3 = java.util.regex.Pattern.compile("content[=:][\\s'\"]*(.*?)[\\s'\",}]");
        java.util.regex.Matcher matcher3 = pattern3.matcher(fullResponse);
        if (matcher3.find()) {
            return matcher3.group(1);
        }

        // Se todas as tentativas falharem, retorna uma mensagem de erro
        log.warn("Não foi possível extrair o conteúdo da resposta: {}", fullResponse);
        return "Não foi possível processar sua pergunta. Por favor, tente novamente.";
    }

    /**
     * Cria uma resposta amigável para situações de erro
     */
    private PerguntasOdontoprev criarRespostaDeErro(String pergunta) {
        PerguntasOdontoprev resposta = new PerguntasOdontoprev(
                pergunta,
                "Desculpe, não foi possível processar sua pergunta neste momento. " +
                        "Para obter assistência imediata, entre em contato com nossa clínica " +
                        "pelo telefone (11) 5555-1234 ou por email: contato@parrottech.com.br"
        );
        resposta.setDataProcessamento(LocalDateTime.now());
        return resposta;
    }
}
