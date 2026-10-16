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
import pt.studymatch.dto.AtualizarNotaRequest;
import pt.studymatch.dto.InscricaoRequest;
import pt.studymatch.dto.InscricaoResponse;
import pt.studymatch.service.InscricaoService;

@RestController
@RequestMapping("/api/estudantes/{estudanteId}/inscricoes")
public class InscricaoController {

    private final InscricaoService inscricaoService;

    public InscricaoController(InscricaoService inscricaoService) {
        this.inscricaoService = inscricaoService;
    }

    @GetMapping
    public List<InscricaoResponse> listar(@PathVariable("estudanteId") Long estudanteId) {
        return inscricaoService.listar(estudanteId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InscricaoResponse registar(@PathVariable("estudanteId") Long estudanteId,
                                      @Valid @RequestBody InscricaoRequest pedido) {
        return inscricaoService.registar(estudanteId, pedido);
    }

    // Corrigir a nota de uma inscrição (útil para mudar dados ao vivo na demonstração)
    @PutMapping("/{inscricaoId}")
    public InscricaoResponse atualizarNota(@PathVariable("estudanteId") Long estudanteId,
                                           @PathVariable("inscricaoId") Long inscricaoId,
                                           @RequestBody AtualizarNotaRequest pedido) {
        return inscricaoService.atualizarNota(estudanteId, inscricaoId, pedido.nota());
    }
}
