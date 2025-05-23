package br.com.fiap.challenge.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Controlador MVC para interação via interface de chat com o assistente virtual
 */
@Controller
@RequestMapping("/assistente-odontologico")
@Slf4j
public class AssistenteIaController {

    private final AzureOpenAiChatModel chatModel;
    private final ObjectMapper objectMapper;

    private static final String SYSTEM_PROMPT =
            "Você é um assistente odontológico especializado da clínica ParrotTech. " +
                    "Ajude pacientes com dúvidas sobre saúde bucal, procedimentos dentários e cuidados preventivos. " +
                    "Suas respostas devem ser informativas, precisas e amigáveis, mas lembre o paciente que " +
                    "suas informações não substituem uma consulta profissional.";

    @Autowired
    public AssistenteIaController(AzureOpenAiChatModel chatModel, ObjectMapper objectMapper) {
        this.chatModel = chatModel;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public String paginaAssistente(Model model) {
        ArrayList<MensagemChat> historico = new ArrayList<>();
        model.addAttribute("historico", historico);

        // Serializar o histórico para JSON para manter estado
        try {
            model.addAttribute("historicoJson", objectMapper.writeValueAsString(historico));
        } catch (Exception e) {
            log.error("Erro ao serializar histórico vazio: {}", e.getMessage());
            model.addAttribute("historicoJson", "[]");
        }

        return "assistente/chat";
    }

    @PostMapping
    public String processarPergunta(
            @RequestParam(required = false) String pergunta,
            @RequestParam(required = false) String historicoJson,
            Model model) {

        // Deserializar histórico do JSON
        ArrayList<MensagemChat> historico = new ArrayList<>();
        try {
            if (historicoJson != null && !historicoJson.isEmpty()) {
                historico = objectMapper.readValue(historicoJson,
                        new TypeReference<ArrayList<MensagemChat>>() {});
            }
        } catch (Exception e) {
            log.error("Erro ao deserializar histórico: {}", e.getMessage());
        }

        // Validar pergunta
        if (pergunta == null || pergunta.trim().isEmpty()) {
            model.addAttribute("erro", "Por favor, digite uma pergunta.");
            model.addAttribute("historico", historico);

            try {
                model.addAttribute("historicoJson", objectMapper.writeValueAsString(historico));
            } catch (Exception e) {
                model.addAttribute("historicoJson", "[]");
            }

            return "assistente/chat";
        }

        try {
            // Adicionar pergunta ao histórico
            historico.add(new MensagemChat("usuario", pergunta, LocalDateTime.now()));

            // Prepara o prompt com todo o histórico para manter o contexto da conversa
            List<Message> messages = new ArrayList<>();
            messages.add(new SystemMessage(SYSTEM_PROMPT));

            // Adiciona o histórico para manter contexto da conversa
            for (MensagemChat msg : historico) {
                if ("usuario".equals(msg.getTipo())) {
                    messages.add(new UserMessage(msg.getConteudo()));
                } else {
                    messages.add(new AssistantMessage(msg.getConteudo()));
                }
            }

            // Cria o prompt com a lista de mensagens
            Prompt prompt = new Prompt(messages);

            // Chama a API do Azure OpenAI
            ChatResponse chatResponse = chatModel.call(prompt);

            String conteudoResposta = chatResponse
                    .getResults()                    // lista de Generation :contentReference[oaicite:0]{index=0}
                    .get(0)                          // primeira opção
                    .getOutput()                     // AssistantMessage
                    .getText();

            // Adiciona a resposta ao histórico
            historico.add(new MensagemChat("assistente", conteudoResposta, LocalDateTime.now()));

        } catch (Exception e) {
            log.error("Erro ao processar pergunta: {}", e.getMessage(), e);
            // Adiciona mensagem de erro ao histórico
            historico.add(new MensagemChat("assistente",
                    "Desculpe, estou tendo dificuldades técnicas. Por favor, tente novamente mais tarde ou entre em contato com a clínica pelo telefone (11) 5555-1234.",
                    LocalDateTime.now()));
        }

        // Atualizar o modelo
        model.addAttribute("historico", historico);

        // Re-serializar o histórico atualizado
        try {
            model.addAttribute("historicoJson", objectMapper.writeValueAsString(historico));
        } catch (Exception e) {
            log.error("Erro ao serializar histórico: {}", e.getMessage());
            model.addAttribute("historicoJson", "[]");
        }

        return "assistente/chat";
    }

    /**
     * Extrai o conteúdo da resposta do modelo Azure OpenAI
     */
    private String extrairConteudoResposta(String fullResponse) {
        // Tentativa 1: Procura por content='texto'
        Pattern pattern1 = Pattern.compile("content='([^']*)'");
        Matcher matcher1 = pattern1.matcher(fullResponse);
        if (matcher1.find()) {
            return matcher1.group(1);
        }

        // Tentativa 2: Procura por content="texto"
        Pattern pattern2 = Pattern.compile("content=\"([^\"]*)\"");
        Matcher matcher2 = pattern2.matcher(fullResponse);
        if (matcher2.find()) {
            return matcher2.group(1);
        }

        // Tentativa 3: Padrão mais genérico
        Pattern pattern3 = Pattern.compile("content[=:][\\s'\"]*(.*?)[\\s'\",}]");
        Matcher matcher3 = pattern3.matcher(fullResponse);
        if (matcher3.find()) {
            return matcher3.group(1);
        }

        log.warn("Não foi possível extrair o conteúdo da resposta: {}", fullResponse);
        return "Não foi possível processar sua pergunta. Por favor, tente novamente.";
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor // Necessário para desserialização JSON
    public static class MensagemChat implements Serializable {
        private static final long serialVersionUID = 1L;

        private String tipo; // "usuario" ou "assistente"
        private String conteudo;
        private LocalDateTime dataProcessamento;

        // Construtor adicional para compatibilidade com código existente
        public MensagemChat(String tipo, String conteudo) {
            this.tipo = tipo;
            this.conteudo = conteudo;
            this.dataProcessamento = LocalDateTime.now();
        }
    }
}
