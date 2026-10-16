import { useEffect, useState } from 'react'
import {
  atualizarNota,
  listarInscricoes,
  listarUnidadesCurriculares,
  registarInscricao,
} from '../services/api.js'

// Inscrições e notas do estudante selecionado
function InscricoesPagina({ estudante }) {
  const [inscricoes, setInscricoes] = useState([])
  const [unidades, setUnidades] = useState([])
  const [unidadeId, setUnidadeId] = useState('')
  const [anoLetivo, setAnoLetivo] = useState('2025/2026')
  const [nota, setNota] = useState('')
  const [notasEmEdicao, setNotasEmEdicao] = useState({})
  const [erro, setErro] = useState('')

  async function carregar() {
    try {
      setErro('')
      setInscricoes(await listarInscricoes(estudante.id))
      setUnidades(await listarUnidadesCurriculares())
    } catch (excecao) {
      setErro(excecao.message)
    }
  }

  useEffect(() => {
    carregar()
  }, [estudante.id])

  async function aoRegistar(evento) {
    evento.preventDefault()
    try {
      setErro('')
      await registarInscricao(estudante.id, {
        unidadeCurricularId: Number(unidadeId),
        anoLetivo: anoLetivo,
        nota: nota === '' ? null : Number(nota),
      })
      setNota('')
      await carregar()
    } catch (excecao) {
      setErro(excecao.message)
    }
  }

  async function aoGuardarNota(inscricao) {
    const texto = notasEmEdicao[inscricao.id]
    try {
      setErro('')
      await atualizarNota(estudante.id, inscricao.id, texto === '' ? null : Number(texto))
      setNotasEmEdicao({ ...notasEmEdicao, [inscricao.id]: undefined })
      await carregar()
    } catch (excecao) {
      setErro(excecao.message)
    }
  }

  return (
    <div>
      <h2>Notas de {estudante.nome}</h2>

      <form onSubmit={aoRegistar}>
        <select value={unidadeId} onChange={(evento) => setUnidadeId(evento.target.value)} required>
          <option value="">-- unidade curricular --</option>
          {unidades.map((unidade) => (
            <option key={unidade.id} value={unidade.id}>
              {unidade.nome}
            </option>
          ))}
        </select>
        <input placeholder="Ano letivo" value={anoLetivo} onChange={(evento) => setAnoLetivo(evento.target.value)} />
        <input
          type="number"
          min="0"
          max="20"
          step="0.1"
          placeholder="Nota (0-20)"
          value={nota}
          onChange={(evento) => setNota(evento.target.value)}
        />
        <button type="submit">Registar</button>
      </form>
      {erro && <p className="erro">{erro}</p>}

      {inscricoes.length === 0 ? (
        <p>Ainda não há inscrições.</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>Unidade curricular</th>
              <th>Ano letivo</th>
              <th>Nota</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {inscricoes.map((inscricao) => {
              const emEdicao = notasEmEdicao[inscricao.id] !== undefined
              return (
                <tr key={inscricao.id}>
                  <td>{inscricao.unidadeCurricular}</td>
                  <td>{inscricao.anoLetivo}</td>
                  <td>
                    {emEdicao ? (
                      <input
                        type="number"
                        min="0"
                        max="20"
                        step="0.1"
                        value={notasEmEdicao[inscricao.id]}
                        onChange={(evento) =>
                          setNotasEmEdicao({ ...notasEmEdicao, [inscricao.id]: evento.target.value })
                        }
                      />
                    ) : inscricao.nota === null ? (
                      'sem nota'
                    ) : (
                      inscricao.nota
                    )}
                  </td>
                  <td className="acoes">
                    {emEdicao ? (
                      <button onClick={() => aoGuardarNota(inscricao)}>Guardar</button>
                    ) : (
                      <button
                        className="secundario"
                        onClick={() =>
                          setNotasEmEdicao({
                            ...notasEmEdicao,
                            [inscricao.id]: inscricao.nota === null ? '' : String(inscricao.nota),
                          })
                        }
                      >
                        Alterar nota
                      </button>
                    )}
                  </td>
                </tr>
              )
            })}
          </tbody>
        </table>
      )}
    </div>
  )
}

export default InscricoesPagina
