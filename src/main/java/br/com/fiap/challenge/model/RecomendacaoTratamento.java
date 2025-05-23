package br.com.fiap.challenge.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RecomendacaoTratamento {
    private String tratamento;
    private String justificativa;
    private String prioridade;
    private LocalDateTime dataGeracao;
}
