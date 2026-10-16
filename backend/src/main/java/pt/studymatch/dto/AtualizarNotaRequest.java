package pt.studymatch.dto;

/**
 * Pedido para registar ou corrigir a nota de uma inscrição. Nota null apaga a nota.
 */
public record AtualizarNotaRequest(Double nota) {
}
