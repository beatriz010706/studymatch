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
 * Inscrição de um estudante numa unidade curricular, num ano letivo.
 * A nota é opcional: só existe depois de a UC terminar.
 */
@Entity
@Table(name = "inscricoes",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"estudante_id", "unidade_curricular_id", "ano_letivo"}))
public class Inscricao {

    public static final double NOTA_MINIMA = 0;
    public static final double NOTA_MAXIMA = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "estudante_id")
    private Estudante estudante;

    @ManyToOne(optional = false)
    @JoinColumn(name = "unidade_curricular_id")
    private UnidadeCurricular unidadeCurricular;

    // Exemplo: "2025/2026"
    @Column(name = "ano_letivo", nullable = false)
    private String anoLetivo;

    // Fica a null enquanto a UC não tem resultado
    private Double nota;

    // Construtor sem argumentos exigido pelo JPA
    protected Inscricao() {
    }

    public Inscricao(Estudante estudante, UnidadeCurricular unidadeCurricular, String anoLetivo, Double nota) {
        validarNota(nota);
        this.estudante = estudante;
        this.unidadeCurricular = unidadeCurricular;
        this.anoLetivo = anoLetivo;
        this.nota = nota;
    }

    /**
     * Regista ou altera a nota (usado, por exemplo, para corrigir uma nota na demo).
     */
    public void definirNota(Double novaNota) {
        validarNota(novaNota);
        this.nota = novaNota;
    }

    private static void validarNota(Double nota) {
        // Regra de domínio: a nota, quando existe, está entre 0 e 20
        if (nota != null && (nota < NOTA_MINIMA || nota > NOTA_MAXIMA)) {
            throw new IllegalArgumentException("A nota deve estar entre 0 e 20");
        }
    }

    public Long getId() {
        return id;
    }

    public Estudante getEstudante() {
        return estudante;
    }

    public UnidadeCurricular getUnidadeCurricular() {
        return unidadeCurricular;
    }

    public String getAnoLetivo() {
        return anoLetivo;
    }

    public Double getNota() {
        return nota;
    }
}
