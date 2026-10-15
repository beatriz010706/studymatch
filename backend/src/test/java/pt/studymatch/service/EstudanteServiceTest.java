package pt.studymatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import pt.studymatch.dto.EstudanteRequest;
import pt.studymatch.model.Estudante;
import pt.studymatch.repository.EstudanteRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EstudanteServiceTest {

    @Mock
    private EstudanteRepository estudanteRepository;

    @InjectMocks
    private EstudanteService estudanteService;

    private Estudante estudanteComId(Long id, String numeroAluno, String nome) {
        Estudante estudante = new Estudante(numeroAluno, nome, "Engenharia Informática", 2);
        ReflectionTestUtils.setField(estudante, "id", id);
        return estudante;
    }

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

    @Test
    void deveAtualizarOsDadosDoEstudante() {
        Estudante existente = estudanteComId(1L, "51714", "Ana Silva");
        when(estudanteRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(estudanteRepository.findByNumeroAluno("51714")).thenReturn(Optional.of(existente));
        when(estudanteRepository.save(any(Estudante.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        EstudanteRequest pedido = new EstudanteRequest("51714", "Ana Maria Silva", "Engenharia Informática", 3);
        Estudante atualizado = estudanteService.atualizar(1L, pedido);

        assertEquals("Ana Maria Silva", atualizado.getNome());
        assertEquals(3, atualizado.getAnoCurricular());
    }

    @Test
    void deveRejeitarNumeroAlunoQuePertenceAOutroEstudante() {
        Estudante ana = estudanteComId(1L, "51714", "Ana Silva");
        Estudante rui = estudanteComId(2L, "51715", "Rui Costa");
        when(estudanteRepository.findById(1L)).thenReturn(Optional.of(ana));
        when(estudanteRepository.findByNumeroAluno("51715")).thenReturn(Optional.of(rui));

        EstudanteRequest pedido = new EstudanteRequest("51715", "Ana Silva", "Engenharia Informática", 2);

        assertThrows(NumeroAlunoDuplicadoException.class, () -> estudanteService.atualizar(1L, pedido));
        verify(estudanteRepository, never()).save(any(Estudante.class));
    }

    @Test
    void deveRejeitarAtualizacaoDeEstudanteInexistente() {
        when(estudanteRepository.findById(99L)).thenReturn(Optional.empty());

        EstudanteRequest pedido = new EstudanteRequest("51714", "Ana Silva", "Engenharia Informática", 2);

        assertThrows(RecursoNaoEncontradoException.class, () -> estudanteService.atualizar(99L, pedido));
    }

    @Test
    void deveDevolverErroQuandoOEstudanteNaoExiste() {
        when(estudanteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> estudanteService.obter(99L));
    }
}
