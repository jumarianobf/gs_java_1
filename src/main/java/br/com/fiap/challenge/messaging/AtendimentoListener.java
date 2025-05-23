package br.com.fiap.challenge.messaging;

import br.com.fiap.challenge.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AtendimentoListener {

    @RabbitListener(
            queues = RabbitConfig.QUEUE_ATENDIMENTO,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void onAtendimentoEvent(AtendimentoEvent evt) {
        switch (evt.getTipo()) {
            case CRIADO ->
                    System.out.printf("→ [ATENDIMENTO CRIADO] ID=%d, usuário=%d, dentista=%d%n",
                            evt.getAtendimentoId(), evt.getUsuarioId(), evt.getDentistaId());
            case ATUALIZADO ->
                    System.out.printf("→ [ATENDIMENTO ATUALIZADO] ID=%d%n", evt.getAtendimentoId());
            case DELETADO ->
                    System.out.printf("→ [ATENDIMENTO DELETADO] ID=%d%n", evt.getAtendimentoId());
        }
    }
}

