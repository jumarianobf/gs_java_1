package br.com.fiap.challenge.repository;

import br.com.fiap.challenge.model.AtendimentoUsuarioOdontoprev;
import br.com.fiap.challenge.model.UsuarioOdontoprev;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AtendimentoUsuarioRepository extends JpaRepository<AtendimentoUsuarioOdontoprev, Long> {
    List<AtendimentoUsuarioOdontoprev> findByUsuario(UsuarioOdontoprev usuario);
}
