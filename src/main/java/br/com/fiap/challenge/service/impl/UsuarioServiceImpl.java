package br.com.fiap.challenge.service.impl;

import br.com.fiap.challenge.config.RabbitConfig;
import br.com.fiap.challenge.messaging.UsuarioEvent;
import br.com.fiap.challenge.model.UsuarioOdontoprev;
import br.com.fiap.challenge.repository.UsuarioRepository;
import br.com.fiap.challenge.service.UsuarioService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RabbitTemplate    rabbitTemplate;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              RabbitTemplate rabbitTemplate) {
        this.usuarioRepository = usuarioRepository;
        this.rabbitTemplate    = rabbitTemplate;
    }

    private void publishEvent(UsuarioEvent.Tipo tipo, UsuarioOdontoprev u) {
        UsuarioEvent evt = new UsuarioEvent(
                tipo,
                u.getUsuarioId(),
                u.getCpf(),
                u.getNome(),
                u.getSobrenome(),
                u.getDataNascimento(),
                u.getGenero(),
                u.getDataCadastro()
        );
        // sempre usa a mesma routing key e queue
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_USUARIOS,
                RabbitConfig.ROUTING_KEY_USUARIO,
                evt
        );
    }

    @Override
    public UsuarioOdontoprev criar(UsuarioOdontoprev usuario) {
        if (usuarioRepository.existsByCpf(usuario.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }
        UsuarioOdontoprev salvo = usuarioRepository.save(usuario);
        publishEvent(UsuarioEvent.Tipo.CRIADO, salvo);
        return salvo;
    }

    @Override
    public UsuarioOdontoprev atualizar(Long id, UsuarioOdontoprev usuarioAtualizado) {
        UsuarioOdontoprev existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        existente.setNome(usuarioAtualizado.getNome());
        existente.setCpf(usuarioAtualizado.getCpf());
        existente.setSobrenome(usuarioAtualizado.getSobrenome());
        existente.setDataNascimento(usuarioAtualizado.getDataNascimento());
        existente.setGenero(usuarioAtualizado.getGenero());
        UsuarioOdontoprev atualizado = usuarioRepository.save(existente);
        publishEvent(UsuarioEvent.Tipo.ATUALIZADO, atualizado);
        return atualizado;
    }

    @Override
    public void deletar(Long id) {
        UsuarioOdontoprev exist = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuarioRepository.deleteById(id);
        publishEvent(UsuarioEvent.Tipo.DELETADO, exist);
    }

    @Override
    public List<UsuarioOdontoprev> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    public UsuarioOdontoprev buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @Override
    public UsuarioOdontoprev salvar(UsuarioOdontoprev usuario) {
        // delega a criar(), que já dispara evento
        return criar(usuario);
    }
}
