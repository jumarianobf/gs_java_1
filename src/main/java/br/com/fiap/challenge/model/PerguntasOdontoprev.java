package br.com.fiap.challenge.model;

import java.time.LocalDateTime;

public class PerguntasOdontoprev {
    private static final long serialVersionUID = 1L;

    private String pergunta;
    private String resposta;
    private LocalDateTime dataProcessamento;

    public PerguntasOdontoprev() {}

    public PerguntasOdontoprev(String pergunta, String resposta) {
        this.pergunta = pergunta;
        this.resposta = resposta;
        this.dataProcessamento = LocalDateTime.now();
    }

    public String getPergunta() {
        return pergunta;
    }

    public void setPergunta(String pergunta) {
        this.pergunta = pergunta;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public LocalDateTime getDataProcessamento(){
        return dataProcessamento;
    }

    public void setDataProcessamento(LocalDateTime dataProcessamento){
        this.dataProcessamento = dataProcessamento;
    }

}

