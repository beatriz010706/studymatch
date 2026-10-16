package pt.studymatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import pt.studymatch.dto.CompetenciaPerfilResponse;
import pt.studymatch.dto.PerfilResponse;
import pt.studymatch.model.Competencia;
import pt.studymatch.model.ContribuicaoUC;
import pt.studymatch.model.Estudante;
import pt.studymatch.model.Inscricao;
import pt.studymatch.model.UnidadeCurricular;
import pt.studymatch.model.perfil.ItemExplicacao;
import pt.studymatch.model.perfil.OrigemEvidencia;

/**
 * Testa o cálculo do perfil com os dados iniciais do projeto (ver especificação, exemplos 1 a 3).
 * Não precisa de base de dados.
 */
class PerfilServiceTest {

    private final PerfilService perfilService = new PerfilService(
            null, null, null, null, null,
            new CalculadoraAcademica(), new CombinadorColdStart(), new ExplicadorPerfil());

    private final Estudante estudante = new Estudante("51714", "Ana Silva", "Engenharia Informática", 2);

    // Competências
    private final Competencia prog = new Competencia("PROG", "Programação");
    private final Competencia bd = new Competencia("BD", "Bases de Dados");
    private final Competencia web = new Competencia("WEB", "Desenvolvimento Web");
    private final Competencia alg = new Competencia("ALG", "Algoritmos e Estruturas de Dados");
    private final Competencia es = new Competencia("ES", "Engenharia de Software");
    private final List<Competencia> competencias = List.of(prog, bd, web, alg, es);

    // Unidades curriculares
    private final UnidadeCurricular ucBd = new UnidadeCurricular("Bases de Dados", 6, 2, 1);
    private final UnidadeCurricular ucLtw = new UnidadeCurricular("Laboratório de Tecnologias Web", 6, 2, 1);
    private final UnidadeCurricular ucEs = new UnidadeCurricular("Engenharia de Software", 6, 2, 2);
    private final UnidadeCurricular ucAed = new UnidadeCurricular("Algoritmos e Estruturas de Dados", 6, 1, 2);

    // Contribuições iguais às dos dados iniciais
    private final List<ContribuicaoUC> contribuicoes = List.of(
            new ContribuicaoUC(ucBd, bd, 1.0),
            new ContribuicaoUC(ucLtw, web, 1.0),
            new ContribuicaoUC(ucLtw, prog, 0.3),
            new ContribuicaoUC(ucEs, es, 1.0),
            new ContribuicaoUC(ucEs, prog, 0.3),
            new ContribuicaoUC(ucAed, alg, 1.0),
            new ContribuicaoUC(ucAed, prog, 0.5));

    private Inscricao inscricao(UnidadeCurricular uc, Double nota) {
        return new Inscricao(estudante, uc, "2025/2026", nota);
    }

    private CompetenciaPerfilResponse competencia(PerfilResponse perfil, String codigo) {
        return perfil.competencias().stream()
                .filter(c -> c.codigo().equals(codigo))
                .findFirst()
                .orElseThrow();
    }

    private List<Inscricao> exemplo1() {
        return List.of(inscricao(ucBd, 16.0), inscricao(ucAed, 14.0), inscricao(ucLtw, 18.0));
    }

    // S1 - estudante sem dados
    @Test
    void deveDevolverTodasAsCompetenciasDesconhecidasQuandoNaoHaDados() {
        PerfilResponse perfil = perfilService.montarPerfil(1L, List.of(), contribuicoes, competencias, Map.of());

        assertEquals(5, perfil.competencias().size());
        assertEquals(0.0, perfil.maturidade(), 0.0001);
        for (CompetenciaPerfilResponse competencia : perfil.competencias()) {
            assertNull(competencia.nivel());
            assertEquals(OrigemEvidencia.DESCONHECIDA, competencia.origem());
        }
    }

    // S2 - estudante do exemplo 1 (demo A)
    @Test
    void deveCalcularOPerfilDoEstudanteComHistorico() {
        PerfilResponse perfil = perfilService.montarPerfil(1L, exemplo1(), contribuicoes, competencias, Map.of());

        assertEquals(80.0, competencia(perfil, "BD").nivel(), 0.01);
        assertEquals(77.5, competencia(perfil, "PROG").nivel(), 0.01);
        assertEquals(70.0, competencia(perfil, "ALG").nivel(), 0.01);
        assertEquals(90.0, competencia(perfil, "WEB").nivel(), 0.01);
        assertNull(competencia(perfil, "ES").nivel());

        assertEquals(0.33, competencia(perfil, "BD").confianca(), 0.01);
        assertEquals(0.27, competencia(perfil, "PROG").confianca(), 0.01);
        assertEquals(OrigemEvidencia.ACADEMICA, competencia(perfil, "BD").origem());
        assertEquals(OrigemEvidencia.DESCONHECIDA, competencia(perfil, "ES").origem());
        assertEquals(0.25, perfil.maturidade(), 0.01);
    }

