package br.com.fiap.challenge.service.impl;

import br.com.fiap.challenge.config.RabbitConfig;
import br.com.fiap.challenge.messaging.DentistaEvent;
import br.com.fiap.challenge.model.DentistaOdontoprev;
import br.com.fiap.challenge.repository.DentistaRepository;
import br.com.fiap.challenge.service.DentistaService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DentistaServiceImpl implements DentistaService {

    private final DentistaRepository dentistaRepository;
    private final RabbitTemplate     rabbitTemplate;

    public DentistaServiceImpl(DentistaRepository dentistaRepository,
                               RabbitTemplate rabbitTemplate) {
        this.dentistaRepository = dentistaRepository;
        this.rabbitTemplate     = rabbitTemplate;
    }

    private void publishEvent(DentistaEvent.Tipo tipo, DentistaOdontoprev d) {
        var evt = new DentistaEvent(
                tipo,
                d.getDentistaId(),
                d.getNomeDentista(),
                d.getEspecialidade(),
                d.getTelefoneDentista(),
                d.getEmailDentista(),
                d.getUsuario()   != null ? d.getUsuario().getUsuarioId() : null,
                d.getClinica()   != null ? d.getClinica().getClinicaId() : null
        );
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_DENTISTAS,
                RabbitConfig.ROUTING_KEY_DENTISTA,
                evt
        );
    }

    @Override
    public DentistaOdontoprev criar(DentistaOdontoprev dentista) {
        var salvo = dentistaRepository.save(dentista);
        publishEvent(DentistaEvent.Tipo.CRIADO, salvo);
        return salvo;
    }

    @Override
    public List<DentistaOdontoprev> listarTodos() {
        return dentistaRepository.findAll();
    }

    @Override
    public DentistaOdontoprev buscarPorId(Long id) {
        return dentistaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dentista não encontrado"));
    }

    @Override
    public DentistaOdontoprev atualizar(Long id, DentistaOdontoprev dAtualizado) {
        var existente = buscarPorId(id);
        existente.setNomeDentista(dAtualizado.getNomeDentista());
        existente.setEspecialidade(dAtualizado.getEspecialidade());
        existente.setTelefoneDentista(dAtualizado.getTelefoneDentista());
        existente.setEmailDentista(dAtualizado.getEmailDentista());
        existente.setUsuario(dAtualizado.getUsuario());
        existente.setClinica(dAtualizado.getClinica());
        var atualizado = dentistaRepository.save(existente);
        publishEvent(DentistaEvent.Tipo.ATUALIZADO, atualizado);
        return atualizado;
    }

    @Override
    public void deletar(Long id) {
        var exist = buscarPorId(id);
        dentistaRepository.deleteById(id);
        publishEvent(DentistaEvent.Tipo.DELETADO, exist);
    }

    @Override
    public DentistaOdontoprev salvar(DentistaOdontoprev dentista) {
        return criar(dentista);
    }
}
