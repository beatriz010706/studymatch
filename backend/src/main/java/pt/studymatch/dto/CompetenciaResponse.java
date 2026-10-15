package pt.studymatch.dto;

import pt.studymatch.model.Competencia;

public record CompetenciaResponse(Long id, String codigo, String nome) {

    public static CompetenciaResponse de(Competencia competencia) {
        return new CompetenciaResponse(competencia.getId(), competencia.getCodigo(), competencia.getNome());
    }
}
