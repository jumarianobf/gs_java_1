package br.com.fiap.challenge.service.impl;

import br.com.fiap.challenge.config.RabbitConfig;
import br.com.fiap.challenge.messaging.EnderecoUsuarioEvent;
import br.com.fiap.challenge.model.EnderecoUsuarioOdontoprev;
import br.com.fiap.challenge.repository.EnderecoUsuarioRepository;
import br.com.fiap.challenge.service.EnderecoUsuarioService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnderecoUsuarioServiceImpl implements EnderecoUsuarioService {

    private final EnderecoUsuarioRepository repo;
    private final RabbitTemplate rabbitTemplate;

    public EnderecoUsuarioServiceImpl(EnderecoUsuarioRepository repo,
                                      RabbitTemplate rabbitTemplate) {
        this.repo            = repo;
        this.rabbitTemplate  = rabbitTemplate;
    }

    private void publishEvent(EnderecoUsuarioEvent.Tipo tipo,
                              EnderecoUsuarioOdontoprev e) {
        var evt = new EnderecoUsuarioEvent(
                tipo,
                e.getEnderecoUsuarioId(),
                e.getUsuario().getUsuarioId(),
                e.getCepUsuario()
        );
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_USUARIOS,
                RabbitConfig.ROUTING_KEY_END_USUARIO,
                evt
        );
    }

    @Override
    public EnderecoUsuarioOdontoprev criar(EnderecoUsuarioOdontoprev e) {
        var salvo = repo.save(e);
        publishEvent(EnderecoUsuarioEvent.Tipo.CRIADO, salvo);
        return salvo;
    }

    @Override
    public List<EnderecoUsuarioOdontoprev> listarTodos() {
        return repo.findAll();
    }

    @Override
    public EnderecoUsuarioOdontoprev buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço de usuário não encontrado"));
    }

    @Override
    public EnderecoUsuarioOdontoprev atualizar(Long id,
                                               EnderecoUsuarioOdontoprev eAtualizado) {
        var existente = buscarPorId(id);
        existente.setCepUsuario(eAtualizado.getCepUsuario());
        existente.setCidadeUsuario(eAtualizado.getCidadeUsuario());
        existente.setEstadoUsuario(eAtualizado.getEstadoUsuario());
        existente.setLogradouroUsuario(eAtualizado.getLogradouroUsuario());
        existente.setBairroUsuario(eAtualizado.getBairroUsuario());
        existente.setUsuario(eAtualizado.getUsuario());
        var atualizado = repo.save(existente);
        publishEvent(EnderecoUsuarioEvent.Tipo.ATUALIZADO, atualizado);
        return atualizado;
    }

    @Override
    public void deletar(Long id) {
        var exist = buscarPorId(id);
        repo.deleteById(id);
        publishEvent(EnderecoUsuarioEvent.Tipo.DELETADO, exist);
    }

    @Override
    public EnderecoUsuarioOdontoprev salvar(EnderecoUsuarioOdontoprev e) {
        return criar(e);
    }

    @Override
    public List<EnderecoUsuarioOdontoprev> listarTodosComUsuarios() {
        return repo.findAllWithUsuarios();
    }
}
