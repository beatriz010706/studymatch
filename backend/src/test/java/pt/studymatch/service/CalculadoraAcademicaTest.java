package pt.studymatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import pt.studymatch.model.perfil.EvidenciaAcademica;
import pt.studymatch.model.perfil.ResultadoAcademico;

class CalculadoraAcademicaTest {

    private final CalculadoraAcademica calculadora = new CalculadoraAcademica();

    private EvidenciaAcademica evidencia(String uc, double nota, double peso) {
        return new EvidenciaAcademica(uc, "PROG", nota, peso);
    }

    // T1 - cenário normal
    @Test
    void deveCalcularNivelEConfiancaComDuasUcs() {
        List<EvidenciaAcademica> evidencias = List.of(
                evidencia("Algoritmos e Estruturas de Dados", 14, 0.5),
                evidencia("Laboratório de Tecnologias Web", 18, 0.3));

        ResultadoAcademico resultado = calculadora.calcular("PROG", evidencias);

        assertEquals(77.5, resultado.nivel(), 0.01);
        assertEquals(0.267, resultado.confianca(), 0.01);
        assertEquals(2, resultado.evidenciasUsadas().size());
    }

    // T2 - sem evidências
    @Test
    void deveDevolverDesconhecidoQuandoNaoHaEvidencias() {
        ResultadoAcademico resultado = calculadora.calcular("PROG", List.of());

        assertNull(resultado.nivel());
        assertEquals(0.0, resultado.confianca(), 0.0001);
    }

    @Test
    void deveTratarListaNulaComoSemEvidencias() {
        ResultadoAcademico resultado = calculadora.calcular("PROG", null);

        assertNull(resultado.nivel());
    }

    // T3 - nota de reprovação não conta
    @Test
    void deveIgnorarNotaReprovada() {
        ResultadoAcademico resultado = calculadora.calcular("PROG", List.of(evidencia("UC A", 9, 1.0)));

        assertNull(resultado.nivel());
        assertEquals(0.0, resultado.confianca(), 0.0001);
    }

    // T4 - limite: nota exatamente 10 conta
    @Test
    void deveContarNotaExatamenteDezComoAprovada() {
        ResultadoAcademico resultado = calculadora.calcular("PROG", List.of(evidencia("UC A", 10, 1.0)));

        assertEquals(50.0, resultado.nivel(), 0.01);
        assertEquals(0.333, resultado.confianca(), 0.01);
    }

    // T5 - limite: nota máxima
    @Test
    void deveDarNivelCemComNotaVinte() {
        ResultadoAcademico resultado = calculadora.calcular("PROG", List.of(evidencia("UC A", 20, 1.0)));

        assertEquals(100.0, resultado.nivel(), 0.01);
    }

    // T6 - limite: a confiança tem teto em 1
    @Test
    void deveLimitarAConfiancaAUmComTresUcsDePesoUm() {
        List<EvidenciaAcademica> tresUcs = List.of(
                evidencia("UC A", 12, 1.0), evidencia("UC B", 14, 1.0), evidencia("UC C", 16, 1.0));

        assertEquals(1.0, calculadora.calcular("PROG", tresUcs).confianca(), 0.0001);
    }

    @Test
    void naoDeveUltrapassarConfiancaUmComQuatroUcs() {
        List<EvidenciaAcademica> quatroUcs = List.of(
                evidencia("UC A", 12, 1.0), evidencia("UC B", 14, 1.0),
                evidencia("UC C", 16, 1.0), evidencia("UC D", 18, 1.0));

        assertEquals(1.0, calculadora.calcular("PROG", quatroUcs).confianca(), 0.0001);
    }

    // T7 - repetição: a reprovação anterior não conta
    @Test
    void deveContarSoAAprovacaoQuandoAUcFoiRepetida() {
        List<EvidenciaAcademica> evidencias = List.of(evidencia("UC A", 8, 1.0), evidencia("UC A", 12, 1.0));

        ResultadoAcademico resultado = calculadora.calcular("PROG", evidencias);

        assertEquals(60.0, resultado.nivel(), 0.01);
        assertEquals(1, resultado.evidenciasUsadas().size());
    }

    // T8 - melhoria de nota: conta a melhor
    @Test
    void deveContarAMelhorNotaQuandoHaMelhoriaDeNota() {
        List<EvidenciaAcademica> evidencias = List.of(evidencia("UC A", 12, 1.0), evidencia("UC A", 15, 1.0));

        ResultadoAcademico resultado = calculadora.calcular("PROG", evidencias);

        assertEquals(75.0, resultado.nivel(), 0.01);
        assertEquals(0.333, resultado.confianca(), 0.01);
    }

    // T9 - inválidos
    @Test
    void deveRejeitarNotaAcimaDeVinte() {
        assertThrows(IllegalArgumentException.class,
                () -> calculadora.calcular("PROG", List.of(evidencia("UC A", 21, 1.0))));
    }

    @Test
    void deveRejeitarNotaNegativa() {
        assertThrows(IllegalArgumentException.class,
                () -> calculadora.calcular("PROG", List.of(evidencia("UC A", -1, 1.0))));
    }

    // T10 - inválidos
    @Test
    void deveRejeitarPesoAcimaDeUm() {
        assertThrows(IllegalArgumentException.class,
                () -> calculadora.calcular("PROG", List.of(evidencia("UC A", 15, 1.5))));
    }

    @Test
    void deveRejeitarPesoNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> calculadora.calcular("PROG", List.of(evidencia("UC A", 15, -0.2))));
    }

    // T11 - limite: peso zero não contribui nem divide por zero
    @Test
    void deveIgnorarEvidenciaComPesoZero() {
        ResultadoAcademico semOutras = calculadora.calcular("PROG", List.of(evidencia("UC A", 18, 0.0)));
        assertNull(semOutras.nivel());

        ResultadoAcademico comOutra = calculadora.calcular("PROG",
                List.of(evidencia("UC A", 18, 0.0), evidencia("UC B", 14, 1.0)));
        assertEquals(70.0, comOutra.nivel(), 0.01);
    }

    @Test
    void deveIgnorarEvidenciasDeOutraCompetencia() {
        EvidenciaAcademica deBd = new EvidenciaAcademica("Bases de Dados", "BD", 16, 1.0);

        ResultadoAcademico resultado = calculadora.calcular("PROG", List.of(deBd));

        assertNull(resultado.nivel());
    }
}
