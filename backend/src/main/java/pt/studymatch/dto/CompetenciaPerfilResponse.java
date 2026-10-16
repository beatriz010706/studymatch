package pt.studymatch.dto;

import java.util.List;

import pt.studymatch.model.perfil.ItemExplicacao;
import pt.studymatch.model.perfil.OrigemEvidencia;

/**
 * Uma competência no perfil. O nível é null quando a competência é DESCONHECIDA.
 */
public record CompetenciaPerfilResponse(String codigo, String nome, Double nivel, double confianca,
                                        OrigemEvidencia origem, List<ItemExplicacao> explicacao) {
}
