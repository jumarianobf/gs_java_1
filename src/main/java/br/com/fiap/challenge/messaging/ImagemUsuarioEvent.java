// src/main/java/br/com/fiap/challenge/messaging/ImagemUsuarioEvent.java

package br.com.fiap.challenge.messaging;

import java.io.Serializable;
import java.time.LocalDate;

public class ImagemUsuarioEvent implements Serializable {

    public enum Tipo { CRIADO, ATUALIZADO, DELETADO }

    private final Tipo tipo;
    private final Long imagemUsuarioId;
    private final Long usuarioId;
    private final String imagemUrl;
    private final LocalDate dataEnvio;

    public ImagemUsuarioEvent(
            Tipo tipo,
            Long imagemUsuarioId,
            Long usuarioId,
            String imagemUrl,
            LocalDate dataEnvio
    ) {
        this.tipo            = tipo;
        this.imagemUsuarioId = imagemUsuarioId;
        this.usuarioId       = usuarioId;
        this.imagemUrl       = imagemUrl;
        this.dataEnvio       = dataEnvio;
    }

    public Tipo getTipo() { return tipo; }
    public Long getImagemUsuarioId() { return imagemUsuarioId; }
    public Long getUsuarioId() { return usuarioId; }
    public String getImagemUrl() { return imagemUrl; }
    public LocalDate getDataEnvio() { return dataEnvio; }
}
