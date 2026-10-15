package pt.studymatch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pt.studymatch.model.AutoDeclaracao;

public interface AutoDeclaracaoRepository extends JpaRepository<AutoDeclaracao, Long> {

    List<AutoDeclaracao> findByEstudanteId(Long estudanteId);

    Optional<AutoDeclaracao> findByEstudanteIdAndCompetenciaId(Long estudanteId, Long competenciaId);
}
