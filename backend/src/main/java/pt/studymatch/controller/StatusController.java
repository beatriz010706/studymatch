package pt.studymatch.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pt.studymatch.dto.StatusResponse;
import pt.studymatch.service.EstudanteService;

@RestController
@RequestMapping("/api")
public class StatusController {

    private final EstudanteService estudanteService;

    public StatusController(EstudanteService estudanteService) {
        this.estudanteService = estudanteService;
    }

    /**
     * Endpoint de estado. Ao contar os estudantes, também prova que o backend
     * consegue ler da base de dados.
     */
    @GetMapping("/status")
    public StatusResponse obterEstado() {
        long total = estudanteService.contar();
        return new StatusResponse("OK", "StudyMatch API", "ligada", total);
    }
}
