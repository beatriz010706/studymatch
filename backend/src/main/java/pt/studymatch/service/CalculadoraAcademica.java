package pt.studymatch.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import pt.studymatch.model.perfil.EvidenciaAcademica;
import pt.studymatch.model.perfil.ResultadoAcademico;

/**
 * Calcula a parte ACADÉMICA de uma competência: nível e confiança, a partir das notas.
 *
 * Regras (ver especificação, secção 4.1):
 *  - só contam notas maiores ou iguais a 10 (aprovação);
 *  - por unidade curricular conta apenas a MELHOR nota aprovada;
 *  - nível = média ponderada de (nota / 20 x 100), usando o peso da contribuição;
 *  - confiança = min(1, soma dos pesos / 3).
 *
 * Esta classe não usa a base de dados, por isso é simples de testar.
 */
@Component
public class CalculadoraAcademica {

    public static final double NOTA_MINIMA = 0;
    public static final double NOTA_MAXIMA = 20;
    public static final double NOTA_APROVACAO = 10;
    public static final double PESO_MINIMO = 0;
    public static final double PESO_MAXIMO = 1;

    // Soma de pesos necessária para ter confiança máxima (ex.: 3 UCs com peso 1)
    public static final double SOMA_PESOS_CONFIANCA_MAXIMA = 3;

    public ResultadoAcademico calcular(String codigoCompetencia, List<EvidenciaAcademica> evidencias) {
        if (evidencias == null) {
            evidencias = List.of();
        }

        validar(evidencias);

        // Guarda, para cada UC, a melhor nota aprovada. LinkedHashMap mantém a ordem de chegada.
        Map<String, EvidenciaAcademica> melhorPorUc = new LinkedHashMap<>();
        for (EvidenciaAcademica evidencia : evidencias) {
            if (!evidencia.codigoCompetencia().equals(codigoCompetencia)) {
                continue; // evidência de outra competência
            }
            if (evidencia.nota() < NOTA_APROVACAO) {
                continue; // reprovada: não conta
            }
            if (evidencia.peso() <= 0) {
                continue; // peso zero: não contribui (e evita dividir por zero)
            }

            EvidenciaAcademica atual = melhorPorUc.get(evidencia.unidadeCurricular());
            if (atual == null || evidencia.nota() > atual.nota()) {
                melhorPorUc.put(evidencia.unidadeCurricular(), evidencia);
            }
        }

        if (melhorPorUc.isEmpty()) {
            return new ResultadoAcademico(codigoCompetencia, null, 0.0, List.of());
        }

        double somaPontos = 0;
        double somaPesos = 0;
        for (EvidenciaAcademica evidencia : melhorPorUc.values()) {
            double notaEmPercentagem = evidencia.nota() / NOTA_MAXIMA * 100;
            somaPontos = somaPontos + evidencia.peso() * notaEmPercentagem;
            somaPesos = somaPesos + evidencia.peso();
        }

        double nivel = somaPontos / somaPesos;
        double confianca = Math.min(1.0, somaPesos / SOMA_PESOS_CONFIANCA_MAXIMA);

        return new ResultadoAcademico(codigoCompetencia, nivel, confianca,
                List.copyOf(melhorPorUc.values()));
    }

    private void validar(List<EvidenciaAcademica> evidencias) {
        for (EvidenciaAcademica evidencia : evidencias) {
            if (evidencia.nota() < NOTA_MINIMA || evidencia.nota() > NOTA_MAXIMA) {
                throw new IllegalArgumentException("A nota deve estar entre 0 e 20: " + evidencia.nota());
            }
            if (evidencia.peso() < PESO_MINIMO || evidencia.peso() > PESO_MAXIMO) {
                throw new IllegalArgumentException("O peso deve estar entre 0 e 1: " + evidencia.peso());
            }
        }
    }
}
