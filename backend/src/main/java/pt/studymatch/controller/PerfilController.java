package pt.studymatch.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pt.studymatch.dto.PerfilResponse;
import pt.studymatch.service.PerfilService;

@RestController
@RequestMapping("/api/estudantes/{estudanteId}/perfil")
public class PerfilController {

    private final PerfilService perfilService;

    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @GetMapping
    public PerfilResponse obterPerfil(@PathVariable("estudanteId") Long estudanteId) {
        return perfilService.obterPerfil(estudanteId);
    }
}
