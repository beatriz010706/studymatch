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
 * Liga uma unidade curricular a uma competência, com um peso entre 0 e 1.
 * É a forma como as notas de uma UC se transformam em competências.
 */
@Entity
@Table(name = "contribuicoes_uc",
        uniqueConstraints = @UniqueConstraint(columnNames = {"unidade_curricular_id", "competencia_id"}))
public class ContribuicaoUC {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "unidade_curricular_id")
    private UnidadeCurricular unidadeCurricular;

    @ManyToOne(optional = false)
    @JoinColumn(name = "competencia_id")
    private Competencia competencia;

    @Column(nullable = false)
    private double peso;

    // Construtor sem argumentos exigido pelo JPA
    protected ContribuicaoUC() {
    }

    public ContribuicaoUC(UnidadeCurricular unidadeCurricular, Competencia competencia, double peso) {
        // Regra de domínio: o peso está entre 0 e 1
        if (peso < 0 || peso > 1) {
            throw new IllegalArgumentException("O peso da contribuição deve estar entre 0 e 1");
        }
        this.unidadeCurricular = unidadeCurricular;
        this.competencia = competencia;
        this.peso = peso;
    }

    public Long getId() {
        return id;
    }

    public UnidadeCurricular getUnidadeCurricular() {
        return unidadeCurricular;
    }

    public Competencia getCompetencia() {
        return competencia;
    }

    public double getPeso() {
        return peso;
    }
}
