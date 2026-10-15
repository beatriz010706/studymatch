package pt.studymatch.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pt.studymatch.dto.CompetenciaResponse;
import pt.studymatch.dto.UnidadeCurricularResponse;
import pt.studymatch.service.CatalogoService;

@RestController
@RequestMapping("/api")
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping("/unidades-curriculares")
    public List<UnidadeCurricularResponse> listarUnidadesCurriculares() {
        return catalogoService.listarUnidadesCurriculares();
    }

    @GetMapping("/competencias")
    public List<CompetenciaResponse> listarCompetencias() {
        return catalogoService.listarCompetencias();
    }
}
