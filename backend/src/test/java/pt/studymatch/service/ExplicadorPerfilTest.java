package pt.studymatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import pt.studymatch.model.perfil.EvidenciaAcademica;
import pt.studymatch.model.perfil.ItemExplicacao;
import pt.studymatch.model.perfil.ResultadoAcademico;

class ExplicadorPerfilTest {

    private final CalculadoraAcademica calculadora = new CalculadoraAcademica();
    private final ExplicadorPerfil explicador = new ExplicadorPerfil();

    @Test
    void deveListarAsNotasUsadasNoCalculo() {
        ResultadoAcademico academico = calculadora.calcular("PROG", List.of(
                new EvidenciaAcademica("Algoritmos e Estruturas de Dados", "PROG", 14, 0.5),
                new EvidenciaAcademica("Laboratório de Tecnologias Web", "PROG", 18, 0.3)));

        List<ItemExplicacao> itens = explicador.explicar(academico, null, 0);

        assertEquals(2, itens.size());
        assertEquals("NOTA", itens.get(0).tipo());
        assertEquals("Algoritmos e Estruturas de Dados", itens.get(0).fonte());
        assertEquals(14.0, itens.get(0).nota(), 0.0001);
        assertEquals(0.5, itens.get(0).peso(), 0.0001);
    }

    @Test
    void deveIncluirAAutoDeclaracaoQuandoExiste() {
        ResultadoAcademico academico = calculadora.calcular("BD", List.of());

        List<ItemExplicacao> itens = explicador.explicar(academico, 4, 0.4);

        assertEquals(1, itens.size());
        assertEquals("AUTO_DECLARACAO", itens.get(0).tipo());
        assertEquals(4, itens.get(0).nivel());
        assertEquals(0.4, itens.get(0).peso(), 0.0001);
    }

    @Test
    void deveDevolverListaVaziaQuandoNaoHaDados() {
        ResultadoAcademico academico = calculadora.calcular("ES", List.of());

        assertTrue(explicador.explicar(academico, null, 0).isEmpty());
    }

    // A explicação tem de reproduzir exatamente o nível calculado
    @Test
    void asNotasExplicadasReproduzemOMesmoNivelDoCalculo() {
        ResultadoAcademico academico = calculadora.calcular("PROG", List.of(
                new EvidenciaAcademica("UC A", "PROG", 14, 0.5),
                new EvidenciaAcademica("UC B", "PROG", 18, 0.3)));

        double somaPontos = 0;
        double somaPesos = 0;
        for (ItemExplicacao item : explicador.explicar(academico, null, 0)) {
            somaPontos = somaPontos + item.peso() * (item.nota() / 20 * 100);
            somaPesos = somaPesos + item.peso();
        }

        assertEquals(academico.nivel(), somaPontos / somaPesos, 0.0001);
    }
}
