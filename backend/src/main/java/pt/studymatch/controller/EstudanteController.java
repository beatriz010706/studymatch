package pt.studymatch.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pt.studymatch.dto.EstudanteRequest;
import pt.studymatch.model.Estudante;
import pt.studymatch.service.EstudanteService;

@RestController
@RequestMapping("/api/estudantes")
public class EstudanteController {

    private final EstudanteService estudanteService;

    public EstudanteController(EstudanteService estudanteService) {
        this.estudanteService = estudanteService;
    }

    @GetMapping
    public List<Estudante> listar() {
        return estudanteService.listar();
    }

    @GetMapping("/{id}")
    public Estudante obter(@PathVariable("id") Long id) {
        return estudanteService.obter(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Estudante criar(@Valid @RequestBody EstudanteRequest pedido) {
        return estudanteService.criar(pedido);
    }

    @PutMapping("/{id}")
    public Estudante atualizar(@PathVariable("id") Long id, @Valid @RequestBody EstudanteRequest pedido) {
        return estudanteService.atualizar(id, pedido);
    }
}
