package pt.studymatch.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Estudante do StudyMatch.
 * Versão mínima para o esqueleto técnico; vai crescer com o modelo de domínio
 * (curso, percurso académico, perfil, etc.).
 */
@Entity
@Table(name = "estudantes")
public class Estudante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroAluno;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String curso;

    @Column(nullable = false)
    private int anoCurricular;

    // Construtor sem argumentos exigido pelo JPA
    protected Estudante() {
    }

    public Estudante(String numeroAluno, String nome, String curso, int anoCurricular) {
        this.numeroAluno = numeroAluno;
        this.nome = nome;
        this.curso = curso;
        this.anoCurricular = anoCurricular;
    }

    /**
     * Atualiza os dados do estudante (usado no PUT /api/estudantes/{id}).
     */
    public void atualizarDados(String numeroAluno, String nome, String curso, int anoCurricular) {
        this.numeroAluno = numeroAluno;
        this.nome = nome;
        this.curso = curso;
        this.anoCurricular = anoCurricular;
    }

    public Long getId() {
        return id;
    }

    public String getNumeroAluno() {
        return numeroAluno;
    }

    public String getNome() {
        return nome;
    }

    public String getCurso() {
        return curso;
    }

    public int getAnoCurricular() {
        return anoCurricular;
    }
}
