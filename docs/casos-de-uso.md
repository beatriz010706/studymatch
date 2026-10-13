# Modelo de Casos de Uso — StudyMatch (Sprint 1)

Modelo inicial de casos de uso. Cobre as áreas obrigatórias do enunciado, o núcleo das decisões de equipa (cold start híbrido e matching por score ponderado) e **dois casos de uso extra** propostos pela equipa. Os casos de uso podem evoluir nos sprints seguintes.

## 1. Atores

| Ator | Descrição |
|---|---|
| **Estudante** | Pessoa inscrita num curso. Preenche o seu perfil inicial e consulta o seu perfil. |
| **Docente / Coordenador** | Gere estudantes, percurso académico, competências e contextos de agrupamento. |
| **Sistema** | Executa cálculos automáticos (perfil, compatibilidade). |

## 2. Visão geral

| Código | Caso de uso | Área do enunciado | Ator principal |
|---|---|---|---|
| UC01 | Registar estudante | Identidade académica | Docente |
| UC02 | Consultar e atualizar estudantes | Identidade académica | Docente |
| UC03 | Registar percurso académico | Trajetória académica | Docente |
| UC04 | Definir competências e associá-las a UCs | Competências | Docente |
| UC05 | Consultar perfil do estudante | Perfil do estudante | Estudante / Docente |
| UC06 | Preencher perfil inicial (cold start) | Cold start | Estudante |
| UC07 | Definir contexto de agrupamento | Contexto de agrupamento | Docente |
| UC08 | Calcular compatibilidade entre estudantes | Núcleo do matching | Sistema |
| UC09 | Consultar explicação do perfil *(extra)* | Valor acrescentado | Estudante / Docente |
| UC10 | Importar estudantes e resultados por ficheiro CSV *(extra)* | Valor acrescentado | Docente |

---

## UC01 — Registar estudante

**Objetivo:** criar a identidade de um estudante no contexto do seu curso.
**Ator principal:** Docente.

**Cenário principal**
1. O docente escolhe "Novo estudante".
2. Introduz número de aluno, nome, email, curso e ano curricular.
3. O sistema valida os dados.
4. O sistema guarda o estudante e cria um perfil académico vazio.
5. O sistema confirma o registo.

**Fluxos alternativos / exceção**
- 3a. Número de aluno já existente: o sistema rejeita e indica o conflito.
- 3b. Campo obrigatório em falta ou email inválido: o sistema indica o campo em erro.

**Regras de negócio**
- O número de aluno é único.
- Nome, número de aluno, curso e ano curricular são obrigatórios.
- O ano curricular está entre 1 e a duração do curso.

**Conceitos de domínio:** Estudante, Curso, PerfilAcademico.

---

## UC02 — Consultar e atualizar estudantes

**Objetivo:** ver os estudantes existentes e corrigir os seus dados.
**Ator principal:** Docente.

**Cenário principal**
1. O docente abre a lista de estudantes.
2. O sistema apresenta os estudantes registados.
3. O docente seleciona um estudante e edita os dados.
4. O sistema valida e guarda as alterações.

**Fluxos alternativos / exceção**
- 2a. Lista vazia: o sistema indica que não há estudantes registados.
- 4a. Alteração viola uma regra (ex.: número de aluno duplicado): o sistema rejeita a alteração.

**Regras de negócio**
- O número de aluno não pode ser alterado para um já existente.
- As alterações não apagam o percurso académico do estudante.

**Conceitos de domínio:** Estudante.

---

## UC03 — Registar percurso académico

**Objetivo:** registar as inscrições e os resultados de um estudante nas unidades curriculares.
**Ator principal:** Docente.

**Cenário principal**
1. O docente seleciona um estudante.
2. Escolhe a unidade curricular, o ano letivo e indica a inscrição.
3. Mais tarde, regista o resultado final (nota).
4. O sistema guarda a inscrição e o resultado.
5. O sistema atualiza o perfil do estudante (ver UC05).

