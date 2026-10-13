package pt.studymatch.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Dados recebidos para criar um estudante.
 */
public record EstudanteRequest(
        @NotBlank(message = "O número de aluno é obrigatório") String numeroAluno,
        @NotBlank(message = "O nome é obrigatório") String nome,
        @NotBlank(message = "O curso é obrigatório") String curso,
        @Min(value = 1, message = "O ano curricular deve ser pelo menos 1")
        @Max(value = 5, message = "O ano curricular deve ser no máximo 5") int anoCurricular) {
}
