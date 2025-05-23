package br.com.fiap.challenge.service.impl;

import br.com.fiap.challenge.config.RabbitConfig;
import br.com.fiap.challenge.messaging.AtendimentoEvent;
import br.com.fiap.challenge.model.AtendimentoUsuarioOdontoprev;
import br.com.fiap.challenge.repository.AtendimentoUsuarioRepository;
import br.com.fiap.challenge.service.AtendimentoUsuarioService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AtendimentoUsuarioServiceImpl implements AtendimentoUsuarioService {

    private final AtendimentoUsuarioRepository atendimentoUsuarioRepository;
    private final RabbitTemplate rabbitTemplate;

    public AtendimentoUsuarioServiceImpl(AtendimentoUsuarioRepository atendimentoUsuarioRepository,
                                         RabbitTemplate rabbitTemplate) {
        this.atendimentoUsuarioRepository = atendimentoUsuarioRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    private void publishEvent(AtendimentoEvent.Tipo tipo, AtendimentoUsuarioOdontoprev a) {
        AtendimentoEvent evt = new AtendimentoEvent(
                tipo,
                a.getAtendimentoId(),
                a.getUsuario().getUsuarioId(),
                a.getDentista().getDentistaId(),
                a.getClinica().getClinicaId(),
                a.getDataAtendimento(),
                a.getDescricaoProcedimento(),
                a.getCusto(),
                a.getDataRegistro()
        );
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_ATENDIMENTOS,
                RabbitConfig.ROUTING_KEY_ATENDIMENTO,
                evt
        );
    }

    @Override
    public List<AtendimentoUsuarioOdontoprev> listarTodos() {
        return atendimentoUsuarioRepository.findAll();
    }

    @Override
    public AtendimentoUsuarioOdontoprev buscarPorId(Long id) {
        return atendimentoUsuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atendimento não encontrado"));
    }

    @Override
    public AtendimentoUsuarioOdontoprev criar(AtendimentoUsuarioOdontoprev atendimento) {
        AtendimentoUsuarioOdontoprev salvo = atendimentoUsuarioRepository.save(atendimento);
        publishEvent(AtendimentoEvent.Tipo.CRIADO, salvo);
        return salvo;
    }

    @Override
    public AtendimentoUsuarioOdontoprev atualizar(Long id, AtendimentoUsuarioOdontoprev atendimentoAtualizado) {
        AtendimentoUsuarioOdontoprev existente = buscarPorId(id);
        existente.setUsuario(atendimentoAtualizado.getUsuario());
        existente.setDentista(atendimentoAtualizado.getDentista());
        existente.setClinica(atendimentoAtualizado.getClinica());
        existente.setDataAtendimento(atendimentoAtualizado.getDataAtendimento());
        existente.setDescricaoProcedimento(atendimentoAtualizado.getDescricaoProcedimento());
        existente.setCusto(atendimentoAtualizado.getCusto());

        AtendimentoUsuarioOdontoprev atualizado = atendimentoUsuarioRepository.save(existente);
        publishEvent(AtendimentoEvent.Tipo.ATUALIZADO, atualizado);
        return atualizado;
    }

    @Override
    public void deletar(Long id) {
        AtendimentoUsuarioOdontoprev exist = buscarPorId(id);
        atendimentoUsuarioRepository.deleteById(id);
        publishEvent(AtendimentoEvent.Tipo.DELETADO, exist);
    }
}
