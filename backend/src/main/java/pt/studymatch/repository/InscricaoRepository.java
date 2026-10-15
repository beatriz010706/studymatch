package pt.studymatch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pt.studymatch.model.Inscricao;

public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {

    List<Inscricao> findByEstudanteId(Long estudanteId);

    // Garante que a inscrição pertence mesmo ao estudante indicado
    Optional<Inscricao> findByIdAndEstudanteId(Long id, Long estudanteId);

    boolean existsByEstudanteIdAndUnidadeCurricularIdAndAnoLetivo(
            Long estudanteId, Long unidadeCurricularId, String anoLetivo);
}
