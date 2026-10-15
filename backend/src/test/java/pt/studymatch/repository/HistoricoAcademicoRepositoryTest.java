package pt.studymatch.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import pt.studymatch.model.AutoDeclaracao;
import pt.studymatch.model.Competencia;
import pt.studymatch.model.ContribuicaoUC;
import pt.studymatch.model.Estudante;
import pt.studymatch.model.Inscricao;
import pt.studymatch.model.UnidadeCurricular;

/**
 * Testes de persistência do histórico académico, com base de dados em memória (H2).
 */
@DataJpaTest
class HistoricoAcademicoRepositoryTest {

    @Autowired
    private EstudanteRepository estudanteRepository;

    @Autowired
    private UnidadeCurricularRepository unidadeCurricularRepository;

    @Autowired
    private CompetenciaRepository competenciaRepository;

    @Autowired
    private ContribuicaoUCRepository contribuicaoUCRepository;

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @Autowired
    private AutoDeclaracaoRepository autoDeclaracaoRepository;

    private Estudante criarEstudante() {
        return estudanteRepository.save(new Estudante("51714", "Ana Silva", "Engenharia Informática", 2));
    }

    private UnidadeCurricular criarUnidadeCurricular() {
        return unidadeCurricularRepository.save(new UnidadeCurricular("Bases de Dados", 6, 2, 1));
    }

    @Test
    void deveGuardarEListarInscricoesDeUmEstudante() {
        Estudante estudante = criarEstudante();
        UnidadeCurricular bd = criarUnidadeCurricular();
        inscricaoRepository.save(new Inscricao(estudante, bd, "2025/2026", 16.0));

        List<Inscricao> inscricoes = inscricaoRepository.findByEstudanteId(estudante.getId());

        assertEquals(1, inscricoes.size());
        assertEquals(16.0, inscricoes.get(0).getNota());
    }

    @Test
    void deveRejeitarInscricaoDuplicadaNaMesmaUcEAnoLetivo() {
        Estudante estudante = criarEstudante();
        UnidadeCurricular bd = criarUnidadeCurricular();
        inscricaoRepository.saveAndFlush(new Inscricao(estudante, bd, "2025/2026", 12.0));

        assertThrows(DataIntegrityViolationException.class,
                () -> inscricaoRepository.saveAndFlush(new Inscricao(estudante, bd, "2025/2026", 15.0)));
    }

    @Test
    void devePermitirRepetirAUcEmAnoLetivoDiferente() {
        Estudante estudante = criarEstudante();
        UnidadeCurricular bd = criarUnidadeCurricular();
        inscricaoRepository.saveAndFlush(new Inscricao(estudante, bd, "2024/2025", 8.0));
        inscricaoRepository.saveAndFlush(new Inscricao(estudante, bd, "2025/2026", 12.0));

        assertEquals(2, inscricaoRepository.findByEstudanteId(estudante.getId()).size());
    }

    @Test
    void deveDetetarSeJaExisteInscricao() {
        Estudante estudante = criarEstudante();
        UnidadeCurricular bd = criarUnidadeCurricular();
        inscricaoRepository.save(new Inscricao(estudante, bd, "2025/2026", null));

        assertTrue(inscricaoRepository.existsByEstudanteIdAndUnidadeCurricularIdAndAnoLetivo(
                estudante.getId(), bd.getId(), "2025/2026"));
        assertFalse(inscricaoRepository.existsByEstudanteIdAndUnidadeCurricularIdAndAnoLetivo(
                estudante.getId(), bd.getId(), "2024/2025"));
    }

    @Test
    void deveGuardarContribuicoesDeUmaUc() {
        UnidadeCurricular bd = criarUnidadeCurricular();
        Competencia competenciaBd = competenciaRepository.save(new Competencia("BD", "Bases de Dados"));
        contribuicaoUCRepository.save(new ContribuicaoUC(bd, competenciaBd, 1.0));

        List<ContribuicaoUC> contribuicoes = contribuicaoUCRepository.findByUnidadeCurricular(bd);

        assertEquals(1, contribuicoes.size());
        assertEquals("BD", contribuicoes.get(0).getCompetencia().getCodigo());
    }

    @Test
    void deveEncontrarCompetenciaPeloCodigo() {
        competenciaRepository.save(new Competencia("PROG", "Programação"));

        assertTrue(competenciaRepository.findByCodigo("PROG").isPresent());
        assertFalse(competenciaRepository.findByCodigo("XXX").isPresent());
    }

    @Test
    void deveRejeitarDuasAutoDeclaracoesParaAMesmaCompetencia() {
        Estudante estudante = criarEstudante();
        Competencia prog = competenciaRepository.save(new Competencia("PROG", "Programação"));
        autoDeclaracaoRepository.saveAndFlush(new AutoDeclaracao(estudante, prog, 3));

        assertThrows(DataIntegrityViolationException.class,
                () -> autoDeclaracaoRepository.saveAndFlush(new AutoDeclaracao(estudante, prog, 4)));
    }

    @Test
    void deveEncontrarAutoDeclaracaoPorEstudanteECompetencia() {
        Estudante estudante = criarEstudante();
        Competencia prog = competenciaRepository.save(new Competencia("PROG", "Programação"));
        autoDeclaracaoRepository.save(new AutoDeclaracao(estudante, prog, 3));

        assertTrue(autoDeclaracaoRepository
                .findByEstudanteIdAndCompetenciaId(estudante.getId(), prog.getId()).isPresent());
    }

    @Test
    void deveEncontrarInscricaoApenasParaOEstudanteCerto() {
        Estudante ana = criarEstudante();
        Estudante rui = estudanteRepository.save(new Estudante("51715", "Rui Costa", "Engenharia Informática", 2));
        UnidadeCurricular bd = criarUnidadeCurricular();
        Inscricao inscricaoDaAna = inscricaoRepository.save(new Inscricao(ana, bd, "2025/2026", 14.0));

        assertTrue(inscricaoRepository.findByIdAndEstudanteId(inscricaoDaAna.getId(), ana.getId()).isPresent());
        assertFalse(inscricaoRepository.findByIdAndEstudanteId(inscricaoDaAna.getId(), rui.getId()).isPresent());
    }

    @Test
    void deveListarCompetenciasNaOrdemDeInsercao() {
        competenciaRepository.save(new Competencia("PROG", "Programação"));
        competenciaRepository.save(new Competencia("BD", "Bases de Dados"));

        List<Competencia> competencias = competenciaRepository.findAllByOrderByIdAsc();

        assertEquals("PROG", competencias.get(0).getCodigo());
        assertEquals("BD", competencias.get(1).getCodigo());
    }
}
