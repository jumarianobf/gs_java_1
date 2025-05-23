package br.com.fiap.challenge.service.impl;

import br.com.fiap.challenge.config.RabbitConfig;
import br.com.fiap.challenge.messaging.EnderecoClinicaEvent;
import br.com.fiap.challenge.model.EnderecoClinicaOdontoprev;
import br.com.fiap.challenge.repository.EnderecoClinicaRepository;
import br.com.fiap.challenge.service.EnderecoClinicaService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EnderecoClinicaServiceImpl implements EnderecoClinicaService {

    private final EnderecoClinicaRepository enderecoClinicaRepository;
    private final RabbitTemplate rabbitTemplate;

    public EnderecoClinicaServiceImpl(EnderecoClinicaRepository enderecoClinicaRepository,
                                      RabbitTemplate rabbitTemplate) {
        this.enderecoClinicaRepository = enderecoClinicaRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    // ---- Validação de CEP ----
    private boolean validarCep(String cep) {
        String regex = "^[0-9]{5}-[0-9]{3}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(cep);
        return matcher.matches();
    }

    // ---- Publicação de evento ----
    private void publishEvent(EnderecoClinicaEvent.Tipo tipo, EnderecoClinicaOdontoprev e) {
        EnderecoClinicaEvent evt = new EnderecoClinicaEvent(
                tipo,
                e.getEnderecoClinicaId(),
                e.getClinica().getClinicaId(),
                e.getCepClinica()
        );
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_CLINICAS,              // reuse exchange.clinicas
                RabbitConfig.ROUTING_KEY_ENDERECO_CLINICA,   // nova routing key
                evt
        );
    }

    @Override
    public EnderecoClinicaOdontoprev criar(EnderecoClinicaOdontoprev endereco) {
        if (!validarCep(endereco.getCepClinica())) {
            throw new IllegalArgumentException("CEP inválido. Formato esperado: 12345-678");
        }
        EnderecoClinicaOdontoprev salvo = enderecoClinicaRepository.save(endereco);
        publishEvent(EnderecoClinicaEvent.Tipo.CRIADO, salvo);
        return salvo;
    }

    @Override
    public List<EnderecoClinicaOdontoprev> listarTodos() {
        return enderecoClinicaRepository.findAll();
    }

    @Override
    public EnderecoClinicaOdontoprev buscarPorId(Long id) {
        return enderecoClinicaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço de clínica não encontrado"));
    }

    @Override
    public EnderecoClinicaOdontoprev atualizar(Long id, EnderecoClinicaOdontoprev updated) {
        if (!validarCep(updated.getCepClinica())) {
            throw new IllegalArgumentException("CEP inválido. Formato esperado: 12345-678");
        }
        EnderecoClinicaOdontoprev existente = buscarPorId(id);
        existente.setCepClinica(updated.getCepClinica());
        existente.setCidadeClinica(updated.getCidadeClinica());
        existente.setEstadoClinica(updated.getEstadoClinica());
        existente.setLogradouroClinica(updated.getLogradouroClinica());
        existente.setBairroClinica(updated.getBairroClinica());
        existente.setClinica(updated.getClinica());

        EnderecoClinicaOdontoprev salvo = enderecoClinicaRepository.save(existente);
        publishEvent(EnderecoClinicaEvent.Tipo.ATUALIZADO, salvo);
        return salvo;
    }

    @Override
    public void deletar(Long id) {
        EnderecoClinicaOdontoprev exist = buscarPorId(id);
        enderecoClinicaRepository.deleteById(id);
        publishEvent(EnderecoClinicaEvent.Tipo.DELETADO, exist);
    }

    @Override
    public EnderecoClinicaOdontoprev salvar(EnderecoClinicaOdontoprev endereco) {
        // delega a criar(), que já faz validação e dispara o evento
        return criar(endereco);
    }
}
