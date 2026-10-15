package pt.studymatch.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AutoDeclaracaoTest {

    @Test
    void deveAceitarNiveisNosLimites() {
        assertEquals(1, new AutoDeclaracao(null, null, 1).getNivel());
        assertEquals(5, new AutoDeclaracao(null, null, 5).getNivel());
    }

    @Test
    void deveRejeitarNivelZero() {
        assertThrows(IllegalArgumentException.class, () -> new AutoDeclaracao(null, null, 0));
    }

    @Test
    void deveRejeitarNivelSeis() {
        assertThrows(IllegalArgumentException.class, () -> new AutoDeclaracao(null, null, 6));
    }

    @Test
    void devePermitirAtualizarORespondeDeNovoOQuestionario() {
        AutoDeclaracao autoDeclaracao = new AutoDeclaracao(null, null, 2);

        autoDeclaracao.alterarNivel(4);

        assertEquals(4, autoDeclaracao.getNivel());
    }
}
