import { useEffect, useState } from 'react'
import { guardarAutoDeclaracao, listarCompetencias, obterAutoDeclaracao } from '../services/api.js'

const NIVEIS = [
  { valor: 1, texto: '1 - Muito baixo' },
  { valor: 2, texto: '2 - Baixo' },
  { valor: 3, texto: '3 - Médio' },
  { valor: 4, texto: '4 - Bom' },
  { valor: 5, texto: '5 - Muito bom' },
]

// Questionário de cold start: o estudante diz que nível acha que tem em cada competência
function QuestionarioPagina({ estudante, aoGuardar }) {
  const [competencias, setCompetencias] = useState([])
  const [respostas, setRespostas] = useState({})
  const [erro, setErro] = useState('')

  useEffect(() => {
    async function carregar() {
      try {
        setErro('')
        const listaCompetencias = await listarCompetencias()
        const guardadas = await obterAutoDeclaracao(estudante.id)

        const respostasGuardadas = {}
        for (const resposta of guardadas) {
          respostasGuardadas[resposta.codigoCompetencia] = String(resposta.nivel)
        }
        setCompetencias(listaCompetencias)
        setRespostas(respostasGuardadas)
      } catch (excecao) {
        setErro(excecao.message)
      }
    }
    carregar()
  }, [estudante.id])

  async function aoSubmeter(evento) {
    evento.preventDefault()

    // Só se enviam as competências a que o estudante respondeu
    const corpo = []
    for (const competencia of competencias) {
      const valor = respostas[competencia.codigo]
      if (valor !== undefined && valor !== '') {
        corpo.push({ codigoCompetencia: competencia.codigo, nivel: Number(valor) })
      }
    }

    try {
      setErro('')
      await guardarAutoDeclaracao(estudante.id, corpo)
      aoGuardar()
    } catch (excecao) {
      setErro(excecao.message)
    }
  }

  return (
    <div>
      <h2>Questionário inicial de {estudante.nome}</h2>
      <p>Indique o nível que acha que tem em cada competência. Pode deixar em branco o que não souber.</p>

      <form onSubmit={aoSubmeter} className="questionario">
        {competencias.map((competencia) => (
          <label key={competencia.codigo}>
            <span>{competencia.nome}</span>
            <select
              value={respostas[competencia.codigo] || ''}
              onChange={(evento) => setRespostas({ ...respostas, [competencia.codigo]: evento.target.value })}
            >
              <option value="">-- sem resposta --</option>
              {NIVEIS.map((nivel) => (
                <option key={nivel.valor} value={nivel.valor}>
                  {nivel.texto}
                </option>
              ))}
            </select>
          </label>
        ))}
        <button type="submit">Guardar e ver perfil</button>
      </form>
      {erro && <p className="erro">{erro}</p>}
    </div>
  )
}

export default QuestionarioPagina
