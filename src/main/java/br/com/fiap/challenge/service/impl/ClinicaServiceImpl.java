package br.com.fiap.challenge.service.impl;

import br.com.fiap.challenge.config.RabbitConfig;
import br.com.fiap.challenge.messaging.ClinicaEvent;
import br.com.fiap.challenge.model.ClinicaOdontoprev;
import br.com.fiap.challenge.repository.ClinicaRepository;
import br.com.fiap.challenge.service.ClinicaService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;

@Service
public class ClinicaServiceImpl implements ClinicaService {

    private final ClinicaRepository clinicaRepository;
    private final RabbitTemplate    rabbitTemplate;

    public ClinicaServiceImpl(
            ClinicaRepository clinicaRepository,
            RabbitTemplate rabbitTemplate
    ) {
        this.clinicaRepository = clinicaRepository;
        this.rabbitTemplate    = rabbitTemplate;
    }

    private void publishEvent(ClinicaEvent.Tipo tipo, ClinicaOdontoprev c) {
        ClinicaEvent evt = new ClinicaEvent(
                tipo,
                c.getClinicaId(),
                c.getNomeClinica(),
                c.getTelefoneClinica()
        );
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_CLINICAS,
                RabbitConfig.ROUTING_KEY_CLINICA,
                evt
        );
    }

    @Override
    @Transactional
    public ClinicaOdontoprev criar(ClinicaOdontoprev clinica) {
        ClinicaOdontoprev salvo = clinicaRepository.save(clinica);
        publishEvent(ClinicaEvent.Tipo.CRIADO, salvo);
        return salvo;
    }

    @Override
    @Transactional
    public List<ClinicaOdontoprev> listarTodas() {
        List<ClinicaOdontoprev> todas = clinicaRepository.findAll();
        // força fetch de endereços, se for EAGER você nem precisa
        todas.forEach(c -> { if (c.getEnderecos()!=null) c.getEnderecos().size(); });
        return todas;
    }

    @Override
    @Transactional
    public ClinicaOdontoprev buscarPorId(Long id) {
        return clinicaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clínica não encontrada"));
    }

    @Override
    @Transactional
    public ClinicaOdontoprev atualizar(Long id, ClinicaOdontoprev atualizado) {
        ClinicaOdontoprev existente = buscarPorId(id);
        existente.setNomeClinica(atualizado.getNomeClinica());
        existente.setTelefoneClinica(atualizado.getTelefoneClinica());
        ClinicaOdontoprev salvo = clinicaRepository.save(existente);
        publishEvent(ClinicaEvent.Tipo.ATUALIZADO, salvo);
        return salvo;
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        ClinicaOdontoprev exist = buscarPorId(id);
        clinicaRepository.deleteById(id);
        publishEvent(ClinicaEvent.Tipo.DELETADO, exist);
    }

    @Override
    public ClinicaOdontoprev salvar(ClinicaOdontoprev clinica) {
        return criar(clinica);
    }
}
