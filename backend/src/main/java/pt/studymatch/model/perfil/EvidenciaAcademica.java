package pt.studymatch.model.perfil;

/**
 * Uma nota de uma unidade curricular, já associada a uma competência com um peso.
 * Não depende da base de dados, por isso é fácil de usar nos testes.
 */
public record EvidenciaAcademica(String unidadeCurricular, String codigoCompetencia,
                                 double nota, double peso) {
}
