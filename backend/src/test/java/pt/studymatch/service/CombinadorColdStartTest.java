package pt.studymatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import pt.studymatch.model.perfil.NivelCompetencia;
import pt.studymatch.model.perfil.OrigemEvidencia;
import pt.studymatch.model.perfil.ResultadoAcademico;

class CombinadorColdStartTest {

    private final CombinadorColdStart combinador = new CombinadorColdStart();

    private ResultadoAcademico semEvidencia(String codigo) {
        return new ResultadoAcademico(codigo, null, 0.0, List.of());
    }

    private ResultadoAcademico comEvidencia(String codigo, double nivel, double confianca) {
        return new ResultadoAcademico(codigo, nivel, confianca, List.of());
    }

    // C1 - cold start puro: só auto-declaração
    @Test
    void deveUsarSoAAutoDeclaracaoQuandoNaoHaNotas() {
        NivelCompetencia resultado = combinador.combinar(semEvidencia("BD"), 3);

        assertEquals(60.0, resultado.nivel(), 0.01);
        assertEquals(0.4, resultado.confianca(), 0.0001);
        assertEquals(OrigemEvidencia.AUTO_DECLARADA, resultado.origem());
    }

    // C2 - cold start sem qualquer informação
    @Test
    void deveDevolverDesconhecidaSemNotasESemAutoDeclaracao() {
        NivelCompetencia resultado = combinador.combinar(semEvidencia("BD"), null);

        assertNull(resultado.nivel());
        assertEquals(0.0, resultado.confianca(), 0.0001);
        assertEquals(OrigemEvidencia.DESCONHECIDA, resultado.origem());
    }

    // C3 - cenário normal: só evidência académica
    @Test
    void deveManterOsValoresAcademicosQuandoNaoHaAutoDeclaracao() {
        NivelCompetencia resultado = combinador.combinar(comEvidencia("PROG", 77.5, 0.2667), null);

        assertEquals(77.5, resultado.nivel(), 0.01);
        assertEquals(0.2667, resultado.confianca(), 0.0001);
        assertEquals(OrigemEvidencia.ACADEMICA, resultado.origem());
    }

    // C4 - transição: as duas fontes
    @Test
    void deveMisturarAsDuasFontesComPesoDaAutoDeclaracaoReduzido() {
        double confiancaAcademica = 0.8 / 3;

        NivelCompetencia resultado = combinador.combinar(comEvidencia("PROG", 77.5, confiancaAcademica), 4);

        assertEquals(78.81, resultado.nivel(), 0.01);
        assertEquals(0.56, resultado.confianca(), 0.01);
        assertEquals(OrigemEvidencia.MISTA, resultado.origem());
    }

    // C5 - transição / limite: com confiança académica máxima a auto-declaração é ignorada
    @Test
    void deveIgnorarAAutoDeclaracaoQuandoAConfiancaAcademicaEMaxima() {
        NivelCompetencia resultado = combinador.combinar(comEvidencia("PROG", 70.0, 1.0), 1);

        assertEquals(70.0, resultado.nivel(), 0.01);
        assertEquals(1.0, resultado.confianca(), 0.0001);
        assertEquals(OrigemEvidencia.ACADEMICA, resultado.origem());
    }

    // C6 - regra do cold start: o peso da auto-declaração desce com as notas
    @Test
    void devePerderPesoAAutoDeclaracaoAMedidaQueHaNotas() {
        assertEquals(0.40, combinador.pesoAutoDeclaracao(0.0), 0.01);
        assertEquals(0.27, combinador.pesoAutoDeclaracao(1.0 / 3), 0.01);
        assertEquals(0.13, combinador.pesoAutoDeclaracao(2.0 / 3), 0.01);
        assertEquals(0.0, combinador.pesoAutoDeclaracao(1.0), 0.0001);
    }

    @Test
    void oPesoDaAutoDeclaracaoNuncaSobeQuandoAConfiancaAcademicaSobe() {
        assertTrue(combinador.pesoAutoDeclaracao(0.6) < combinador.pesoAutoDeclaracao(0.2));
    }

    // C7 - inválidos
    @Test
    void deveRejeitarAutoDeclaracaoAbaixoDeUm() {
        assertThrows(IllegalArgumentException.class, () -> combinador.combinar(semEvidencia("BD"), 0));
    }

    @Test
    void deveRejeitarAutoDeclaracaoAcimaDeCinco() {
        assertThrows(IllegalArgumentException.class, () -> combinador.combinar(semEvidencia("BD"), 6));
    }

    @Test
    void deveRejeitarConfiancaAcademicaForaDeZeroAUm() {
        assertThrows(IllegalArgumentException.class, () -> combinador.pesoAutoDeclaracao(1.5));
    }

    // C7 - limites válidos da auto-declaração
    @Test
    void deveAceitarAutoDeclaracaoNosLimites() {
        assertEquals(20.0, combinador.combinar(semEvidencia("BD"), 1).nivel(), 0.01);
        assertEquals(100.0, combinador.combinar(semEvidencia("BD"), 5).nivel(), 0.01);
    }

    // C8 - transição do cold start para evidência académica
    @Test
    void deveMudarDeAutoDeclaradaParaMistaQuandoChegaUmaNota() {
        // Antes: o estudante só respondeu ao questionário (BD = 3)
        NivelCompetencia antes = combinador.combinar(semEvidencia("BD"), 3);
        assertEquals(OrigemEvidencia.AUTO_DECLARADA, antes.origem());
        assertEquals(60.0, antes.nivel(), 0.01);

        // Depois: entra uma nota de 16 (nível 80, confiança 1/3)
        NivelCompetencia depois = combinador.combinar(comEvidencia("BD", 80.0, 1.0 / 3), 3);

        assertEquals(OrigemEvidencia.MISTA, depois.origem());
        assertEquals(71.11, depois.nivel(), 0.01);
        assertEquals(0.60, depois.confianca(), 0.01);
        assertTrue(depois.confianca() > antes.confianca());
    }
}
