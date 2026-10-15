package pt.studymatch.dto;

import pt.studymatch.model.UnidadeCurricular;

public record UnidadeCurricularResponse(Long id, String nome, int ects, int anoCurricular, int semestre) {

    public static UnidadeCurricularResponse de(UnidadeCurricular uc) {
        return new UnidadeCurricularResponse(uc.getId(), uc.getNome(), uc.getEcts(),
                uc.getAnoCurricular(), uc.getSemestre());
    }
}