**Fluxos alternativos / exceção**
- 3a. Nota fora do intervalo 0–20: o sistema rejeita.
- 2a. Estudante já inscrito na UC nesse ano letivo: o sistema rejeita a duplicação.
- 2b. Estudante repete a UC: o sistema regista uma **nova tentativa**, sem apagar as anteriores.

**Regras de negócio**
- Escala de notas de 0 a 20; aprovação com nota maior ou igual a 10.
- Cada tentativa fica registada (o histórico nunca é apagado).
- Para o perfil, considera-se a melhor nota aprovada de cada UC.
- Uma inscrição sem resultado ainda não contribui para o perfil.

**Conceitos de domínio:** Inscricao, UnidadeCurricular, Estudante, EstadoInscricao.

---

## UC04 — Definir competências e associá-las a unidades curriculares

**Objetivo:** definir que competências existem e como cada UC contribui para elas.
**Ator principal:** Docente (coordenador).

**Cenário principal**
1. O docente cria uma competência (nome e descrição).
2. Seleciona uma unidade curricular.
3. Associa a competência à UC, indicando o peso da contribuição.
4. O sistema guarda a associação.

**Fluxos alternativos / exceção**
- 3a. Peso fora do intervalo 0 a 1: o sistema rejeita.
- 3b. Associação já existente: o sistema atualiza o peso em vez de duplicar.

**Regras de negócio**
- Uma UC pode contribuir para várias competências e uma competência pode receber contributos de várias UCs.
- O peso da contribuição está entre 0 e 1.
- As competências são configuráveis, não estão fixas no código.

**Conceitos de domínio:** Competencia, UnidadeCurricular, ContribuicaoUC.

---

## UC05 — Consultar perfil do estudante

**Objetivo:** apresentar um resumo das competências do estudante, útil para a futura formação de grupos.
**Atores principais:** Estudante, Docente.

**Cenário principal**
1. O ator seleciona um estudante.
2. O sistema calcula o nível de cada competência a partir dos resultados académicos e da auto-declaração.
3. O sistema apresenta o perfil: nível, confiança e origem (académica ou auto-declarada) de cada competência.

**Fluxos alternativos / exceção**
- 2a. Sem resultados académicos: o perfil usa a informação do cold start (UC06).
- 2b. Competência sem qualquer informação: aparece como "desconhecida", com confiança zero.

**Regras de negócio**
- O nível de uma competência vem da média ponderada das notas das UCs associadas.
- A confiança aumenta com o número de evidências académicas.
- Quando há evidência académica, esta prevalece sobre a auto-declaração.

**Conceitos de domínio:** PerfilAcademico, NivelCompetencia, OrigemEvidencia.

---

## UC06 — Preencher perfil inicial (cold start)

**Objetivo:** construir um perfil útil para um estudante com pouco ou nenhum histórico.
**Ator principal:** Estudante.

**Cenário principal**
1. O estudante entra pela primeira vez no sistema.
2. O sistema obtém o contexto institucional: curso, ano curricular e UCs em que está inscrito.
3. O sistema apresenta um questionário curto: nível percebido (1 a 5) em cada competência e disponibilidade semanal.
4. O estudante responde e submete.
5. O sistema cria o perfil inicial, marcado com maturidade baixa.

**Fluxos alternativos / exceção**
- 4a. Questionário incompleto: as competências não respondidas ficam "desconhecidas".
- 2a. Estudante sem inscrições registadas: o perfil assenta apenas na auto-declaração.
- 4b. Valor fora de 1 a 5: o sistema rejeita a resposta.

**Regras de negócio**
- A auto-declaração tem confiança limitada, inferior à da evidência académica.
- À medida que surgem resultados académicos, o peso da auto-declaração diminui.
- O questionário pode ser atualizado pelo estudante.

**Conceitos de domínio:** AutoDeclaracao, Disponibilidade, PerfilAcademico (maturidade), Competencia.

---

## UC07 — Definir contexto de agrupamento

**Objetivo:** descrever a situação em que um conjunto de grupos vai ser formado.
**Ator principal:** Docente.

