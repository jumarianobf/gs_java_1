package br.com.fiap.challenge.messaging;

import br.com.fiap.challenge.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DentistaListener {

    @RabbitListener(
            queues = RabbitConfig.QUEUE_DENTISTA,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void onDentistaEvent(DentistaEvent evt) {
        switch (evt.getTipo()) {
            case CRIADO:
                System.out.printf("→ [CRIADO]     ID=%d, nome=%s%n",
                        evt.getDentistaId(), evt.getNomeDentista());
                break;
            case ATUALIZADO:
                System.out.printf("→ [ATUALIZADO] ID=%d, nome=%s%n",
                        evt.getDentistaId(), evt.getNomeDentista());
                break;
            case DELETADO:
                System.out.printf("→ [DELETADO]   ID=%d, nome=%s%n",
                        evt.getDentistaId(), evt.getNomeDentista());
                break;
        }
    }
}
