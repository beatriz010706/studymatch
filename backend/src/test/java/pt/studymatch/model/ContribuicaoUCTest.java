package pt.studymatch.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ContribuicaoUCTest {

    @Test
    void deveAceitarPesosNosLimites() {
        assertEquals(0.0, new ContribuicaoUC(null, null, 0.0).getPeso());
        assertEquals(1.0, new ContribuicaoUC(null, null, 1.0).getPeso());
    }

    @Test
    void deveRejeitarPesoAcimaDeUm() {
        assertThrows(IllegalArgumentException.class, () -> new ContribuicaoUC(null, null, 1.5));
    }

    @Test
    void deveRejeitarPesoNegativo() {
        assertThrows(IllegalArgumentException.class, () -> new ContribuicaoUC(null, null, -0.1));
    }
}
