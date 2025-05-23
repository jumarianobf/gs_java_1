package br.com.fiap.challenge.messaging;

import br.com.fiap.challenge.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ClinicaListener {

    @RabbitListener(
            queues = RabbitConfig.QUEUE_CLINICA,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void onClinicaEvent(ClinicaEvent evt) {
        switch (evt.getTipo()) {
            case CRIADO:
                System.out.printf("→ [CRIADO]     ID=%d, nome=%s%n",
                        evt.getClinicaId(), evt.getNomeClinica());
                break;
            case ATUALIZADO:
                System.out.printf("→ [ATUALIZADO] ID=%d, nome=%s%n",
                        evt.getClinicaId(), evt.getNomeClinica());
                break;
            case DELETADO:
                System.out.printf("→ [DELETADO]   ID=%d, nome=%s%n",
                        evt.getClinicaId(), evt.getNomeClinica());
                break;
        }
    }
}
