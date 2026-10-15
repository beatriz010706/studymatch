package pt.studymatch.service;

/**
 * Lançada quando o estudante já está inscrito na mesma UC no mesmo ano letivo. Devolve HTTP 409.
 */
public class InscricaoDuplicadaException extends RuntimeException {

    public InscricaoDuplicadaException(String unidadeCurricular, String anoLetivo) {
        super("O estudante já tem inscrição em " + unidadeCurricular + " no ano letivo " + anoLetivo);
    }
}
