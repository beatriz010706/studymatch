// Todas as chamadas à API ficam aqui, fora dos componentes.

export async function obterEstado() {
    const resposta = await fetch('/api/status')
    if (!resposta.ok) {
        throw new Error('Não foi possível contactar o backend')
    }
    return resposta.json()
}

export async function listarEstudantes() {
    const resposta = await fetch('/api/estudantes')
    if (!resposta.ok) {
        throw new Error('Não foi possível obter os estudantes')
    }
    return resposta.json()
}

export async function criarEstudante(dadosEstudante) {
    const resposta = await fetch('/api/estudantes', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(dadosEstudante),
    })

    if (!resposta.ok) {
        const corpo = await resposta.json().catch(() => ({}))
        throw new Error(corpo.erro || 'Não foi possível criar o estudante')
    }
    return resposta.json()
}
