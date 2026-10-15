package pt.studymatch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pt.studymatch.model.UnidadeCurricular;

public interface UnidadeCurricularRepository extends JpaRepository<UnidadeCurricular, Long> {

    Optional<UnidadeCurricular> findByNome(String nome);

    List<UnidadeCurricular> findAllByOrderByNomeAsc();
}
