package pt.studymatch.service;

import org.springframework.stereotype.Component;

import pt.studymatch.model.perfil.NivelCompetencia;
import pt.studymatch.model.perfil.OrigemEvidencia;
import pt.studymatch.model.perfil.ResultadoAcademico;

/**
 * Estratégia de COLD START: combina a evidência académica com a auto-declaração.
 *
 * Regras (ver especificação, secção 4.2):
 *  - a auto-declaração (1 a 5) vale nível x 20;
 *  - tem confiança fixa de 0,4 quando é a única fonte;
 *  - o seu peso é 0,4 x (1 - confiança académica), por isso desce à medida que há notas
 *    e chega a zero quando a confiança académica é 1;
 *  - quando existem as duas fontes, o nível é a média pesada das duas.
 *
 * Esta classe não usa a base de dados, por isso é simples de testar.
 */
@Component
public class CombinadorColdStart {

    public static final int AUTO_NIVEL_MINIMO = 1;
    public static final int AUTO_NIVEL_MAXIMO = 5;
    public static final double AUTO_ESCALA = 20;
    public static final double CONFIANCA_AUTO_DECLARACAO = 0.4;

    /**
     * @param academico       resultado da parte académica da competência
     * @param autoDeclaracao  nível de 1 a 5, ou null se o estudante não respondeu
     */
    public NivelCompetencia combinar(ResultadoAcademico academico, Integer autoDeclaracao) {
        if (autoDeclaracao != null) {
            validarAutoDeclaracao(autoDeclaracao);
        }

        String codigo = academico.codigoCompetencia();
        boolean temAcademico = academico.nivel() != null;
        boolean temAutoDeclaracao = autoDeclaracao != null;

        // Sem nenhuma informação: competência desconhecida
        if (!temAcademico && !temAutoDeclaracao) {
            return new NivelCompetencia(codigo, null, 0.0, OrigemEvidencia.DESCONHECIDA);
        }

        // Só evidência académica
        if (!temAutoDeclaracao) {
            return new NivelCompetencia(codigo, academico.nivel(), academico.confianca(),
                    OrigemEvidencia.ACADEMICA);
        }

        double nivelAuto = autoDeclaracao * AUTO_ESCALA;

        // Só auto-declaração (cold start puro)
        if (!temAcademico) {
            return new NivelCompetencia(codigo, nivelAuto, CONFIANCA_AUTO_DECLARACAO,
                    OrigemEvidencia.AUTO_DECLARADA);
        }

        // As duas fontes
        double pesoAcademico = academico.confianca();
        double pesoAuto = pesoAutoDeclaracao(pesoAcademico);

        // Com confiança académica máxima, a auto-declaração já não conta
        if (pesoAuto <= 0) {
            return new NivelCompetencia(codigo, academico.nivel(), academico.confianca(),
                    OrigemEvidencia.ACADEMICA);
        }

        double nivelFinal = (pesoAcademico * academico.nivel() + pesoAuto * nivelAuto)
                / (pesoAcademico + pesoAuto);
        double confiancaFinal = pesoAcademico + pesoAuto;

        return new NivelCompetencia(codigo, nivelFinal, confiancaFinal, OrigemEvidencia.MISTA);
    }

    /**
     * Peso da auto-declaração: 0,4 x (1 - confiança académica).
     * Desce quando a confiança académica sobe, e é 0 quando esta chega a 1.
     */
    public double pesoAutoDeclaracao(double confiancaAcademica) {
        if (confiancaAcademica < 0 || confiancaAcademica > 1) {
            throw new IllegalArgumentException("A confiança académica deve estar entre 0 e 1");
        }
        return CONFIANCA_AUTO_DECLARACAO * (1 - confiancaAcademica);
    }

    private void validarAutoDeclaracao(int autoDeclaracao) {
        if (autoDeclaracao < AUTO_NIVEL_MINIMO || autoDeclaracao > AUTO_NIVEL_MAXIMO) {
            throw new IllegalArgumentException("A auto-declaração deve estar entre 1 e 5");
        }
    }
}
