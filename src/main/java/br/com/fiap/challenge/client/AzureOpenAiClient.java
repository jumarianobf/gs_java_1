package br.com.fiap.challenge.client;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;

@Component
@Slf4j
public class AzureOpenAiClient {

    private final AzureOpenAiChatModel chatModel;
    private final MeterRegistry meterRegistry;

    @Autowired
    public AzureOpenAiClient(AzureOpenAiChatModel chatModel, MeterRegistry meterRegistry) {
        this.chatModel = chatModel;
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void init() {
        try {
            String testResponse = chatModel.call(new Prompt(
                    new SystemMessage("Responda apenas com 'OK'"),
                    new UserMessage("Teste de conexão")
            )).toString();

            log.info("Conexão com Azure OpenAI estabelecida com sucesso");
            meterRegistry.gauge("ai.service.available", 1);
        } catch (Exception e) {
            log.error("Falha ao conectar com Azure OpenAI: {}", e.getMessage(), e);
            meterRegistry.gauge("ai.service.available", 0);
        }
    }

    public String enviarPrompt(String sistema, String usuarioPrompt) {
        return enviarPrompt(new SystemMessage(sistema), new UserMessage(usuarioPrompt));
    }

    public String enviarPrompt(SystemMessage sistema, UserMessage usuarioPrompt) {
        Prompt prompt = new Prompt(List.of(sistema, usuarioPrompt));
        var response = chatModel.call(prompt);
        return extrairConteudoResposta(response.toString());
    }

    private String extrairConteudoResposta(String fullResponse) {
        java.util.regex.Pattern pattern1 = java.util.regex.Pattern.compile("content='([^']*)'");
        java.util.regex.Matcher matcher1 = pattern1.matcher(fullResponse);
        if (matcher1.find()) {
            return matcher1.group(1);
        }

        java.util.regex.Pattern pattern2 = java.util.regex.Pattern.compile("content=\"([^\"]*)\"");
        java.util.regex.Matcher matcher2 = pattern2.matcher(fullResponse);
        if (matcher2.find()) {
            return matcher2.group(1);
        }

        java.util.regex.Pattern pattern3 = java.util.regex.Pattern.compile("content[=:][\\s'\"]*(.*?)[\\s'\",}]");
        java.util.regex.Matcher matcher3 = pattern3.matcher(fullResponse);
        if (matcher3.find()) {
            return matcher3.group(1);
        }

        log.warn("Não foi possível extrair o conteúdo da resposta: {}", fullResponse);
        return "[]";
    }
}
