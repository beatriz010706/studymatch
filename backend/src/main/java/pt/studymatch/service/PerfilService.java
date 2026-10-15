package pt.studymatch.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.studymatch.dto.CompetenciaPerfilResponse;
import pt.studymatch.dto.PerfilResponse;
import pt.studymatch.model.AutoDeclaracao;
import pt.studymatch.model.Competencia;
import pt.studymatch.model.ContribuicaoUC;
import pt.studymatch.model.Inscricao;
import pt.studymatch.model.perfil.EvidenciaAcademica;
import pt.studymatch.model.perfil.ItemExplicacao;
import pt.studymatch.model.perfil.NivelCompetencia;
import pt.studymatch.model.perfil.ResultadoAcademico;
import pt.studymatch.repository.AutoDeclaracaoRepository;
import pt.studymatch.repository.CompetenciaRepository;
import pt.studymatch.repository.ContribuicaoUCRepository;
import pt.studymatch.repository.EstudanteRepository;
import pt.studymatch.repository.InscricaoRepository;

/**
 * Orquestra o cálculo do perfil de um estudante.
 *
 * O perfil NÃO é guardado na base de dados: é calculado a cada pedido a partir das notas e da
 * auto-declaração, por isso reflete sempre os dados atuais.
 *
 * Passos para cada competência:
 *   1. reunir as evidências (notas das UCs que contribuem para a competência);
 *   2. CalculadoraAcademica -> nível e confiança académicos;
 *   3. CombinadorColdStart -> junta a auto-declaração, se existir;
 *   4. ExplicadorPerfil -> lista as evidências usadas.
 */
@Service
public class PerfilService {

    private final EstudanteRepository estudanteRepository;
    private final InscricaoRepository inscricaoRepository;
    private final ContribuicaoUCRepository contribuicaoUCRepository;
    private final CompetenciaRepository competenciaRepository;
    private final AutoDeclaracaoRepository autoDeclaracaoRepository;
    private final CalculadoraAcademica calculadoraAcademica;
    private final CombinadorColdStart combinadorColdStart;
    private final ExplicadorPerfil explicadorPerfil;

    public PerfilService(EstudanteRepository estudanteRepository,
                         InscricaoRepository inscricaoRepository,
                         ContribuicaoUCRepository contribuicaoUCRepository,
                         CompetenciaRepository competenciaRepository,
                         AutoDeclaracaoRepository autoDeclaracaoRepository,
                         CalculadoraAcademica calculadoraAcademica,
                         CombinadorColdStart combinadorColdStart,
                         ExplicadorPerfil explicadorPerfil) {
        this.estudanteRepository = estudanteRepository;
        this.inscricaoRepository = inscricaoRepository;
        this.contribuicaoUCRepository = contribuicaoUCRepository;
        this.competenciaRepository = competenciaRepository;
        this.autoDeclaracaoRepository = autoDeclaracaoRepository;
        this.calculadoraAcademica = calculadoraAcademica;
        this.combinadorColdStart = combinadorColdStart;
        this.explicadorPerfil = explicadorPerfil;
    }

    @Transactional(readOnly = true)
    public PerfilResponse obterPerfil(Long estudanteId) {
        if (!estudanteRepository.existsById(estudanteId)) {
            throw new RecursoNaoEncontradoException("Estudante não encontrado: " + estudanteId);
        }

        List<Inscricao> inscricoes = inscricaoRepository.findByEstudanteId(estudanteId);
        List<ContribuicaoUC> contribuicoes = contribuicaoUCRepository.findAll();
        List<Competencia> competencias = competenciaRepository.findAllByOrderByIdAsc();

        Map<String, Integer> autoDeclaracoes = new HashMap<>();
        for (AutoDeclaracao autoDeclaracao : autoDeclaracaoRepository.findByEstudanteId(estudanteId)) {
            autoDeclaracoes.put(autoDeclaracao.getCompetencia().getCodigo(), autoDeclaracao.getNivel());
        }

        return montarPerfil(estudanteId, inscricoes, contribuicoes, competencias, autoDeclaracoes);
    }

    /**
     * Cálculo puro (sem base de dados), fácil de testar.
     *
     * @param autoDeclaracoes nível de 1 a 5 por código de competência
     */
    public PerfilResponse montarPerfil(Long estudanteId, List<Inscricao> inscricoes,
                                       List<ContribuicaoUC> contribuicoes, List<Competencia> competencias,
                                       Map<String, Integer> autoDeclaracoes) {
        List<CompetenciaPerfilResponse> resultado = new ArrayList<>();
        double somaConfiancasAcademicas = 0;

        for (Competencia competencia : competencias) {
            String codigo = competencia.getCodigo();

            List<EvidenciaAcademica> evidencias = reunirEvidencias(codigo, inscricoes, contribuicoes);
            ResultadoAcademico academico = calculadoraAcademica.calcular(codigo, evidencias);

            Integer autoDeclaracao = autoDeclaracoes.get(codigo);
            NivelCompetencia nivelCompetencia = combinadorColdStart.combinar(academico, autoDeclaracao);

            double pesoAuto = 0;
            if (autoDeclaracao != null) {
                pesoAuto = arredondar(combinadorColdStart.pesoAutoDeclaracao(academico.confianca()), 2);
            }
            List<ItemExplicacao> explicacao = explicadorPerfil.explicar(academico, autoDeclaracao, pesoAuto);

            Double nivel = null;
            if (nivelCompetencia.nivel() != null) {
                nivel = arredondar(nivelCompetencia.nivel(), 1);
            }

            resultado.add(new CompetenciaPerfilResponse(codigo, competencia.getNome(), nivel,
                    arredondar(nivelCompetencia.confianca(), 2), nivelCompetencia.origem(), explicacao));

            somaConfiancasAcademicas = somaConfiancasAcademicas + academico.confianca();
        }

        // Maturidade = média das confianças académicas (0 em cold start puro)
        double maturidade = 0;
        if (!competencias.isEmpty()) {
            maturidade = arredondar(somaConfiancasAcademicas / competencias.size(), 2);
        }

        return new PerfilResponse(estudanteId, maturidade, resultado);
    }

    // Notas das UCs que contribuem para a competência, com o peso da contribuição
    private List<EvidenciaAcademica> reunirEvidencias(String codigoCompetencia, List<Inscricao> inscricoes,
                                                      List<ContribuicaoUC> contribuicoes) {
        List<EvidenciaAcademica> evidencias = new ArrayList<>();

        for (Inscricao inscricao : inscricoes) {
            if (inscricao.getNota() == null) {
                continue; // ainda sem resultado: não contribui
            }
            String nomeUc = inscricao.getUnidadeCurricular().getNome();

            for (ContribuicaoUC contribuicao : contribuicoes) {
                boolean mesmaUc = contribuicao.getUnidadeCurricular().getNome().equals(nomeUc);
                boolean mesmaCompetencia = contribuicao.getCompetencia().getCodigo().equals(codigoCompetencia);

                if (mesmaUc && mesmaCompetencia) {
                    evidencias.add(new EvidenciaAcademica(nomeUc, codigoCompetencia,
                            inscricao.getNota(), contribuicao.getPeso()));
                }
            }
        }
        return evidencias;
    }

    private static double arredondar(double valor, int casasDecimais) {
        double fator = Math.pow(10, casasDecimais);
        return Math.round(valor * fator) / fator;
    }
}
