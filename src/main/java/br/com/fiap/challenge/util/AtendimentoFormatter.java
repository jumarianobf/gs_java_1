package br.com.fiap.challenge.util;

import br.com.fiap.challenge.model.AtendimentoUsuarioOdontoprev;
import br.com.fiap.challenge.model.UsuarioOdontoprev;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AtendimentoFormatter {

    public String formatarHistorico(List<AtendimentoUsuarioOdontoprev> atendimentos) {
        if (atendimentos.isEmpty()) {
            return "Sem histórico de atendimentos disponível.";
        }

        return atendimentos.stream()
                .sorted(Comparator.comparing(AtendimentoUsuarioOdontoprev::getDataAtendimento).reversed())
                .map(a -> String.format("- Data: %s | Dentista: %s | Clínica: %s | Procedimento: %s | Custo: R$ %.2f",
                        a.getDataAtendimento(),
                        a.getDentista().getNomeDentista(),
                        a.getClinica().getNomeClinica(),
                        a.getDescricaoProcedimento(),
                        a.getCusto()))
                .collect(Collectors.joining("\n"));
    }

    public String criarPromptUsuario(UsuarioOdontoprev usuario, String historicoTexto) {
        return String.format("""
                DADOS DO PACIENTE:
                Nome: %s %s
                Idade: %d anos
                Gênero: %s
                
                HISTÓRICO DE ATENDIMENTOS:
                %s
                
                Com base nos dados acima, forneça recomendações de tratamentos preventivos e cuidados específicos para este paciente.
                """,
                usuario.getNome(),
                usuario.getSobrenome(),
                calcularIdade(usuario.getDataNascimento()),
                usuario.getGenero(),
                historicoTexto);
    }

    public int calcularIdade(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            return 0;
        }
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }
}
