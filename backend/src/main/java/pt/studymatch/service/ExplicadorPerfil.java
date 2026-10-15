package pt.studymatch.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import pt.studymatch.model.perfil.EvidenciaAcademica;
import pt.studymatch.model.perfil.ItemExplicacao;
import pt.studymatch.model.perfil.ResultadoAcademico;

/**
 * Funcionalidade própria: explica de onde vem o valor de uma competência.
 * Lista exatamente as evidências que o cálculo usou (mesmas notas, mesmos pesos).
 */
@Component
public class ExplicadorPerfil {

    public static final String TIPO_NOTA = "NOTA";
    public static final String TIPO_AUTO_DECLARACAO = "AUTO_DECLARACAO";
    public static final String FONTE_QUESTIONARIO = "Questionário";

    /**
     * @param academico           resultado da parte académica (traz as evidências usadas)
     * @param autoDeclaracao      nível de 1 a 5 declarado pelo estudante, ou null se não respondeu
     * @param pesoAutoDeclaracao  peso que a auto-declaração teve no cálculo
     */
    public List<ItemExplicacao> explicar(ResultadoAcademico academico, Integer autoDeclaracao,
                                         double pesoAutoDeclaracao) {
        List<ItemExplicacao> itens = new ArrayList<>();

        for (EvidenciaAcademica evidencia : academico.evidenciasUsadas()) {
            itens.add(new ItemExplicacao(TIPO_NOTA, evidencia.unidadeCurricular(),
                    evidencia.nota(), null, evidencia.peso()));
        }

        if (autoDeclaracao != null) {
            itens.add(new ItemExplicacao(TIPO_AUTO_DECLARACAO, FONTE_QUESTIONARIO,
                    null, autoDeclaracao, pesoAutoDeclaracao));
        }

        return itens;
    }
}
