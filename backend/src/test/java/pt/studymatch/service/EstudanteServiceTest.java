package pt.studymatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pt.studymatch.dto.EstudanteRequest;
import pt.studymatch.model.Estudante;
import pt.studymatch.repository.EstudanteRepository;

@ExtendWith(MockitoExtension.class)
class EstudanteServiceTest {

    @Mock
    private EstudanteRepository estudanteRepository;

    @InjectMocks
    private EstudanteService estudanteService;

    @Test
    void deveCriarEstudanteQuandoNumeroAlunoEUnico() {
        EstudanteRequest pedido = new EstudanteRequest("51714", "Ana Silva", "Engenharia Informática", 2);
        when(estudanteRepository.existsByNumeroAluno("51714")).thenReturn(false);
        when(estudanteRepository.save(any(Estudante.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Estudante criado = estudanteService.criar(pedido);

        assertEquals("51714", criado.getNumeroAluno());
        assertEquals("Ana Silva", criado.getNome());
    }

    @Test
    void deveRejeitarNumeroAlunoDuplicado() {
        EstudanteRequest pedido = new EstudanteRequest("51714", "Ana Silva", "Engenharia Informática", 2);
        when(estudanteRepository.existsByNumeroAluno("51714")).thenReturn(true);

        assertThrows(NumeroAlunoDuplicadoException.class, () -> estudanteService.criar(pedido));

        verify(estudanteRepository, never()).save(any(Estudante.class));
    }
}
