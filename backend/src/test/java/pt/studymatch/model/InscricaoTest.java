package pt.studymatch.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class InscricaoTest {

    @Test
    void deveAceitarNotaValida() {
        Inscricao inscricao = new Inscricao(null, null, "2025/2026", 16.0);

        assertEquals(16.0, inscricao.getNota());
    }

    @Test
    void deveAceitarInscricaoSemNota() {
        Inscricao inscricao = new Inscricao(null, null, "2025/2026", null);

        assertNull(inscricao.getNota());
    }

    @Test
    void deveAceitarNotasNosLimites() {
        assertEquals(0.0, new Inscricao(null, null, "2025/2026", 0.0).getNota());
        assertEquals(20.0, new Inscricao(null, null, "2025/2026", 20.0).getNota());
    }

    @Test
    void deveRejeitarNotaAcimaDoMaximo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Inscricao(null, null, "2025/2026", 21.0));
    }

    @Test
    void deveRejeitarNotaNegativa() {
        assertThrows(IllegalArgumentException.class,
                () -> new Inscricao(null, null, "2025/2026", -1.0));
    }

    @Test
    void deveRejeitarAlteracaoParaNotaInvalida() {
        Inscricao inscricao = new Inscricao(null, null, "2025/2026", 12.0);

        assertThrows(IllegalArgumentException.class, () -> inscricao.definirNota(25.0));
        assertEquals(12.0, inscricao.getNota());
    }

    @Test
    void devePermitirCorrigirANota() {
        Inscricao inscricao = new Inscricao(null, null, "2025/2026", 12.0);

        inscricao.definirNota(15.0);

        assertEquals(15.0, inscricao.getNota());
    }
}
