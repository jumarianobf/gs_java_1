package br.com.fiap.challenge.messaging;

import java.io.Serializable;

public class EnderecoUsuarioEvent implements Serializable {
    public enum Tipo { CRIADO, ATUALIZADO, DELETADO }

    private final Tipo tipo;
    private final Long enderecoUsuarioId;
    private final Long usuarioId;
    private final String cepUsuario;

    public EnderecoUsuarioEvent(Tipo tipo,
                                Long enderecoUsuarioId,
                                Long usuarioId,
                                String cepUsuario) {
        this.tipo              = tipo;
        this.enderecoUsuarioId = enderecoUsuarioId;
        this.usuarioId         = usuarioId;
        this.cepUsuario        = cepUsuario;
    }

    public Tipo getTipo() { return tipo; }
    public Long getEnderecoUsuarioId() { return enderecoUsuarioId; }
    public Long getUsuarioId() { return usuarioId; }
    public String getCepUsuario() { return cepUsuario; }
}
