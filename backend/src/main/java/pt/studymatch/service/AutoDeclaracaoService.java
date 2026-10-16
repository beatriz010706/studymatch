package pt.studymatch.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.studymatch.dto.AutoDeclaracaoRequest;
import pt.studymatch.dto.AutoDeclaracaoResponse;
import pt.studymatch.model.AutoDeclaracao;
import pt.studymatch.model.Competencia;
import pt.studymatch.model.Estudante;
import pt.studymatch.repository.AutoDeclaracaoRepository;
import pt.studymatch.repository.CompetenciaRepository;
import pt.studymatch.repository.EstudanteRepository;

/**
 * Questionário de cold start: guarda o nível (1 a 5) que o estudante declara em cada competência.
 */
@Service
public class AutoDeclaracaoService {

    private final AutoDeclaracaoRepository autoDeclaracaoRepository;
    private final EstudanteRepository estudanteRepository;
    private final CompetenciaRepository competenciaRepository;

    public AutoDeclaracaoService(AutoDeclaracaoRepository autoDeclaracaoRepository,
                                 EstudanteRepository estudanteRepository,
                                 CompetenciaRepository competenciaRepository) {
        this.autoDeclaracaoRepository = autoDeclaracaoRepository;
        this.estudanteRepository = estudanteRepository;
        this.competenciaRepository = competenciaRepository;
    }

    public List<AutoDeclaracaoResponse> listar(Long estudanteId) {
        if (!estudanteRepository.existsById(estudanteId)) {
            throw new RecursoNaoEncontradoException("Estudante não encontrado: " + estudanteId);
        }
        return autoDeclaracaoRepository.findByEstudanteId(estudanteId).stream()
                .map(AutoDeclaracaoResponse::de)
                .toList();
    }

    /**
     * Guarda as respostas. Se o estudante já tinha respondido a uma competência, a resposta é atualizada
     * (não se duplica). Se alguma resposta for inválida, nenhuma é guardada.
     */
    @Transactional
    public List<AutoDeclaracaoResponse> guardar(Long estudanteId, List<AutoDeclaracaoRequest> respostas) {
        Estudante estudante = estudanteRepository.findById(estudanteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Estudante não encontrado: " + estudanteId));

        for (AutoDeclaracaoRequest resposta : respostas) {
            Competencia competencia = competenciaRepository.findByCodigo(resposta.codigoCompetencia())
                    .orElseThrow(() -> new RecursoNaoEncontradoException(
                            "Competência não encontrada: " + resposta.codigoCompetencia()));

            AutoDeclaracao existente = autoDeclaracaoRepository
                    .findByEstudanteIdAndCompetenciaId(estudanteId, competencia.getId())
                    .orElse(null);

            if (existente == null) {
                // O construtor valida o nível (1 a 5)
                autoDeclaracaoRepository.save(new AutoDeclaracao(estudante, competencia, resposta.nivel()));
            } else {
                existente.alterarNivel(resposta.nivel());
                autoDeclaracaoRepository.save(existente);
            }
        }

        return listar(estudanteId);
    }
}
