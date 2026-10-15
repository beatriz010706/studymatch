package pt.studymatch.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import pt.studymatch.model.Competencia;
import pt.studymatch.model.ContribuicaoUC;
import pt.studymatch.model.UnidadeCurricular;
import pt.studymatch.repository.CompetenciaRepository;
import pt.studymatch.repository.ContribuicaoUCRepository;
import pt.studymatch.repository.UnidadeCurricularRepository;

/**
 * Insere as competências, unidades curriculares e contribuições iniciais.
 * Só insere quando a tabela está vazia, por isso arrancar várias vezes não duplica nada.
 */
@Component
public class DadosIniciais implements CommandLineRunner {

    private final CompetenciaRepository competenciaRepository;
    private final UnidadeCurricularRepository unidadeCurricularRepository;
    private final ContribuicaoUCRepository contribuicaoUCRepository;

    public DadosIniciais(CompetenciaRepository competenciaRepository,
                         UnidadeCurricularRepository unidadeCurricularRepository,
                         ContribuicaoUCRepository contribuicaoUCRepository) {
        this.competenciaRepository = competenciaRepository;
        this.unidadeCurricularRepository = unidadeCurricularRepository;
        this.contribuicaoUCRepository = contribuicaoUCRepository;
    }

    @Override
    public void run(String... args) {
        if (competenciaRepository.count() == 0) {
            criarCompetencias();
        }
        if (unidadeCurricularRepository.count() == 0) {
            criarUnidadesCurriculares();
        }
        if (contribuicaoUCRepository.count() == 0) {
            criarContribuicoes();
        }
    }

    private void criarCompetencias() {
        competenciaRepository.save(new Competencia("PROG", "Programação"));
        competenciaRepository.save(new Competencia("BD", "Bases de Dados"));
        competenciaRepository.save(new Competencia("WEB", "Desenvolvimento Web"));
        competenciaRepository.save(new Competencia("ALG", "Algoritmos e Estruturas de Dados"));
        competenciaRepository.save(new Competencia("ES", "Engenharia de Software"));
    }

    private void criarUnidadesCurriculares() {
        // nome, ECTS, ano curricular, semestre
        unidadeCurricularRepository.save(new UnidadeCurricular("Algoritmos e Estruturas de Dados", 6, 1, 2));
        unidadeCurricularRepository.save(new UnidadeCurricular("Bases de Dados", 6, 2, 1));
        unidadeCurricularRepository.save(new UnidadeCurricular("Laboratório de Tecnologias Web", 6, 2, 1));
        unidadeCurricularRepository.save(new UnidadeCurricular("Engenharia de Software", 6, 2, 2));
        unidadeCurricularRepository.save(new UnidadeCurricular("Compiladores", 6, 2, 2));
    }

    private void criarContribuicoes() {
        contribuir("Bases de Dados", "BD", 1.0);

        contribuir("Laboratório de Tecnologias Web", "WEB", 1.0);
        contribuir("Laboratório de Tecnologias Web", "PROG", 0.3);

        contribuir("Engenharia de Software", "ES", 1.0);
        contribuir("Engenharia de Software", "PROG", 0.3);

        contribuir("Algoritmos e Estruturas de Dados", "ALG", 1.0);
        contribuir("Algoritmos e Estruturas de Dados", "PROG", 0.5);

        contribuir("Compiladores", "PROG", 0.5);
        contribuir("Compiladores", "ALG", 0.4);
    }

    private void contribuir(String nomeUc, String codigoCompetencia, double peso) {
        UnidadeCurricular unidadeCurricular = unidadeCurricularRepository.findByNome(nomeUc)
                .orElseThrow(() -> new IllegalStateException("UC em falta nos dados iniciais: " + nomeUc));
        Competencia competencia = competenciaRepository.findByCodigo(codigoCompetencia)
                .orElseThrow(() -> new IllegalStateException(
                        "Competência em falta nos dados iniciais: " + codigoCompetencia));

        contribuicaoUCRepository.save(new ContribuicaoUC(unidadeCurricular, competencia, peso));
    }
}
