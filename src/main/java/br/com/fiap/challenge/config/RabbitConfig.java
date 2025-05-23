package br.com.fiap.challenge.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // ——— Usuários —————————————————————————————————————————————
    public static final String EXCHANGE_USUARIOS   = "exchange.usuarios";
    public static final String ROUTING_KEY_USUARIO = "usuario.key";
    public static final String QUEUE_USUARIO       = "fila.usuario";

    // ——— Atendimentos ————————————————————————————————————————————
    public static final String EXCHANGE_ATENDIMENTOS   = "exchange.atendimentos";
    public static final String ROUTING_KEY_ATENDIMENTO = "atendimento.key";
    public static final String QUEUE_ATENDIMENTO       = "fila.atendimento";

    // ——— Clínicas ———————————————————————————————————————————————
    public static final String EXCHANGE_CLINICAS   = "exchange.clinicas";
    public static final String ROUTING_KEY_CLINICA = "clinica.key";
    public static final String QUEUE_CLINICA       = "fila.clinica";

    // ——— Dentistas —————————————————————————————————————————————
    public static final String EXCHANGE_DENTISTAS  = "exchange.dentistas";
    public static final String ROUTING_KEY_DENTISTA= "dentista.key";
    public static final String QUEUE_DENTISTA      = "fila.dentista";

    // ——— Endereços de Clínica —————————————————————————————————————
    public static final String ROUTING_KEY_ENDERECO_CLINICA = "clinica.endereco.key";
    public static final String QUEUE_ENDERECO_CLINICA       = "fila.clinica.endereco";

    // ——— Endereço de Usuário —————————————————————————————————————
    public static final String ROUTING_KEY_END_USUARIO    = "usuario.endereco.key";
    public static final String QUEUE_END_USUARIO          = "fila.usuario.endereco";

    // ——— Imagens de Usuário ——————————————————————————————————————
    public static final String EXCHANGE_IMAGENS_USUARIO   = "exchange.imagens.usuario";
    public static final String ROUTING_KEY_IMAGEM_USUARIO = "imagem.usuario.key";
    public static final String QUEUE_IMAGEM_USUARIO       = "fila.imagem.usuario";

    // ——— Previsões de Usuário ———————————————————————————————————
    public static final String EXCHANGE_PREVISAO_USUARIOS     = "exchange.previsoes.usuario";
    public static final String ROUTING_KEY_PREVISAO_USUARIO   = "previsao.usuario.key";
    public static final String QUEUE_PREVISAO_USUARIO         = "fila.previsao.usuario";

    // 1) JSON converter
    @Bean public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // 2) RabbitTemplate com JSON
    @Bean public RabbitTemplate rabbitTemplate(
            ConnectionFactory cf,
            Jackson2JsonMessageConverter converter
    ) {
        RabbitTemplate tpl = new RabbitTemplate(cf);
        tpl.setMessageConverter(converter);
        return tpl;
    }

    // ——— Topologia Usuário ———————————————————————————————————————
    @Bean public DirectExchange exchangeUsuarios() {
        return ExchangeBuilder.directExchange(EXCHANGE_USUARIOS)
                .durable(true)
                .build();
    }

    @Bean public Queue filaUsuario() {
        return QueueBuilder.durable(QUEUE_USUARIO).build();
    }

    @Bean public Binding bindingUsuario(Queue filaUsuario, DirectExchange exchangeUsuarios) {
        return BindingBuilder.bind(filaUsuario)
                .to(exchangeUsuarios)
                .with(ROUTING_KEY_USUARIO);
    }

    // ——— Topologia Atendimentos ———————————————————————————————————
    @Bean public DirectExchange exchangeAtendimentos() {
        return ExchangeBuilder.directExchange(EXCHANGE_ATENDIMENTOS)
                .durable(true)
                .build();
    }

    @Bean public Queue filaAtendimento() {
        return QueueBuilder.durable(QUEUE_ATENDIMENTO).build();
    }

    @Bean public Binding bindingAtendimento(Queue filaAtendimento, DirectExchange exchangeAtendimentos) {
        return BindingBuilder.bind(filaAtendimento)
                .to(exchangeAtendimentos)
                .with(ROUTING_KEY_ATENDIMENTO);
    }

    // ——— Topologia Clínicas —————————————————————————————————————
    @Bean public DirectExchange exchangeClinicas() {
        return ExchangeBuilder.directExchange(EXCHANGE_CLINICAS)
                .durable(true)
                .build();
    }

    @Bean public Queue filaClinica() {
        return QueueBuilder.durable(QUEUE_CLINICA).build();
    }

    @Bean public Binding bindingClinica(Queue filaClinica, DirectExchange exchangeClinicas) {
        return BindingBuilder.bind(filaClinica)
                .to(exchangeClinicas)
                .with(ROUTING_KEY_CLINICA);
    }

    // ——— Topologia Dentistas —————————————————————————————————————
    @Bean public DirectExchange exchangeDentistas() {
        return ExchangeBuilder.directExchange(EXCHANGE_DENTISTAS)
                .durable(true)
                .build();
    }

    @Bean public Queue filaDentista() {
        return QueueBuilder.durable(QUEUE_DENTISTA).build();
    }

    @Bean public Binding bindingDentista(Queue filaDentista, DirectExchange exchangeDentistas) {
        return BindingBuilder.bind(filaDentista)
                .to(exchangeDentistas)
                .with(ROUTING_KEY_DENTISTA);
    }

    // 3d) Fila de endereço de clínica (reutiliza a mesma exchangeClinicas)
    @Bean
    public Queue filaEnderecoClinica() {
        return QueueBuilder
                .durable(QUEUE_ENDERECO_CLINICA)
                .build();
    }

    // 3e) Binding clinica.endereco.key → fila.clinica.endereco
    @Bean
    public Binding bindingEnderecoClinica(
            Queue filaEnderecoClinica,
            DirectExchange exchangeClinicas
    ) {
        return BindingBuilder
                .bind(filaEnderecoClinica)
                .to(exchangeClinicas)
                .with(ROUTING_KEY_ENDERECO_CLINICA);
    }

    // ——— Fila e binding de Endereço de Usuário (reusa mesma exchangeUsuarios) ——
    @Bean public Queue filaEnderecoUsuario() {
        return QueueBuilder.durable(QUEUE_END_USUARIO).build();
    }

    @Bean public Binding bindingEnderecoUsuario(
            Queue filaEnderecoUsuario,
            DirectExchange exchangeUsuarios
    ) {
        return BindingBuilder.bind(filaEnderecoUsuario)
                .to(exchangeUsuarios)
                .with(ROUTING_KEY_END_USUARIO);
    }

    @Bean
    public DirectExchange exchangeImagensUsuario() {
        return ExchangeBuilder
                .directExchange(EXCHANGE_IMAGENS_USUARIO)
                .durable(true)
                .build();
    }

    /**
     * 3y) Fila para eventos de Imagem de Usuário
     */
    @Bean
    public Queue filaImagemUsuario() {
        return QueueBuilder
                .durable(QUEUE_IMAGEM_USUARIO)
                .build();
    }

    /**
     * 3z) Binding routingKey → fila.imagem.usuario
     */
    @Bean
    public Binding bindingImagemUsuario(
            Queue filaImagemUsuario,
            DirectExchange exchangeImagensUsuario
    ) {
        return BindingBuilder
                .bind(filaImagemUsuario)
                .to(exchangeImagensUsuario)
                .with(ROUTING_KEY_IMAGEM_USUARIO);
    }

    // 1) Exchange dedicada
    @Bean
    public DirectExchange exchangePrevisaoUsuarios() {
        return ExchangeBuilder
                .directExchange(EXCHANGE_PREVISAO_USUARIOS)
                .durable(true)
                .build();
    }

    // 2) Fila de Previsão de Usuário
    @Bean
    public Queue filaPrevisaoUsuario() {
        return QueueBuilder
                .durable(QUEUE_PREVISAO_USUARIO)
                .build();
    }

    // 3) Binding routingKey → fila
    @Bean
    public Binding bindingPrevisaoUsuario(
            Queue filaPrevisaoUsuario,
            DirectExchange exchangePrevisaoUsuarios
    ) {
        return BindingBuilder
                .bind(filaPrevisaoUsuario)
                .to(exchangePrevisaoUsuarios)
                .with(ROUTING_KEY_PREVISAO_USUARIO);
    }

    // 4) Listener container factory
    @Bean public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory cf,
            Jackson2JsonMessageConverter converter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(cf);
        factory.setMessageConverter(converter);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}
