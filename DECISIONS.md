# Decisões Técnicas e de Design — StudyMatch

Este documento regista as decisões importantes do projeto e a sua justificação. Será atualizado à medida que o produto evolui.

## 1. Stack tecnológica

| Camada | Escolha |
|---|---|
| Frontend | React |
| Backend | Java + Spring Boot (API REST) |
| Persistência | PostgreSQL, com Spring Data JPA |
| Build e dependências | Maven (com Maven Wrapper) |
| Testes | JUnit 5 |

### Justificação

| Critério | Justificação |
|---|---|
| **Adequação** | O produto exige um frontend, uma API, lógica de negócio e persistência relacional (estudantes, UCs, notas, competências). Spring Boot + React + base de dados relacional cobre exatamente esta arquitetura. |
| **Manutenibilidade** | Spring Boot impõe uma estrutura em camadas clara (controller, service, repository), o que facilita a separação de responsabilidades e a evolução do produto. React permite componentes reutilizáveis. |
| **Testabilidade** | Spring Boot integra-se nativamente com JUnit 5 e Mockito. A lógica de negócio, isolada na camada de serviço, é testável sem dependências externas. |
| **Integração** | Comunicação entre frontend e backend através de uma API REST em JSON, uma abordagem simples e amplamente documentada. Spring Data JPA abstrai a base de dados. |
| **Conhecimento da equipa** | A equipa já trabalha com Java e Maven na unidade curricular, e com o IntelliJ como ambiente de desenvolvimento, o que reduz a curva de aprendizagem. |
| **Tooling** | Maven e o Maven Wrapper garantem uma build reprodutível fora do IDE. PostgreSQL é gratuito e tem boas ferramentas de administração. |

### Alternativas consideradas

| Alternativa | Porque não foi escolhida |
|---|---|
| Node.js/Express no backend | Válida, mas obrigaria a duas linguagens e a menor reutilização do conhecimento de Java já existente na equipa. |
| MongoDB | Os dados do domínio são fortemente relacionais (estudante ↔ inscrição ↔ UC ↔ competência), o que favorece um modelo relacional. |
| Gradle | Equivalente ao Maven, mas a equipa já tem experiência com Maven. |
| Angular / Vue | Opções válidas; React foi escolhido pela abundância de documentação e exemplos. |

## 2. Arquitetura em camadas

```
Frontend (React) → API REST (Controller) → Lógica de negócio (Service/Domínio) → Persistência (Repository) → PostgreSQL
```

**Decisão:** arquitetura em camadas, com as regras de negócio concentradas nos serviços e no modelo de domínio.

**Justificação:** o enunciado indica que os requisitos vão evoluir em sprints futuros. Separar responsabilidades e manter baixo acoplamento permite alterar, por exemplo, a fórmula de matching sem tocar na API ou na persistência.

## 3. Estratégia de Cold Start

**Decisão:** abordagem híbrida, combinando duas fontes de informação:

1. **Auto-declaração:** questionário curto de onboarding, em que o estudante indica competências percebidas, interesses e disponibilidade.
2. **Contexto institucional:** dados já existentes no sistema (curso, ano curricular, unidades curriculares em que está inscrito).

**Justificação:**
- Resolve a ausência de histórico sem inventar dados nem enviesar o perfil.
- Aproveita informação que o sistema já tem, sem esforço adicional para o estudante.
- Combina duas fontes com características diferentes: uma subjetiva e uma objetiva.

**Evolução prevista:** o perfil inicial é marcado como de baixa maturidade. À medida que surgem resultados académicos reais, estes ganham peso no perfil e a influência da auto-declaração diminui.

**Limitação conhecida:** a auto-declaração depende do autoconhecimento e da honestidade do estudante.

## 4. Estratégia de Matching

**Decisão:** score ponderado com regras explícitas. Cada atributo relevante do perfil entra numa fórmula com um peso definido pela equipa, e a compatibilidade resulta dessa pontuação.

**Justificação:**
- É transparente: é possível explicar a um estudante ou docente porque dois estudantes foram juntos.
- É simples de implementar e de testar com testes unitários.
- Permite evoluir mais tarde (ajustar pesos, adicionar atributos, introduzir restrições como a disponibilidade).

**Âmbito atual:** a formação automática de grupos não faz parte dos primeiros sprints. Nesta fase, o foco é construir o perfil que servirá de base ao matching.

## 5. Convenções de trabalho

O workflow de GitHub, as convenções de código, a estrutura do projeto e a Definition of Done estão documentados em [`CONTRIBUTING.md`](CONTRIBUTING.md).
