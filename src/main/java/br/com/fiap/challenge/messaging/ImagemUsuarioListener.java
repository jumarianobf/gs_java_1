package br.com.fiap.challenge.messaging;

import br.com.fiap.challenge.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ImagemUsuarioListener {

    @RabbitListener(
            queues = RabbitConfig.QUEUE_IMAGEM_USUARIO,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void onImagemUsuarioEvent(ImagemUsuarioEvent evt) {
        switch (evt.getTipo()) {
            case CRIADO     -> System.out.printf("→ [IMG_USR CRIADO]     ID=%d, userId=%d, url=%s%n",
                    evt.getImagemUsuarioId(), evt.getUsuarioId(), evt.getImagemUrl());
            case ATUALIZADO -> System.out.printf("→ [IMG_USR ATUALIZADO] ID=%d, userId=%d, url=%s%n",
                    evt.getImagemUsuarioId(), evt.getUsuarioId(), evt.getImagemUrl());
            case DELETADO   -> System.out.printf("→ [IMG_USR DELETADO]   ID=%d, userId=%d, url=%s%n",
                    evt.getImagemUsuarioId(), evt.getUsuarioId(), evt.getImagemUrl());
        }
    }
}
