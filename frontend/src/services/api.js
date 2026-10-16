// Todas as chamadas ao backend ficam neste ficheiro (nunca dentro dos componentes).

async function pedir(url, opcoes) {
  const resposta = await fetch(url, opcoes)

  if (!resposta.ok) {
    // O backend devolve os erros no formato { "erro": "mensagem" }
    const corpo = await resposta.json().catch(() => ({}))
    throw new Error(corpo.erro || 'Pedido falhou (código ' + resposta.status + ')')
  }

  return resposta.json()
}

function enviarJson(metodo, corpo) {
  return {
    method: metodo,
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(corpo),
  }
}

export function obterEstado() {
  return pedir('/api/status')
}

// ----- Estudantes -----
export function listarEstudantes() {
  return pedir('/api/estudantes')
}

export function criarEstudante(dados) {
  return pedir('/api/estudantes', enviarJson('POST', dados))
}

export function atualizarEstudante(id, dados) {
  return pedir('/api/estudantes/' + id, enviarJson('PUT', dados))
}

// ----- Catálogo -----
export function listarUnidadesCurriculares() {
  return pedir('/api/unidades-curriculares')
}

export function listarCompetencias() {
  return pedir('/api/competencias')
}

// ----- Inscrições e notas -----
export function listarInscricoes(estudanteId) {
  return pedir('/api/estudantes/' + estudanteId + '/inscricoes')
}

export function registarInscricao(estudanteId, dados) {
  return pedir('/api/estudantes/' + estudanteId + '/inscricoes', enviarJson('POST', dados))
}

export function atualizarNota(estudanteId, inscricaoId, nota) {
  return pedir(
    '/api/estudantes/' + estudanteId + '/inscricoes/' + inscricaoId,
    enviarJson('PUT', { nota: nota }),
  )
}

// ----- Cold start: questionário -----
export function obterAutoDeclaracao(estudanteId) {
  return pedir('/api/estudantes/' + estudanteId + '/auto-declaracao')
}

export function guardarAutoDeclaracao(estudanteId, respostas) {
  return pedir('/api/estudantes/' + estudanteId + '/auto-declaracao', enviarJson('POST', respostas))
}

// ----- Perfil -----
export function obterPerfil(estudanteId) {
  return pedir('/api/estudantes/' + estudanteId + '/perfil')
}