    // S3 - uma nota nova atualiza o perfil
    @Test
    void deveAtualizarOPerfilQuandoEntraUmaNotaNova() {
        PerfilResponse antes = perfilService.montarPerfil(1L, exemplo1(), contribuicoes, competencias, Map.of());
        assertNull(competencia(antes, "ES").nivel());

        List<Inscricao> comNotaNova = List.of(
                inscricao(ucBd, 16.0), inscricao(ucAed, 14.0), inscricao(ucLtw, 18.0), inscricao(ucEs, 15.0));
        PerfilResponse depois = perfilService.montarPerfil(1L, comNotaNova, contribuicoes, competencias, Map.of());

        assertEquals(75.0, competencia(depois, "ES").nivel(), 0.01);
        assertTrue(depois.maturidade() > antes.maturidade());
    }

    // A demo pede para mudar uma nota ao vivo
    @Test
    void deveMudarOPerfilQuandoSeCorrigeUmaNota() {
        PerfilResponse antes = perfilService.montarPerfil(1L, exemplo1(), contribuicoes, competencias, Map.of());

        Inscricao bdCorrigida = inscricao(ucBd, 16.0);
        bdCorrigida.definirNota(12.0);
        List<Inscricao> corrigidas = List.of(bdCorrigida, inscricao(ucAed, 14.0), inscricao(ucLtw, 18.0));
        PerfilResponse depois = perfilService.montarPerfil(1L, corrigidas, contribuicoes, competencias, Map.of());

        assertEquals(80.0, competencia(antes, "BD").nivel(), 0.01);
        assertEquals(60.0, competencia(depois, "BD").nivel(), 0.01);
    }

    @Test
    void naoDeveContarInscricoesSemNota() {
        List<Inscricao> semNota = List.of(inscricao(ucBd, null));

        PerfilResponse perfil = perfilService.montarPerfil(1L, semNota, contribuicoes, competencias, Map.of());

        assertNull(competencia(perfil, "BD").nivel());
        assertEquals(0.0, perfil.maturidade(), 0.0001);
    }

    // Demo B - cold start puro
    @Test
    void deveUsarAAutoDeclaracaoQuandoNaoHaNotas() {
        PerfilResponse perfil = perfilService.montarPerfil(1L, List.of(), contribuicoes, competencias,
                Map.of("BD", 3));

        CompetenciaPerfilResponse competenciaBd = competencia(perfil, "BD");
        assertEquals(60.0, competenciaBd.nivel(), 0.01);
        assertEquals(0.4, competenciaBd.confianca(), 0.01);
        assertEquals(OrigemEvidencia.AUTO_DECLARADA, competenciaBd.origem());
        assertEquals(OrigemEvidencia.DESCONHECIDA, competencia(perfil, "PROG").origem());
        assertEquals(0.0, perfil.maturidade(), 0.0001);
    }

    // Exemplo 3 - transição: notas + auto-declaração
    @Test
    void deveMisturarNotasEAutoDeclaracao() {
        PerfilResponse perfil = perfilService.montarPerfil(1L, exemplo1(), contribuicoes, competencias,
                Map.of("PROG", 4));

        CompetenciaPerfilResponse competenciaProg = competencia(perfil, "PROG");
        assertEquals(78.8, competenciaProg.nivel(), 0.01);
        assertEquals(0.56, competenciaProg.confianca(), 0.01);
        assertEquals(OrigemEvidencia.MISTA, competenciaProg.origem());

        ItemExplicacao ultimo = competenciaProg.explicacao().get(competenciaProg.explicacao().size() - 1);
        assertEquals("AUTO_DECLARACAO", ultimo.tipo());
        assertEquals(0.29, ultimo.peso(), 0.01);
    }

    // Funcionalidade própria: a explicação lista as notas usadas
    @Test
    void deveExplicarAsNotasQueDeramOValorDeProgramacao() {
        PerfilResponse perfil = perfilService.montarPerfil(1L, exemplo1(), contribuicoes, competencias, Map.of());

        List<ItemExplicacao> explicacao = competencia(perfil, "PROG").explicacao();

        assertEquals(2, explicacao.size());
        assertEquals("NOTA", explicacao.get(0).tipo());
    }
}
