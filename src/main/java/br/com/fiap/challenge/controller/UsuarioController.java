package br.com.fiap.challenge.controller;

import br.com.fiap.challenge.model.UsuarioOdontoprev;
import br.com.fiap.challenge.service.impl.UsuarioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioServiceImpl usuarioService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public String listarUsuarios(Model model) {
        List<UsuarioOdontoprev> usuarios = usuarioService.listarTodos();
        model.addAttribute("usuarios", usuarios);
        return "usuario/lista";
    }

    // Método para exibir o formulário de novo usuário
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/novo")
    public String novoUsuario(Model model) {
        model.addAttribute("usuario", new UsuarioOdontoprev());
        return "usuario/form";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/salvar")
    public String salvarUsuario(@ModelAttribute UsuarioOdontoprev usuario, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.salvar(usuario);
            redirectAttributes.addFlashAttribute("mensagem", "Usuário cadastrado com sucesso!");
            return "redirect:/enderecos-usuario/novo?usuarioId=" + usuario.getUsuarioId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar o usuário: " + e.getMessage());
            return "redirect:/usuarios/novo";
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        try {
            UsuarioOdontoprev usuario = usuarioService.buscarPorId(id);
            model.addAttribute("usuario", usuario);
            return "usuario/form";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Usuário não encontrado");
            return "redirect:/usuarios";
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/atualizar/{id}")
    public String atualizarUsuario(@PathVariable Long id, @ModelAttribute UsuarioOdontoprev usuario) {
        usuarioService.atualizar(id, usuario);
        return "redirect:/usuarios";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/deletar/{id}")
    public String deletarUsuario(@PathVariable Long id, Model model) {
        try {
            usuarioService.deletar(id);
            return "redirect:/usuarios";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Erro ao deletar usuário");
            return "redirect:/usuarios";
        }
    }

}
