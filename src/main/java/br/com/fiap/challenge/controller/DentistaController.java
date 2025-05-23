package br.com.fiap.challenge.controller;

import br.com.fiap.challenge.model.DentistaOdontoprev;
import br.com.fiap.challenge.service.DentistaService;
import br.com.fiap.challenge.service.impl.ClinicaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/dentistas")
public class DentistaController {

    private final DentistaService dentistaService;
    private final ClinicaServiceImpl clinicaService;


    @Autowired
    public DentistaController(DentistaService dentistaService, ClinicaServiceImpl clinicaService) {
        this.dentistaService = dentistaService;
        this.clinicaService = clinicaService;
    }

    // Método para listar todos os dentistas
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public String listarDentistas(Model model) {
        List<DentistaOdontoprev> dentistas = dentistaService.listarTodos();
        model.addAttribute("dentistas", dentistas);
        return "dentista/lista";
    }

    // Método para exibir o formulário de novo dentista
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/novo")
    public String novoDentista(Model model) {
        model.addAttribute("dentista", new DentistaOdontoprev());
        model.addAttribute("clinicas", clinicaService.listarTodas());
        return "dentista/form";
    }

    // Método para salvar um novo dentista
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/salvar")
    public String salvarDentista(@ModelAttribute DentistaOdontoprev dentista) {
        dentistaService.salvar(dentista);
        return "redirect:/dentistas";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/editar/{id}")
    public String editarDentista(@PathVariable Long id, Model model) {
        DentistaOdontoprev dentista = dentistaService.buscarPorId(id);
        model.addAttribute("dentista", dentista);
        model.addAttribute("clinicas", clinicaService.listarTodas());
        return "dentista/form";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/atualizar/{id}")
    public String atualizarDentista(@PathVariable Long id, @ModelAttribute DentistaOdontoprev dentista) {
        dentistaService.atualizar(id, dentista);
        return "redirect:/dentistas";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/deletar/{id}")
    public String deletarDentista(@PathVariable Long id) {
        dentistaService.deletar(id);
        return "redirect:/dentistas";
    }
}
