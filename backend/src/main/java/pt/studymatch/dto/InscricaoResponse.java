package pt.studymatch.dto;

import pt.studymatch.model.Inscricao;

public record InscricaoResponse(Long id, Long unidadeCurricularId, String unidadeCurricular,
                                String anoLetivo, Double nota) {

    public static InscricaoResponse de(Inscricao inscricao) {
        return new InscricaoResponse(
                inscricao.getId(),
                inscricao.getUnidadeCurricular().getId(),
                inscricao.getUnidadeCurricular().getNome(),
                inscricao.getAnoLetivo(),
                inscricao.getNota());
    }
}
