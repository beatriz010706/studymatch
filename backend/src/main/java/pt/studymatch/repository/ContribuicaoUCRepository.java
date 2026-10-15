package pt.studymatch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pt.studymatch.model.Competencia;
import pt.studymatch.model.ContribuicaoUC;
import pt.studymatch.model.UnidadeCurricular;

public interface ContribuicaoUCRepository extends JpaRepository<ContribuicaoUC, Long> {

    List<ContribuicaoUC> findByUnidadeCurricular(UnidadeCurricular unidadeCurricular);

    List<ContribuicaoUC> findByCompetencia(Competencia competencia);
}
