package br.com.fiap.challenge.messaging;

import br.com.fiap.challenge.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EnderecoUsuarioListener {

    @RabbitListener(
            queues = RabbitConfig.QUEUE_END_USUARIO,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void onEnderecoUsuarioEvent(EnderecoUsuarioEvent evt) {
        switch (evt.getTipo()) {
            case CRIADO:
                System.out.printf("→ [END_USUARIO CRIADO]   ID=%d, usuarioId=%d, cep=%s%n",
                        evt.getEnderecoUsuarioId(),
                        evt.getUsuarioId(),
                        evt.getCepUsuario());
                break;
            case ATUALIZADO:
                System.out.printf("→ [END_USUARIO ATUALIZADO] ID=%d, usuarioId=%d, cep=%s%n",
                        evt.getEnderecoUsuarioId(),
                        evt.getUsuarioId(),
                        evt.getCepUsuario());
                break;
            case DELETADO:
                System.out.printf("→ [END_USUARIO DELETADO]   ID=%d, usuarioId=%d, cep=%s%n",
                        evt.getEnderecoUsuarioId(),
                        evt.getUsuarioId(),
                        evt.getCepUsuario());
                break;
        }
    }
}
