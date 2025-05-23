package br.com.fiap.challenge.controller;

import br.com.fiap.challenge.model.ImagemUsuarioOdontoprev;
import br.com.fiap.challenge.model.UsuarioOdontoprev;
import br.com.fiap.challenge.service.impl.ImagemUsuarioServiceImpl;
import br.com.fiap.challenge.service.impl.UsuarioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/imagens-usuarios")
public class ImagemUsuarioController {

    @Autowired
    private ImagemUsuarioServiceImpl imagemUsuarioService;

    @Autowired
    private UsuarioServiceImpl usuarioService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public String listarImagensUsuarios(Model model) {
        List<ImagemUsuarioOdontoprev> imagens = imagemUsuarioService.listarTodos();
        model.addAttribute("imagens", imagens);
        return "imagemUsuario/lista";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/novo")
    public String novaImagemUsuario(Model model) {
        List<UsuarioOdontoprev> usuarios = usuarioService.listarTodos();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("imagem", new ImagemUsuarioOdontoprev());
        return "imagemUsuario/form";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/salvar")
    public String salvarImagemUsuario(@ModelAttribute ImagemUsuarioOdontoprev imagem) {
        imagemUsuarioService.salvar(imagem);
        return "redirect:/imagens-usuarios";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/editar/{id}")
    public String editarImagemUsuario(@PathVariable Long id, Model model) {
        ImagemUsuarioOdontoprev imagem = imagemUsuarioService.buscarPorId(id);
        model.addAttribute("imagem", imagem);
        return "imagemUsuario/form";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/atualizar/{id}")
    public String atualizarImagemUsuario(@PathVariable Long id, @ModelAttribute ImagemUsuarioOdontoprev imagem) {
        imagemUsuarioService.atualizar(id, imagem);
        return "redirect:/imagens-usuarios";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/deletar/{id}")
    public String deletarImagemUsuario(@PathVariable Long id) {
        imagemUsuarioService.deletar(id);
        return "redirect:/imagens-usuarios";
    }
}
