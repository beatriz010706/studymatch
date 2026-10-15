package pt.studymatch.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import pt.studymatch.dto.EstudanteRequest;
import pt.studymatch.model.Estudante;
import pt.studymatch.repository.EstudanteRepository;

@Service
public class EstudanteService {

    private final EstudanteRepository estudanteRepository;

    public EstudanteService(EstudanteRepository estudanteRepository) {
        this.estudanteRepository = estudanteRepository;
    }

    public List<Estudante> listar() {
        return estudanteRepository.findAll();
    }

    public long contar() {
        return estudanteRepository.count();
    }

    public Estudante obter(Long id) {
        return estudanteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estudante não encontrado: " + id));
    }

    public Estudante criar(EstudanteRequest pedido) {
        // Regra de negócio: o número de aluno é único
        if (estudanteRepository.existsByNumeroAluno(pedido.numeroAluno())) {
            throw new NumeroAlunoDuplicadoException(pedido.numeroAluno());
        }

        Estudante novoEstudante = new Estudante(
                pedido.numeroAluno(),
                pedido.nome(),
                pedido.curso(),
                pedido.anoCurricular());

        return estudanteRepository.save(novoEstudante);
    }

    public Estudante atualizar(Long id, EstudanteRequest pedido) {
        Estudante estudante = obter(id);

        // Regra de negócio: o número de aluno não pode pertencer a outro estudante
        Optional<Estudante> comMesmoNumero = estudanteRepository.findByNumeroAluno(pedido.numeroAluno());
        if (comMesmoNumero.isPresent() && !id.equals(comMesmoNumero.get().getId())) {
            throw new NumeroAlunoDuplicadoException(pedido.numeroAluno());
        }

        estudante.atualizarDados(pedido.numeroAluno(), pedido.nome(), pedido.curso(), pedido.anoCurricular());
        return estudanteRepository.save(estudante);
    }
}