**Cenário principal**
1. O docente escolhe a unidade curricular.
2. Indica a atividade (ex.: projeto de Software Quality) e o tamanho alvo dos grupos.
3. Define restrições opcionais (ex.: competências mínimas, sobreposição de disponibilidade).
4. O sistema guarda o contexto de agrupamento.

**Fluxos alternativos / exceção**
- 2a. Tamanho alvo inferior a 2: o sistema rejeita.

**Regras de negócio**
- Um contexto de agrupamento está sempre ligado a uma UC.
- O tamanho alvo dos grupos é de, pelo menos, 2 elementos.

**Conceitos de domínio:** ContextoAgrupamento, UnidadeCurricular, Grupo (futuro).

---

## UC08 — Calcular compatibilidade entre estudantes

**Objetivo:** obter uma pontuação de compatibilidade entre estudantes, base da futura formação de grupos.
**Ator principal:** Sistema (a pedido do docente).

**Cenário principal**
1. O docente pede a compatibilidade entre dois estudantes num contexto de agrupamento.
2. O sistema obtém o perfil de cada estudante.
3. Para cada atributo (competências, disponibilidade, ano curricular), calcula a contribuição ponderada.
4. O sistema soma as contribuições e devolve a pontuação.

**Fluxos alternativos / exceção**
- 2a. Estudante com perfil incompleto: os atributos sem dados contribuem com um valor neutro e a confiança do resultado desce.

**Regras de negócio**
- Score ponderado: soma de (peso do atributo × contribuição do atributo).
- Os pesos são definidos pela equipa e ficam configuráveis.
- A soma dos pesos é 1.

**Conceitos de domínio:** PerfilAcademico, NivelCompetencia, ContextoAgrupamento.

---

## UC09 — Consultar explicação do perfil *(extra)*

**Objetivo:** mostrar como cada resultado e cada resposta do questionário contribuíram para o perfil.
**Atores principais:** Estudante, Docente.

**Cenário principal**
1. O ator abre o perfil de um estudante (UC05).
2. Escolhe "Porquê este valor?" numa competência.
3. O sistema lista as evidências usadas (UCs, notas, pesos, auto-declaração) e o respetivo contributo.

**Fluxos alternativos / exceção**
- 3a. Competência sem evidências: o sistema indica que não há informação.

**Regras de negócio**
- A explicação usa exatamente as mesmas evidências e pesos do cálculo do perfil.

**Conceitos de domínio:** NivelCompetencia, Inscricao, ContribuicaoUC, AutoDeclaracao.

**Justificação do valor:** a formação de grupos influencia a experiência e os resultados dos estudantes. Tornar o perfil explicável aumenta a confiança no sistema, permite ao estudante corrigir dados errados e ajuda o docente a justificar decisões. Responde à preocupação de **transparência**.

---

## UC10 — Importar estudantes e resultados por ficheiro CSV *(extra)*

**Objetivo:** carregar em lote estudantes e resultados académicos.
**Ator principal:** Docente.

**Cenário principal**
1. O docente escolhe "Importar" e seleciona um ficheiro CSV.
2. O sistema valida o ficheiro linha a linha.
3. O sistema cria ou atualiza os estudantes e as inscrições válidas.
4. O sistema apresenta um relatório: linhas importadas e linhas rejeitadas, com o motivo.

**Fluxos alternativos / exceção**
- 2a. Formato ou cabeçalhos inválidos: o sistema rejeita o ficheiro.
- 3a. Linha inválida (ex.: nota fora de 0–20): a linha é ignorada e registada no relatório; as restantes são importadas.

**Regras de negócio**
- A importação aplica as mesmas regras de validação do registo manual.
- Importar o mesmo ficheiro duas vezes não duplica dados.

**Conceitos de domínio:** Estudante, Inscricao, UnidadeCurricular.

**Justificação do valor:** introduzir manualmente centenas de estudantes e resultados é impraticável. A importação reflete a realidade da **gestão académica** (os dados já existem em sistemas institucionais) e torna o produto utilizável em contexto real.
