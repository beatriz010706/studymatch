package pt.studymatch.model.perfil;

import java.util.List;

/**
 * Resultado da parte académica de uma competência.
 * O nível é null quando não existe qualquer evidência.
 */
public record ResultadoAcademico(String codigoCompetencia, Double nivel, double confianca,
                                 List<EvidenciaAcademica> evidenciasUsadas) {
}
