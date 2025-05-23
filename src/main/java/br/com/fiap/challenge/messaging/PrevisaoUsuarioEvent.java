package br.com.fiap.challenge.messaging;

import java.io.Serializable;
import java.time.LocalDate;

public class PrevisaoUsuarioEvent implements Serializable {
    public enum Tipo { CRIADO, ATUALIZADO, DELETADO }

    private final Tipo tipo;
    private final Long previsaoId;
    private final Long usuarioId;
    private final Long imagemUsuarioId;
    private final String texto;
    private final Float probabilidade;
    private final String recomendacao;
    private final LocalDate dataPrevisao;

    public PrevisaoUsuarioEvent(
            Tipo tipo,
            Long previsaoId,
            Long usuarioId,
            Long imagemUsuarioId,
            String texto,
            Float probabilidade,
            String recomendacao,
            LocalDate dataPrevisao
    ) {
        this.tipo            = tipo;
        this.previsaoId      = previsaoId;
        this.usuarioId       = usuarioId;
        this.imagemUsuarioId = imagemUsuarioId;
        this.texto           = texto;
        this.probabilidade   = probabilidade;
        this.recomendacao    = recomendacao;
        this.dataPrevisao    = dataPrevisao;
    }

    public Tipo getTipo() { return tipo; }
    public Long getPrevisaoId() { return previsaoId; }
    public Long getUsuarioId() { return usuarioId; }
    public Long getImagemUsuarioId() { return imagemUsuarioId; }
    public String getTexto() { return texto; }
    public Float getProbabilidade() { return probabilidade; }
    public String getRecomendacao() { return recomendacao; }
    public LocalDate getDataPrevisao() { return dataPrevisao; }
}
