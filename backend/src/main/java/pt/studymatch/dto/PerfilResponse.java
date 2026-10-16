package pt.studymatch.dto;

import java.util.List;

/**
 * Perfil completo de um estudante. A maturidade (0 a 1) mede quanta evidência académica existe.
 */
public record PerfilResponse(Long estudanteId, double maturidade, List<CompetenciaPerfilResponse> competencias) {
}
