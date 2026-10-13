package pt.studymatch.service;

/**
 * Lançada quando se tenta registar um número de aluno que já existe.
 */
public class NumeroAlunoDuplicadoException extends RuntimeException {

    public NumeroAlunoDuplicadoException(String numeroAluno) {
        super("Já existe um estudante com o número de aluno " + numeroAluno);
    }
}
