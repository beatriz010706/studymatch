package pt.studymatch.dto;

/**
 * Uma resposta do questionário de cold start: competência e nível de 1 a 5.
 */
public record AutoDeclaracaoRequest(String codigoCompetencia, int nivel) {
}
