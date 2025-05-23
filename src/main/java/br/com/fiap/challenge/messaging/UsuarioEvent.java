package br.com.fiap.challenge.messaging;

import java.io.Serializable;
import java.time.LocalDate;

public class UsuarioEvent implements Serializable {
    public enum Tipo { CRIADO, ATUALIZADO, DELETADO }

    private Tipo tipo;
    private Long usuarioId;
    private String cpf;
    private String nome;
    private String sobrenome;
    private LocalDate dataNascimento;
    private String genero;
    private LocalDate dataCadastro;

    // Se quiser incluir endereços e contatos:
    // private List<EnderecoDto> enderecos;
    // private List<ContatoDto>  contatos;

    public UsuarioEvent(Tipo tipo,
                        Long usuarioId,
                        String cpf,
                        String nome,
                        String sobrenome,
                        LocalDate dataNascimento,
                        String genero,
                        LocalDate dataCadastro) {
        this.tipo           = tipo;
        this.usuarioId      = usuarioId;
        this.cpf            = cpf;
        this.nome           = nome;
        this.sobrenome      = sobrenome;
        this.dataNascimento = dataNascimento;
        this.genero         = genero;
        this.dataCadastro   = dataCadastro;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public String getGenero() {
        return genero;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }
}
