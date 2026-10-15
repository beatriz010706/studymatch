package pt.studymatch.service;

import java.util.List;

import org.springframework.stereotype.Service;

import pt.studymatch.dto.InscricaoRequest;
import pt.studymatch.dto.InscricaoResponse;
import pt.studymatch.model.Estudante;
import pt.studymatch.model.Inscricao;
import pt.studymatch.model.UnidadeCurricular;
import pt.studymatch.repository.EstudanteRepository;
import pt.studymatch.repository.InscricaoRepository;
import pt.studymatch.repository.UnidadeCurricularRepository;

/**
 * Percurso académico: inscrições dos estudantes nas UCs e respetivas notas.
 */
@Service
public class InscricaoService {

    private final InscricaoRepository inscricaoRepository;
    private final EstudanteRepository estudanteRepository;
    private final UnidadeCurricularRepository unidadeCurricularRepository;

    public InscricaoService(InscricaoRepository inscricaoRepository,
                            EstudanteRepository estudanteRepository,
                            UnidadeCurricularRepository unidadeCurricularRepository) {
        this.inscricaoRepository = inscricaoRepository;
        this.estudanteRepository = estudanteRepository;
        this.unidadeCurricularRepository = unidadeCurricularRepository;
    }

    public List<InscricaoResponse> listar(Long estudanteId) {
        if (!estudanteRepository.existsById(estudanteId)) {
            throw new RecursoNaoEncontradoException("Estudante não encontrado: " + estudanteId);
        }
        return inscricaoRepository.findByEstudanteId(estudanteId).stream()
                .map(InscricaoResponse::de)
                .toList();
    }

    public InscricaoResponse registar(Long estudanteId, InscricaoRequest pedido) {
        Estudante estudante = estudanteRepository.findById(estudanteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Estudante não encontrado: " + estudanteId));
        UnidadeCurricular unidadeCurricular = unidadeCurricularRepository.findById(pedido.unidadeCurricularId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Unidade curricular não encontrada: " + pedido.unidadeCurricularId()));

        // Regra de negócio: uma inscrição por UC e ano letivo
        if (inscricaoRepository.existsByEstudanteIdAndUnidadeCurricularIdAndAnoLetivo(
                estudanteId, unidadeCurricular.getId(), pedido.anoLetivo())) {
            throw new InscricaoDuplicadaException(unidadeCurricular.getNome(), pedido.anoLetivo());
        }

        // O construtor valida a nota (0 a 20) e lança IllegalArgumentException se for inválida
        Inscricao nova = new Inscricao(estudante, unidadeCurricular, pedido.anoLetivo(), pedido.nota());
        return InscricaoResponse.de(inscricaoRepository.save(nova));
    }

    public InscricaoResponse atualizarNota(Long estudanteId, Long inscricaoId, Double nota) {
        Inscricao inscricao = inscricaoRepository.findByIdAndEstudanteId(inscricaoId, estudanteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Inscrição não encontrada: " + inscricaoId));

        inscricao.definirNota(nota);
        return InscricaoResponse.de(inscricaoRepository.save(inscricao));
    }
}
