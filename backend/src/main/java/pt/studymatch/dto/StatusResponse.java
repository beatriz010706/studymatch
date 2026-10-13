package pt.studymatch.dto;

/**
 * Resposta do endpoint de estado da aplicação.
 */
public record StatusResponse(String estado, String aplicacao, String baseDeDados, long totalEstudantes) {
}
