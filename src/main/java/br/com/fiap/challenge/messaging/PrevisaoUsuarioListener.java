package br.com.fiap.challenge.messaging;

import br.com.fiap.challenge.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PrevisaoUsuarioListener {

    @RabbitListener(
            queues = RabbitConfig.QUEUE_PREVISAO_USUARIO,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void onPrevisaoUsuarioEvent(PrevisaoUsuarioEvent evt) {
        switch (evt.getTipo()) {
            case CRIADO     -> System.out.printf("→ [PREV CRIADO]     ID=%d, user=%d, img=%d, prob=%.2f%n",
                    evt.getPrevisaoId(),
                    evt.getUsuarioId(),
                    evt.getImagemUsuarioId(),
                    evt.getProbabilidade());
            case ATUALIZADO -> System.out.printf("→ [PREV ATUALIZADO] ID=%d, user=%d, img=%d, prob=%.2f%n",
                    evt.getPrevisaoId(),
                    evt.getUsuarioId(),
                    evt.getImagemUsuarioId(),
                    evt.getProbabilidade());
            case DELETADO   -> System.out.printf("→ [PREV DELETADO]   ID=%d, user=%d, img=%d, prob=%.2f%n",
                    evt.getPrevisaoId(),
                    evt.getUsuarioId(),
                    evt.getImagemUsuarioId(),
                    evt.getProbabilidade());
        }
    }
}
