package pt.studymatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import pt.studymatch.dto.AutoDeclaracaoRequest;
import pt.studymatch.model.AutoDeclaracao;
import pt.studymatch.model.Competencia;
import pt.studymatch.model.Estudante;
import pt.studymatch.repository.AutoDeclaracaoRepository;
import pt.studymatch.repository.CompetenciaRepository;
import pt.studymatch.repository.EstudanteRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AutoDeclaracaoServiceTest {

    @Mock
    private AutoDeclaracaoRepository autoDeclaracaoRepository;

    @Mock
    private EstudanteRepository estudanteRepository;

    @Mock
    private CompetenciaRepository competenciaRepository;

    @InjectMocks
    private AutoDeclaracaoService autoDeclaracaoService;

    private final Estudante estudante = new Estudante("51714", "Ana Silva", "Engenharia Informática", 2);
    private final Competencia bd = new Competencia("BD", "Bases de Dados");

    private void prepararEstudanteECompetencia() {
        when(estudanteRepository.findById(1L)).thenReturn(Optional.of(estudante));
        when(estudanteRepository.existsById(1L)).thenReturn(true);
        when(competenciaRepository.findByCodigo("BD")).thenReturn(Optional.of(bd));
        when(autoDeclaracaoRepository.save(any(AutoDeclaracao.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));
    }

    @Test
    void deveGuardarUmaRespostaNova() {
        prepararEstudanteECompetencia();
        when(autoDeclaracaoRepository.findByEstudanteIdAndCompetenciaId(any(), any()))
                .thenReturn(Optional.empty());
        AutoDeclaracao guardada = new AutoDeclaracao(estudante, bd, 3);
        when(autoDeclaracaoRepository.findByEstudanteId(1L)).thenReturn(List.of(guardada));

        var respostas = autoDeclaracaoService.guardar(1L, List.of(new AutoDeclaracaoRequest("BD", 3)));

        assertEquals(1, respostas.size());
        assertEquals("BD", respostas.get(0).codigoCompetencia());
        assertEquals(3, respostas.get(0).nivel());
    }

    @Test
    void deveAtualizarARespostaExistenteEmVezDeDuplicar() {
        prepararEstudanteECompetencia();
        AutoDeclaracao existente = new AutoDeclaracao(estudante, bd, 2);
        when(autoDeclaracaoRepository.findByEstudanteIdAndCompetenciaId(any(), any()))
                .thenReturn(Optional.of(existente));
        when(autoDeclaracaoRepository.findByEstudanteId(1L)).thenReturn(List.of(existente));

        var respostas = autoDeclaracaoService.guardar(1L, List.of(new AutoDeclaracaoRequest("BD", 5)));

        assertEquals(5, existente.getNivel());
        assertEquals(1, respostas.size());
    }

    @Test
    void deveRejeitarNivelForaDeUmACinco() {
        prepararEstudanteECompetencia();
        when(autoDeclaracaoRepository.findByEstudanteIdAndCompetenciaId(any(), any()))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> autoDeclaracaoService.guardar(1L, List.of(new AutoDeclaracaoRequest("BD", 6))));
    }

    @Test
    void deveRejeitarCompetenciaInexistente() {
        when(estudanteRepository.findById(1L)).thenReturn(Optional.of(estudante));
        when(competenciaRepository.findByCodigo("XXX")).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> autoDeclaracaoService.guardar(1L, List.of(new AutoDeclaracaoRequest("XXX", 3))));
    }

    @Test
    void deveRejeitarEstudanteInexistente() {
        when(estudanteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> autoDeclaracaoService.guardar(99L, List.of(new AutoDeclaracaoRequest("BD", 3))));
    }
}
