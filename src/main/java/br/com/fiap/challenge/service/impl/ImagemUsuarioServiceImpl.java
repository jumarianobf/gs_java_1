// src/main/java/br/com/fiap/challenge/service/impl/ImagemUsuarioServiceImpl.java

package br.com.fiap.challenge.service.impl;

import br.com.fiap.challenge.config.RabbitConfig;
import br.com.fiap.challenge.messaging.ImagemUsuarioEvent;
import br.com.fiap.challenge.model.ImagemUsuarioOdontoprev;
import br.com.fiap.challenge.repository.ImagemUsuarioRepository;
import br.com.fiap.challenge.service.ImagemUsuarioService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImagemUsuarioServiceImpl implements ImagemUsuarioService {

    private final ImagemUsuarioRepository repo;
    private final RabbitTemplate rabbitTemplate;

    public ImagemUsuarioServiceImpl(
            ImagemUsuarioRepository repo,
            RabbitTemplate rabbitTemplate
    ) {
        this.repo           = repo;
        this.rabbitTemplate = rabbitTemplate;
    }

    private void publishEvent(ImagemUsuarioEvent.Tipo tipo,
                              ImagemUsuarioOdontoprev img) {
        var evt = new ImagemUsuarioEvent(
                tipo,
                img.getImagemUsuarioId(),
                img.getUsuario().getUsuarioId(),
                img.getImagemUrl(),
                img.getDataEnvio()
        );
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_IMAGENS_USUARIO,
                RabbitConfig.ROUTING_KEY_IMAGEM_USUARIO,
                evt
        );
    }

    @Override
    public ImagemUsuarioOdontoprev criar(ImagemUsuarioOdontoprev imagem) {
        var salvo = repo.save(imagem);
        publishEvent(ImagemUsuarioEvent.Tipo.CRIADO, salvo);
        return salvo;
    }

    @Override
    public List<ImagemUsuarioOdontoprev> listarTodos() {
        return repo.findAll();
    }

    @Override
    public ImagemUsuarioOdontoprev buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Imagem não encontrada"));
    }

    @Override
    public ImagemUsuarioOdontoprev atualizar(Long id, ImagemUsuarioOdontoprev upd) {
        var existente = buscarPorId(id);
        existente.setImagemUrl(upd.getImagemUrl());
        existente.setDataEnvio(upd.getDataEnvio());
        existente.setUsuario(upd.getUsuario());
        var atualizado = repo.save(existente);
        publishEvent(ImagemUsuarioEvent.Tipo.ATUALIZADO, atualizado);
        return atualizado;
    }

    @Override
    public void deletar(Long id) {
        var exist = buscarPorId(id);
        repo.deleteById(id);
        publishEvent(ImagemUsuarioEvent.Tipo.DELETADO, exist);
    }

    @Override
    public ImagemUsuarioOdontoprev salvar(ImagemUsuarioOdontoprev imagem) {
        return criar(imagem);
    }
}
