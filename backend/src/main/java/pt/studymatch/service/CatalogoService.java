package pt.studymatch.service;

import java.util.List;

import org.springframework.stereotype.Service;

import pt.studymatch.dto.CompetenciaResponse;
import pt.studymatch.dto.UnidadeCurricularResponse;
import pt.studymatch.repository.CompetenciaRepository;
import pt.studymatch.repository.UnidadeCurricularRepository;

/**
 * Consulta das unidades curriculares e das competências disponíveis.
 */
@Service
public class CatalogoService {

    private final UnidadeCurricularRepository unidadeCurricularRepository;
    private final CompetenciaRepository competenciaRepository;

    public CatalogoService(UnidadeCurricularRepository unidadeCurricularRepository,
                           CompetenciaRepository competenciaRepository) {
        this.unidadeCurricularRepository = unidadeCurricularRepository;
        this.competenciaRepository = competenciaRepository;
    }

    public List<UnidadeCurricularResponse> listarUnidadesCurriculares() {
        return unidadeCurricularRepository.findAllByOrderByNomeAsc().stream()
                .map(UnidadeCurricularResponse::de)
                .toList();
    }

    public List<CompetenciaResponse> listarCompetencias() {
        return competenciaRepository.findAllByOrderByIdAsc().stream()
                .map(CompetenciaResponse::de)
                .toList();
    }
}
