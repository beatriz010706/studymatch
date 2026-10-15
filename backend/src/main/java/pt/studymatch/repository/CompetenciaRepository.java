package pt.studymatch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pt.studymatch.model.Competencia;

public interface CompetenciaRepository extends JpaRepository<Competencia, Long> {

    Optional<Competencia> findByCodigo(String codigo);

    // Mantém sempre a mesma ordem das competências (a da inserção)
    List<Competencia> findAllByOrderByIdAsc();
}
