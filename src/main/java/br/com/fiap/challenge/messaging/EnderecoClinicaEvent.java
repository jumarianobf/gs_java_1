package br.com.fiap.challenge.messaging;

import java.io.Serializable;

public class EnderecoClinicaEvent implements Serializable {

    public enum Tipo { CRIADO, ATUALIZADO, DELETADO }

    private Tipo tipo;
    private Long enderecoClinicaId;
    private Long clinicaId;
    private String cepClinica;
    // (adicione outros campos se precisar)

    public EnderecoClinicaEvent(
            Tipo tipo,
            Long enderecoClinicaId,
            Long clinicaId,
            String cepClinica
    ) {
        this.tipo               = tipo;
        this.enderecoClinicaId  = enderecoClinicaId;
        this.clinicaId          = clinicaId;
        this.cepClinica         = cepClinica;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public Long getEnderecoClinicaId() {
        return enderecoClinicaId;
    }

    public Long getClinicaId() {
        return clinicaId;
    }

    public String getCepClinica() {
        return cepClinica;
    }
}
