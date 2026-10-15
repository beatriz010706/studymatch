package pt.studymatch.dto;

import pt.studymatch.model.AutoDeclaracao;

public record AutoDeclaracaoResponse(String codigoCompetencia, int nivel) {

    public static AutoDeclaracaoResponse de(AutoDeclaracao autoDeclaracao) {
        return new AutoDeclaracaoResponse(autoDeclaracao.getCompetencia().getCodigo(), autoDeclaracao.getNivel());
    }
}
