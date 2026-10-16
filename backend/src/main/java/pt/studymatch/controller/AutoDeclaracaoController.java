package pt.studymatch.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pt.studymatch.dto.AutoDeclaracaoRequest;
import pt.studymatch.dto.AutoDeclaracaoResponse;
import pt.studymatch.service.AutoDeclaracaoService;

@RestController
@RequestMapping("/api/estudantes/{estudanteId}/auto-declaracao")
public class AutoDeclaracaoController {

    private final AutoDeclaracaoService autoDeclaracaoService;

    public AutoDeclaracaoController(AutoDeclaracaoService autoDeclaracaoService) {
        this.autoDeclaracaoService = autoDeclaracaoService;
    }

    @GetMapping
    public List<AutoDeclaracaoResponse> listar(@PathVariable("estudanteId") Long estudanteId) {
        return autoDeclaracaoService.listar(estudanteId);
    }

    @PostMapping
    public List<AutoDeclaracaoResponse> guardar(@PathVariable("estudanteId") Long estudanteId,
                                                @RequestBody List<AutoDeclaracaoRequest> respostas) {
        return autoDeclaracaoService.guardar(estudanteId, respostas);
    }
}
