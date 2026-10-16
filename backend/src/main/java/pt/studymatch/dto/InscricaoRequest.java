package pt.studymatch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Pedido para inscrever um estudante numa UC. A nota é opcional.
 */
public record InscricaoRequest(
        @NotNull(message = "A unidade curricular é obrigatória") Long unidadeCurricularId,
        @NotBlank(message = "O ano letivo é obrigatório") String anoLetivo,
        Double nota) {
}
