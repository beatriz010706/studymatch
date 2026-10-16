import { useState } from 'react'
import { atualizarEstudante, criarEstudante } from '../services/api.js'

const FORMULARIO_VAZIO = {
  numeroAluno: '',
  nome: '',
  curso: 'Engenharia Informática',
  anoCurricular: 1,
}

// Lista de estudantes + formulário para registar ou editar
function EstudantesPagina({ estudantes, aoAlterar, aoSelecionar }) {
  const [formulario, setFormulario] = useState(FORMULARIO_VAZIO)
  const [idEmEdicao, setIdEmEdicao] = useState(null)
  const [erro, setErro] = useState('')

  function aoAlterarCampo(evento) {
    const { name, value } = evento.target
    setFormulario({ ...formulario, [name]: value })
  }

  function comecarEdicao(estudante) {
    setIdEmEdicao(estudante.id)
    setFormulario({
      numeroAluno: estudante.numeroAluno,
      nome: estudante.nome,
      curso: estudante.curso,
      anoCurricular: estudante.anoCurricular,
    })
    setErro('')
  }

  function cancelarEdicao() {
    setIdEmEdicao(null)
    setFormulario(FORMULARIO_VAZIO)
    setErro('')
  }

  async function aoSubmeter(evento) {
    evento.preventDefault()
    const dados = { ...formulario, anoCurricular: Number(formulario.anoCurricular) }

    try {
      setErro('')
      if (idEmEdicao === null) {
        await criarEstudante(dados)
      } else {
        await atualizarEstudante(idEmEdicao, dados)
      }
      cancelarEdicao()
      await aoAlterar()
    } catch (excecao) {
      setErro(excecao.message)
    }
  }

  return (
    <div>
      <h2>{idEmEdicao === null ? 'Novo estudante' : 'Editar estudante'}</h2>
      <form onSubmit={aoSubmeter}>
        <input name="numeroAluno" placeholder="Número de aluno" value={formulario.numeroAluno} onChange={aoAlterarCampo} />
        <input name="nome" placeholder="Nome" value={formulario.nome} onChange={aoAlterarCampo} />
        <input name="curso" placeholder="Curso" value={formulario.curso} onChange={aoAlterarCampo} />
        <input
          name="anoCurricular"
          type="number"
          min="1"
          max="5"
          value={formulario.anoCurricular}
          onChange={aoAlterarCampo}
        />
        <button type="submit">{idEmEdicao === null ? 'Registar' : 'Guardar alterações'}</button>
        {idEmEdicao !== null && (
          <button type="button" className="secundario" onClick={cancelarEdicao}>
            Cancelar
          </button>
        )}
      </form>
      {erro && <p className="erro">{erro}</p>}

      <h2>Estudantes</h2>
      {estudantes.length === 0 ? (
        <p>Ainda não há estudantes registados.</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>Nº aluno</th>
              <th>Nome</th>
              <th>Curso</th>
              <th>Ano</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {estudantes.map((estudante) => (
              <tr key={estudante.id}>
                <td>{estudante.numeroAluno}</td>
                <td>{estudante.nome}</td>
                <td>{estudante.curso}</td>
                <td>{estudante.anoCurricular}</td>
                <td className="acoes">
                  <button className="secundario" onClick={() => aoSelecionar(estudante.id)}>
                    Selecionar
                  </button>
                  <button className="secundario" onClick={() => comecarEdicao(estudante)}>
                    Editar
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}

export default EstudantesPagina
