package pt.studymatch.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pt.studymatch.model.Estudante;

public interface EstudanteRepository extends JpaRepository<Estudante, Long> {

    boolean existsByNumeroAluno(String numeroAluno);

    Optional<Estudante> findByNumeroAluno(String numeroAluno);
}
