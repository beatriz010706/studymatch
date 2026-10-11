# Guia de Contribuição — StudyMatch

Este documento define como a equipa trabalha no repositório: fluxo de desenvolvimento, branches, Pull Requests, convenções de código e Definition of Done.

## 1. Fluxo de desenvolvimento

```
Issue → Branch → Implementação → Pull Request → Review → Merge
```

- Todo o trabalho relevante tem de estar associado a uma **Issue**.
- **Não se desenvolve diretamente na `main`.** A `main` está protegida.
- Cada Issue é atribuída (campo *Assignees*) a um membro da equipa antes de começar.
- Cada Issue deve ser pequena e concreta. Evitar Issues genéricas do tipo "Implementar Sprint 2".

## 2. Branches

### Nomenclatura

```
<tipo>/<número-da-issue>-<descrição-curta-em-minúsculas>
```

| Tipo | Quando usar | Exemplo |
|---|---|---|
| `feature/` | Nova funcionalidade | `feature/14-endpoint-health-check` |
| `fix/` | Correção de erro | `fix/21-validacao-numero-aluno` |
| `docs/` | Documentação | `docs/5-decisions-md` |
| `chore/` | Configuração, build, dependências | `chore/3-setup-backend` |
| `test/` | Testes | `test/30-testes-perfil` |

### Regras
- Criar sempre a branch a partir da `main` atualizada.
- Uma branch = uma Issue.
- Branches de curta duração: integrar cedo e com frequência.
- **Apagar a branch depois do merge** (opção "Delete branch" no PR).

## 3. Commits

Mensagens curtas, no imperativo, com prefixo de tipo:

```
feat: adiciona endpoint de health-check
fix: corrige validação do número de aluno
docs: atualiza README com instruções de setup
test: adiciona testes ao cálculo do perfil
chore: configura Maven Wrapper
```

Sempre que possível, referenciar a Issue (`#14`).

## 4. Pull Requests

### Abertura
- Título claro e descrição do que foi feito e porquê.
- Referenciar a Issue na descrição com `Closes #<número>` (fecha a Issue automaticamente no merge).
- Associar o PR ao milestone do sprint.
- PRs pequenos e focados (idealmente uma Issue por PR).

### Review
- **Cada PR tem de ser revisto e aprovado por, pelo menos, 1 outro membro.**
- **Ninguém aprova o seu próprio PR.**
- O autor indica o *reviewer* ao abrir o PR; o reviewer responde no prazo de 24 horas.
- Os comentários devem ser concretos e construtivos (o quê, porquê e, se possível, uma sugestão).
- O autor responde a todos os comentários e só pede nova review depois de os resolver.
- O merge é feito pelo autor, depois de aprovado e com a build a passar.

### Merge
- Estratégia: *Squash and merge*, para manter o histórico da `main` limpo.
- Apagar a branch após o merge.

### Checklist do autor (antes de pedir review)
- [ ] O projeto compila e os testes passam localmente
- [ ] O PR está ligado à Issue (`Closes #N`)
- [ ] Sem código comentado, `System.out.println` de debug ou ficheiros desnecessários
- [ ] Documentação atualizada, se aplicável

## 5. Responsabilidades

| Tarefa | Responsável |
|---|---|
| Atribuir Issues | Cada membro atribui-se a si; dúvidas resolvem-se na reunião de equipa |
| Rever PRs | Qualquer membro que não seja o autor, em rotatividade |
| Manter o board atualizado | Cada membro move os seus cartões |
| Manter a documentação | Quem altera o comportamento atualiza a documentação |

## 6. Estrutura do projeto

```
studymatch/
├── backend/              # Spring Boot + Maven
│   └── src/main/java/pt/studymatch/
│       ├── controller/   # Endpoints REST
│       ├── service/      # Lógica de negócio
│       ├── repository/   # Acesso a dados (Spring Data JPA)
│       ├── model/        # Entidades de domínio
│       ├── dto/          # Objetos de transferência (pedidos/respostas)
│       └── config/       # Configuração
├── frontend/             # React
│   └── src/
│       ├── components/
│       ├── pages/
│       └── services/     # Chamadas à API
├── docs/                 # Casos de uso, domínio, arquitetura
├── CONTRIBUTING.md
├── DECISIONS.md
└── README.md
```

A lógica de negócio fica na camada `service` (e no modelo de domínio), nunca nos controllers.

## 7. Convenções de código

### Gerais
- Código claro e explícito, preferindo legibilidade a soluções "inteligentes".
- Métodos curtos, com uma única responsabilidade.
- Sem código morto nem comentários a desativar código.
- Comentários explicam o *porquê*, não o *quê*.

### Java (backend)
| Elemento | Convenção | Exemplo |
|---|---|---|
| Classes | `PascalCase` | `PerfilAcademico` |
| Métodos e variáveis | `camelCase` | `calcularPerfil()` |
| Constantes | `UPPER_SNAKE_CASE` | `PESO_MAXIMO` |
| Pacotes | minúsculas | `pt.studymatch.service` |
| Testes | `<Classe>Test` | `PerfilServiceTest` |

- Conceitos de domínio com nomes consistentes em todo o código (ex.: `Estudante`, `UnidadeCurricular`, `Competencia`).
- Injeção de dependências por construtor.
- Testes unitários com JUnit 5, focados em comportamento e regras de negócio.

### JavaScript/React (frontend)
- Componentes em `PascalCase`, funções e variáveis em `camelCase`.
- Chamadas à API isoladas em `services/`, nunca diretamente nos componentes.

### API REST
- Prefixo `/api`, recursos no plural e em minúsculas (ex.: `GET /api/estudantes`).
- Respostas em JSON, com códigos HTTP adequados (200, 201, 400, 404).

## 8. Definition of Done — Sprint 1

Uma tarefa só está concluída quando:

- [ ] Existe uma Issue associada, com a branch e o PR ligados a ela
- [ ] O PR foi revisto e aprovado por outro membro da equipa
- [ ] O código compila com a ferramenta de build (`./mvnw clean package` no backend, `npm run build` no frontend)
- [ ] Segue as convenções deste documento
- [ ] A documentação relevante foi criada ou atualizada
- [ ] O trabalho foi integrado na `main` e a branch foi apagada
- [ ] O cartão no board está na coluna *Done*
