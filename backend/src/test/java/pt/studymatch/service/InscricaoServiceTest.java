package pt.studymatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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

import pt.studymatch.dto.InscricaoRequest;
import pt.studymatch.dto.InscricaoResponse;
import pt.studymatch.model.Estudante;
import pt.studymatch.model.Inscricao;
import pt.studymatch.model.UnidadeCurricular;
import pt.studymatch.repository.EstudanteRepository;
import pt.studymatch.repository.InscricaoRepository;
import pt.studymatch.repository.UnidadeCurricularRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class InscricaoServiceTest {

    @Mock
    private InscricaoRepository inscricaoRepository;

    @Mock
    private EstudanteRepository estudanteRepository;

    @Mock
    private UnidadeCurricularRepository unidadeCurricularRepository;

    @InjectMocks
    private InscricaoService inscricaoService;

    private final Estudante estudante = new Estudante("51714", "Ana Silva", "Engenharia Informática", 2);
    private final UnidadeCurricular bd = new UnidadeCurricular("Bases de Dados", 6, 2, 1);

    private void prepararEstudanteEUc() {
        when(estudanteRepository.findById(1L)).thenReturn(Optional.of(estudante));
        when(unidadeCurricularRepository.findById(3L)).thenReturn(Optional.of(bd));
        when(inscricaoRepository.existsByEstudanteIdAndUnidadeCurricularIdAndAnoLetivo(
                anyLong(), any(), anyString())).thenReturn(false);
        when(inscricaoRepository.save(any(Inscricao.class))).thenAnswer(invocacao -> invocacao.getArgument(0));
    }

    @Test
    void deveRegistarInscricaoComNota() {
        prepararEstudanteEUc();

        InscricaoResponse resposta = inscricaoService.registar(1L, new InscricaoRequest(3L, "2025/2026", 16.0));

        assertEquals("Bases de Dados", resposta.unidadeCurricular());
        assertEquals(16.0, resposta.nota(), 0.0001);
    }

    @Test
    void deveRejeitarNotaForaDoIntervalo() {
        prepararEstudanteEUc();

        assertThrows(IllegalArgumentException.class,
                () -> inscricaoService.registar(1L, new InscricaoRequest(3L, "2025/2026", 25.0)));
        verify(inscricaoRepository, never()).save(any(Inscricao.class));
    }

    @Test
    void deveRejeitarInscricaoDuplicada() {
        prepararEstudanteEUc();
        when(inscricaoRepository.existsByEstudanteIdAndUnidadeCurricularIdAndAnoLetivo(
                anyLong(), any(), anyString())).thenReturn(true);

        assertThrows(InscricaoDuplicadaException.class,
                () -> inscricaoService.registar(1L, new InscricaoRequest(3L, "2025/2026", 14.0)));
        verify(inscricaoRepository, never()).save(any(Inscricao.class));
    }

    @Test
    void deveRejeitarEstudanteInexistente() {
        when(estudanteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> inscricaoService.registar(99L, new InscricaoRequest(3L, "2025/2026", 14.0)));
    }

    @Test
    void deveRejeitarUnidadeCurricularInexistente() {
        when(estudanteRepository.findById(1L)).thenReturn(Optional.of(estudante));
        when(unidadeCurricularRepository.findById(77L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> inscricaoService.registar(1L, new InscricaoRequest(77L, "2025/2026", 14.0)));
    }

    @Test
    void deveCorrigirANotaDeUmaInscricao() {
        Inscricao inscricao = new Inscricao(estudante, bd, "2025/2026", 12.0);
        when(inscricaoRepository.findByIdAndEstudanteId(5L, 1L)).thenReturn(Optional.of(inscricao));
        when(inscricaoRepository.save(any(Inscricao.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        InscricaoResponse resposta = inscricaoService.atualizarNota(1L, 5L, 17.0);

        assertEquals(17.0, resposta.nota(), 0.0001);
    }

    @Test
    void deveRejeitarCorrecaoDeInscricaoQueNaoExiste() {
        when(inscricaoRepository.findByIdAndEstudanteId(5L, 1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> inscricaoService.atualizarNota(1L, 5L, 17.0));
    }
}
