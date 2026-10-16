# StudyMatch

Aplicação full-stack para a formação automática de grupos de estudantes, com base no histórico académico e nas competências acumuladas ao longo do curso.

Projeto desenvolvido no âmbito da unidade curricular de **Software Quality**, na Universidade Portucalense (UPT).

## Contexto

Em muitos contextos académicos, os grupos de trabalho são formados de forma aleatória, por amizade ou disponibilidade momentânea, sem aproveitar o conhecimento e as competências que os estudantes acumulam ao longo do curso. O StudyMatch propõe-se resolver isto, construindo uma representação de cada estudante e usando-a para apoiar a formação de grupos.

Dois problemas centrais guiam o desenho do sistema:

- **Cold start:** como representar um estudante com pouco ou nenhum histórico académico.
- **Matching:** como usar o histórico e as competências para formar grupos.

## Estado atual: Sprint 2 (Perfil do estudante e cold start)

- Gestão de estudantes (criar, listar, editar) com persistência.
- Histórico académico: unidades curriculares, inscrições e notas.
- **Perfil do estudante** por competência, com nível, confiança e origem, calculado a partir das notas.
- **Cold start:** questionário de auto-declaração que perde peso à medida que há notas.
- **Explicação do perfil** ("Porquê este valor?").
- Testes unitários e build reproduzível.

A formação automática de grupos será implementada em sprints futuros.

## Stack tecnológica

| Camada | Tecnologia |
|---|---|
| Frontend | React (Vite) |
| Backend | Java 21 + Spring Boot 3 (API REST) |
| Persistência | PostgreSQL ou H2 (Spring Data JPA) |
| Build | Maven (Maven Wrapper) |
| Testes | JUnit 5 + Mockito |

## Pré-requisitos

- Java 21 (JDK)
- Node.js 20 ou superior
- Docker (opcional, só para usar PostgreSQL)

## Como correr o projeto

### 1. Backend

No Windows usar `mvnw.cmd` em vez de `./mvnw`. O backend fica em http://localhost:8080.

```bash
cd backend
```

Escolher **uma** das opções de base de dados:

| Opção | Comando | Os dados ficam guardados? |
|---|---|---|
| **H2 em ficheiro** (recomendada) | `./mvnw spring-boot:run -Dspring-boot.run.profiles=h2file` | Sim, na pasta `backend/data` |
| PostgreSQL (Docker) | `docker compose up -d` na raiz e depois `./mvnw spring-boot:run` | Sim |
| H2 em memória | `./mvnw spring-boot:run -Dspring-boot.run.profiles=h2` | Não, perdem-se ao parar |

No PowerShell, o argumento do perfil leva aspas: `.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=h2file"`.

Ao arrancar pela primeira vez, o backend insere automaticamente as competências, as unidades curriculares e as respetivas contribuições (não duplica nas execuções seguintes).

### 2. Frontend

```bash
cd frontend
npm install
npm run dev
```

O frontend fica em http://localhost:5173 e comunica com o backend através de `/api`.

### 3. Testes e build do backend

```bash
cd backend
./mvnw clean test       # compila e executa todos os testes
./mvnw clean package    # gera o artefacto em target/
```

Executar o artefacto gerado:

```bash
java -jar target/studymatch-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=h2file
```

## API

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/status` | Estado da aplicação |
| GET, POST | `/api/estudantes` | Listar e criar estudantes |
| GET, PUT | `/api/estudantes/{id}` | Consultar e atualizar um estudante |
| GET | `/api/unidades-curriculares` | Listar unidades curriculares |
| GET | `/api/competencias` | Listar competências |
| GET, POST | `/api/estudantes/{id}/inscricoes` | Listar e registar inscrições e notas |
| PUT | `/api/estudantes/{id}/inscricoes/{inscricaoId}` | Corrigir a nota de uma inscrição |
| GET, POST | `/api/estudantes/{id}/auto-declaracao` | Questionário de cold start |
| GET | `/api/estudantes/{id}/perfil` | Perfil do estudante, com explicação |

Os erros são devolvidos como `{ "erro": "mensagem" }` com os códigos 400, 404 ou 409.

## Guião da demonstração

| Cenário | Passos |
|---|---|
| **A. Estudante com histórico** | Registar um estudante. No separador *Notas*, registar Bases de Dados 16, Algoritmos e Estruturas de Dados 14 e Laboratório de Tecnologias Web 18. No separador *Perfil*, ver BD 80, PROG 77,5, ALG 70, WEB 90. **Alterar uma nota** e ver o perfil mudar |
| **B. Cold start** | Registar um estudante novo: o perfil aparece sem dados. No *Questionário*, responder e ver o perfil inicial (origem auto-declarada, confiança 40%). Registar uma nota e ver a origem passar a mista, com a auto-declaração a perder peso |
| **C. Funcionalidade própria** | No *Perfil*, clicar em **Porquê este valor?** numa competência e mostrar as notas e os pesos que a produziram |
| **D. Testes e build** | `cd backend` e `./mvnw clean test`, com `BUILD SUCCESS` |

## Documentação

- [Casos de uso](docs/casos-de-uso.md)
- [Modelo de domínio](docs/modelo-dominio.md)
- [Arquitetura](docs/arquitetura.md)
- [Decisões de design do Sprint 2](docs/design-sprint2.md)
- [Decisões técnicas](DECISIONS.md)
- [Guia de contribuição](CONTRIBUTING.md) (workflow, convenções, Definition of Done)

## Equipa

- Inês
- Beatriz
- Miguel
- Diogo

## Workflow de desenvolvimento

`Issue → Branch → Implementação → Pull Request → Review → Merge`

Detalhes em [CONTRIBUTING.md](CONTRIBUTING.md).
