package br.com.fiap.challenge.messaging;

import java.io.Serializable;

public class ClinicaEvent implements Serializable {
    public enum Tipo { CRIADO, ATUALIZADO, DELETADO }

    private final Tipo tipo;
    private final Long clinicaId;
    private final String nomeClinica;
    private final String telefoneClinica;

    public ClinicaEvent(
            Tipo tipo,
            Long clinicaId,
            String nomeClinica,
            String telefoneClinica
    ) {
        this.tipo             = tipo;
        this.clinicaId        = clinicaId;
        this.nomeClinica      = nomeClinica;
        this.telefoneClinica  = telefoneClinica;
    }

    public Tipo getTipo()             { return tipo; }
    public Long getClinicaId()        { return clinicaId; }
    public String getNomeClinica()    { return nomeClinica; }
    public String getTelefoneClinica(){ return telefoneClinica; }
}
