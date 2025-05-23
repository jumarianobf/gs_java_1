package br.com.fiap.challenge.messaging;

import java.io.Serializable;

public class DentistaEvent implements Serializable {
    public enum Tipo { CRIADO, ATUALIZADO, DELETADO }

    private Tipo tipo;
    private Long dentistaId;
    private String nomeDentista;
    private String especialidade;
    private String telefoneDentista;
    private String emailDentista;
    private Long usuarioId;
    private Long clinicaId;

    public DentistaEvent(Tipo tipo,
                         Long dentistaId,
                         String nomeDentista,
                         String especialidade,
                         String telefoneDentista,
                         String emailDentista,
                         Long usuarioId,
                         Long clinicaId) {
        this.tipo            = tipo;
        this.dentistaId      = dentistaId;
        this.nomeDentista    = nomeDentista;
        this.especialidade   = especialidade;
        this.telefoneDentista= telefoneDentista;
        this.emailDentista   = emailDentista;
        this.usuarioId       = usuarioId;
        this.clinicaId       = clinicaId;
    }

    // getters...
    public Tipo getTipo() { return tipo; }
    public Long getDentistaId() { return dentistaId; }
    public String getNomeDentista() { return nomeDentista; }
    public String getEspecialidade() { return especialidade; }
    public String getTelefoneDentista() { return telefoneDentista; }
    public String getEmailDentista() { return emailDentista; }
    public Long getUsuarioId() { return usuarioId; }
    public Long getClinicaId() { return clinicaId; }
}
