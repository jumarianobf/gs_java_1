package br.com.fiap.challenge.messaging;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class AtendimentoEvent implements Serializable {

    public enum Tipo { CRIADO, ATUALIZADO, DELETADO }

    private final Tipo tipo;
    private final Long atendimentoId;
    private final Long usuarioId;
    private final Long dentistaId;
    private final Long clinicaId;
    private final LocalDate dataAtendimento;
    private final String descricaoProcedimento;
    private final BigDecimal custo;
    private final LocalDate dataRegistro;

    public AtendimentoEvent(
            Tipo tipo,
            Long atendimentoId,
            Long usuarioId,
            Long dentistaId,
            Long clinicaId,
            LocalDate dataAtendimento,
            String descricaoProcedimento,
            BigDecimal custo,
            LocalDate dataRegistro
    ) {
        this.tipo = tipo;
        this.atendimentoId = atendimentoId;
        this.usuarioId = usuarioId;
        this.dentistaId = dentistaId;
        this.clinicaId = clinicaId;
        this.dataAtendimento = dataAtendimento;
        this.descricaoProcedimento = descricaoProcedimento;
        this.custo = custo;
        this.dataRegistro = dataRegistro;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public Long getAtendimentoId() {
        return atendimentoId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Long getDentistaId() {
        return dentistaId;
    }

    public Long getClinicaId() {
        return clinicaId;
    }

    public LocalDate getDataAtendimento() {
        return dataAtendimento;
    }

    public String getDescricaoProcedimento() {
        return descricaoProcedimento;
    }

    public BigDecimal getCusto() {
        return custo;
    }

    public LocalDate getDataRegistro() {
        return dataRegistro;
    }

}
