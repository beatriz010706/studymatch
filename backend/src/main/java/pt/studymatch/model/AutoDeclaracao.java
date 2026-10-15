package pt.studymatch.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Resposta do estudante ao questionário de cold start:
 * o nível que acha que tem numa competência (1 a 5).
 */
@Entity
@Table(name = "auto_declaracoes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"estudante_id", "competencia_id"}))
public class AutoDeclaracao {

    public static final int NIVEL_MINIMO = 1;
    public static final int NIVEL_MAXIMO = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "estudante_id")
    private Estudante estudante;

    @ManyToOne(optional = false)
    @JoinColumn(name = "competencia_id")
    private Competencia competencia;

    @Column(nullable = false)
    private int nivel;

    // Construtor sem argumentos exigido pelo JPA
    protected AutoDeclaracao() {
    }

    public AutoDeclaracao(Estudante estudante, Competencia competencia, int nivel) {
        validarNivel(nivel);
        this.estudante = estudante;
        this.competencia = competencia;
        this.nivel = nivel;
    }

    /**
     * Atualiza a resposta quando o estudante volta a preencher o questionário.
     */
    public void alterarNivel(int novoNivel) {
        validarNivel(novoNivel);
        this.nivel = novoNivel;
    }

    private static void validarNivel(int nivel) {
        // Regra de domínio: o nível declarado vai de 1 a 5
        if (nivel < NIVEL_MINIMO || nivel > NIVEL_MAXIMO) {
            throw new IllegalArgumentException("O nível deve estar entre 1 e 5");
        }
    }

    public Long getId() {
        return id;
    }

    public Estudante getEstudante() {
        return estudante;
    }

    public Competencia getCompetencia() {
        return competencia;
    }

    public int getNivel() {
        return nivel;
    }
}
