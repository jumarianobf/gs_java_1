package br.com.fiap.challenge.messaging;

import br.com.fiap.challenge.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EnderecoClinicaListener {

    @RabbitListener(
            queues = RabbitConfig.QUEUE_ENDERECO_CLINICA,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void onEnderecoClinicaEvent(EnderecoClinicaEvent evt) {
        switch (evt.getTipo()) {
            case CRIADO:
                System.out.printf("→ [END_CLINICA CRIADO] ID=%d, clinicaId=%d, cep=%s%n",
                        evt.getEnderecoClinicaId(),
                        evt.getClinicaId(),
                        evt.getCepClinica());
                break;
            case ATUALIZADO:
                System.out.printf("→ [END_CLINICA ATUALIZADO] ID=%d, clinicaId=%d, cep=%s%n",
                        evt.getEnderecoClinicaId(),
                        evt.getClinicaId(),
                        evt.getCepClinica());
                break;
            case DELETADO:
                System.out.printf("→ [END_CLINICA DELETADO] ID=%d, clinicaId=%d, cep=%s%n",
                        evt.getEnderecoClinicaId(),
                        evt.getClinicaId(),
                        evt.getCepClinica());
                break;
        }
    }
}
