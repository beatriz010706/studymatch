package pt.studymatch.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import pt.studymatch.service.InscricaoDuplicadaException;
import pt.studymatch.service.NumeroAlunoDuplicadoException;
import pt.studymatch.service.RecursoNaoEncontradoException;

/**
 * Traduz exceções em respostas HTTP, sempre com o corpo { "erro": "mensagem" }.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(NumeroAlunoDuplicadoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> tratarNumeroDuplicado(NumeroAlunoDuplicadoException excecao) {
        return Map.of("erro", excecao.getMessage());
    }

    @ExceptionHandler(InscricaoDuplicadaException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> tratarInscricaoDuplicada(InscricaoDuplicadaException excecao) {
        return Map.of("erro", excecao.getMessage());
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> tratarNaoEncontrado(RecursoNaoEncontradoException excecao) {
        return Map.of("erro", excecao.getMessage());
    }

    // Regras de domínio violadas (ex.: nota fora de 0-20, nível fora de 1-5)
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> tratarPedidoInvalido(IllegalArgumentException excecao) {
        return Map.of("erro", excecao.getMessage());
    }

    // Erros das anotações @Valid (campos obrigatórios, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> tratarValidacao(MethodArgumentNotValidException excecao) {
        String mensagem = excecao.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getDefaultMessage())
                .findFirst()
                .orElse("Pedido inválido");
        return Map.of("erro", mensagem);
    }
}
