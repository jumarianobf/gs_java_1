package br.com.fiap.challenge.messaging;

import br.com.fiap.challenge.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UsuarioCriadoListener {

    @RabbitListener(
            queues = RabbitConfig.QUEUE_USUARIO,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void onUsuarioEvent(UsuarioEvent evt) {
        switch (evt.getTipo()) {
            case CRIADO:
                System.out.printf("→ [CRIADO]     ID=%d, nome=%s %s%n",
                        evt.getUsuarioId(), evt.getNome(), evt.getSobrenome());
                break;
            case ATUALIZADO:
                System.out.printf("→ [ATUALIZADO] ID=%d, nome=%s %s%n",
                        evt.getUsuarioId(), evt.getNome(), evt.getSobrenome());
                break;
            case DELETADO:
                System.out.printf("→ [DELETADO]   ID=%d, nome=%s %s%n",
                        evt.getUsuarioId(), evt.getNome(), evt.getSobrenome());
                break;
        }
    }
}
