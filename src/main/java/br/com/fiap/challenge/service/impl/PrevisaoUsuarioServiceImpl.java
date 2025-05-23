// src/main/java/br/com/fiap/challenge/service/impl/PrevisaoUsuarioServiceImpl.java

package br.com.fiap.challenge.service.impl;

import br.com.fiap.challenge.config.RabbitConfig;
import br.com.fiap.challenge.messaging.PrevisaoUsuarioEvent;
import br.com.fiap.challenge.model.PrevisaoUsuarioOdontoprev;
import br.com.fiap.challenge.repository.PrevisaoUsuarioRepository;
import br.com.fiap.challenge.service.PrevisaoUsuarioService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrevisaoUsuarioServiceImpl implements PrevisaoUsuarioService {

    private final PrevisaoUsuarioRepository repo;
    private final RabbitTemplate rabbitTemplate;

    public PrevisaoUsuarioServiceImpl(
            PrevisaoUsuarioRepository repo,
            RabbitTemplate rabbitTemplate
    ) {
        this.repo           = repo;
        this.rabbitTemplate = rabbitTemplate;
    }

    private void publishEvent(PrevisaoUsuarioEvent.Tipo tipo,
                              PrevisaoUsuarioOdontoprev p) {
        var evt = new PrevisaoUsuarioEvent(
                tipo,
                p.getPrevisaoUsuarioId(),
                p.getUsuario().getUsuarioId(),
                p.getImagemUsuario().getImagemUsuarioId(),
                p.getPrevisaoTexto(),
                p.getProbabilidade(),
                p.getRecomendacao(),
                p.getDataPrevisao()
        );
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_PREVISAO_USUARIOS,
                RabbitConfig.ROUTING_KEY_PREVISAO_USUARIO,
                evt
        );
    }

    @Override
    public PrevisaoUsuarioOdontoprev criar(PrevisaoUsuarioOdontoprev previsao) {
        var salvo = repo.save(previsao);
        publishEvent(PrevisaoUsuarioEvent.Tipo.CRIADO, salvo);
        return salvo;
    }

    @Override
    public List<PrevisaoUsuarioOdontoprev> listarTodos() {
        return repo.findAll();
    }

    @Override
    public PrevisaoUsuarioOdontoprev buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Previsão não encontrada"));
    }

    @Override
    public PrevisaoUsuarioOdontoprev atualizar(Long id, PrevisaoUsuarioOdontoprev upd) {
        var existente = buscarPorId(id);
        existente.setImagemUsuario(upd.getImagemUsuario());
        existente.setUsuario(upd.getUsuario());
        existente.setPrevisaoTexto(upd.getPrevisaoTexto());
        existente.setProbabilidade(upd.getProbabilidade());
        existente.setRecomendacao(upd.getRecomendacao());
        existente.setDataPrevisao(upd.getDataPrevisao());
        var atualizado = repo.save(existente);
        publishEvent(PrevisaoUsuarioEvent.Tipo.ATUALIZADO, atualizado);
        return atualizado;
    }

    @Override
    public void deletar(Long id) {
        var exist = buscarPorId(id);
        repo.deleteById(id);
        publishEvent(PrevisaoUsuarioEvent.Tipo.DELETADO, exist);
    }

    @Override
    public PrevisaoUsuarioOdontoprev salvar(PrevisaoUsuarioOdontoprev previsao) {
        return criar(previsao);
    }
}
