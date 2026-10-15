package pt.studymatch.service;

/**
 * Lançada quando um estudante, UC ou competência não existe. Devolve HTTP 404.
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
