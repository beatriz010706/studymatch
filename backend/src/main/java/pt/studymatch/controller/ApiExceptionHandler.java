package pt.studymatch.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import pt.studymatch.service.NumeroAlunoDuplicadoException;

/**
 * Traduz exceções de negócio em respostas HTTP.
 * Os erros de validação (@Valid) já devolvem 400 por omissão.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(NumeroAlunoDuplicadoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> tratarNumeroDuplicado(NumeroAlunoDuplicadoException excecao) {
        return Map.of("erro", excecao.getMessage());
    }
}
