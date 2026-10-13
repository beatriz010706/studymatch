package pt.studymatch.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import pt.studymatch.model.Estudante;

/**
 * Teste de persistência com base de dados em memória (H2),
 * por isso não precisa de PostgreSQL instalado.
 */
@DataJpaTest
class EstudanteRepositoryTest {

    @Autowired
    private EstudanteRepository estudanteRepository;

    @Test
    void deveGuardarEEncontrarEstudantePeloNumeroDeAluno() {
        estudanteRepository.save(new Estudante("51714", "Ana Silva", "Engenharia Informática", 2));

        Optional<Estudante> encontrado = estudanteRepository.findByNumeroAluno("51714");

        assertTrue(encontrado.isPresent());
        assertEquals("Ana Silva", encontrado.get().getNome());
    }
}
