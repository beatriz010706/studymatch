package pt.studymatch.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Unidade curricular (disciplina) do curso.
 */
@Entity
@Table(name = "unidades_curriculares")
public class UnidadeCurricular {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private int ects;

    @Column(nullable = false)
    private int anoCurricular;

    @Column(nullable = false)
    private int semestre;

    // Construtor sem argumentos exigido pelo JPA
    protected UnidadeCurricular() {
    }

    public UnidadeCurricular(String nome, int ects, int anoCurricular, int semestre) {
        this.nome = nome;
        this.ects = ects;
        this.anoCurricular = anoCurricular;
        this.semestre = semestre;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public int getEcts() {
        return ects;
    }

    public int getAnoCurricular() {
        return anoCurricular;
    }

    public int getSemestre() {
        return semestre;
    }
}
