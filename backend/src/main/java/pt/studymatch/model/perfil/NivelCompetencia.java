package pt.studymatch.model.perfil;

/**
 * Resultado final de uma competência no perfil (académico + auto-declaração).
 * O nível é null quando a competência é desconhecida.
 */
public record NivelCompetencia(String codigoCompetencia, Double nivel, double confianca,
                               OrigemEvidencia origem) {
}
