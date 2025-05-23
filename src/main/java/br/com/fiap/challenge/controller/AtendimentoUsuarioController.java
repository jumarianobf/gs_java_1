package br.com.fiap.challenge.controller;

import br.com.fiap.challenge.model.AtendimentoUsuarioOdontoprev;
import br.com.fiap.challenge.service.impl.AtendimentoUsuarioServiceImpl;
import br.com.fiap.challenge.service.DentistaService;
import br.com.fiap.challenge.service.UsuarioService;
import br.com.fiap.challenge.service.impl.ClinicaServiceImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/atendimentos")
public class AtendimentoUsuarioController {

    private final AtendimentoUsuarioServiceImpl atendimentoUsuarioService;
    private final UsuarioService usuarioService;
    private final DentistaService dentistaService;
    private final ClinicaServiceImpl clinicaService;

    public AtendimentoUsuarioController(AtendimentoUsuarioServiceImpl atendimentoUsuarioService,
                                        UsuarioService usuarioService,
                                        DentistaService dentistaService,
                                        ClinicaServiceImpl clinicaService) {
        this.atendimentoUsuarioService = atendimentoUsuarioService;
        this.usuarioService = usuarioService;
        this.dentistaService = dentistaService;
        this.clinicaService = clinicaService;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public String listarAtendimentos(Model model) {
        model.addAttribute("atendimentos", atendimentoUsuarioService.listarTodos());
        model.addAttribute("activeMenu", "atendimentos");
        return "atendimento/lista";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/novo")
    public String novoAtendimento(Model model) {
        model.addAttribute("atendimento", new AtendimentoUsuarioOdontoprev());
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("dentistas", dentistaService.listarTodos());
        model.addAttribute("clinicas", clinicaService.listarTodas());
        model.addAttribute("activeMenu", "atendimentos");
        return "atendimento/form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/editar/{id}")
    public String editarAtendimento(@PathVariable Long id, Model model) {
        AtendimentoUsuarioOdontoprev atendimento = atendimentoUsuarioService.buscarPorId(id);
        model.addAttribute("atendimento", atendimento);
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("dentistas", dentistaService.listarTodos());
        model.addAttribute("clinicas", clinicaService.listarTodas());
        model.addAttribute("activeMenu", "atendimentos");
        return "atendimento/form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/salvar")
    public String salvarAtendimento(@ModelAttribute AtendimentoUsuarioOdontoprev atendimento) {
        atendimentoUsuarioService.criar(atendimento);
        return "redirect:/atendimentos";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/atualizar/{id}")
    public String atualizarAtendimento(@PathVariable Long id, @ModelAttribute AtendimentoUsuarioOdontoprev atendimento) {
        atendimentoUsuarioService.atualizar(id, atendimento);
        return "redirect:/atendimentos";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/deletar/{id}")
    public String deletarAtendimento(@PathVariable Long id) {
        atendimentoUsuarioService.deletar(id);
        return "redirect:/atendimentos";
    }
}
