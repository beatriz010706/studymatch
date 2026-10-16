import { useEffect, useState } from 'react'
import { listarEstudantes, obterEstado } from './services/api.js'
import EstudantesPagina from './components/EstudantesPagina.jsx'
import InscricoesPagina from './components/InscricoesPagina.jsx'

const SEPARADORES = [
  { id: 'estudantes', titulo: 'Estudantes' },
  { id: 'notas', titulo: 'Notas' },
]

function App() {
  const [estado, setEstado] = useState(null)
  const [estudantes, setEstudantes] = useState([])
  const [idSelecionado, setIdSelecionado] = useState('')
  const [separador, setSeparador] = useState('estudantes')
  const [erro, setErro] = useState('')

  async function carregarEstudantes() {
    try {
      setErro('')
      setEstado(await obterEstado())
      setEstudantes(await listarEstudantes())
    } catch (excecao) {
      setEstado(null)
      setErro(excecao.message)
    }
  }

  useEffect(() => {
    carregarEstudantes()
  }, [])

  // O estudante selecionado é partilhado por todos os separadores
  const estudanteSelecionado = estudantes.find((estudante) => String(estudante.id) === String(idSelecionado))

  function renderizarSeparador() {
    if (separador === 'estudantes') {
      return (
        <EstudantesPagina
          estudantes={estudantes}
          aoAlterar={carregarEstudantes}
          aoSelecionar={(id) => setIdSelecionado(String(id))}
        />
      )
    }

    if (!estudanteSelecionado) {
      return <p className="aviso">Escolha um estudante na lista acima para continuar.</p>
    }

    if (separador === 'notas') {
      return <InscricoesPagina estudante={estudanteSelecionado} />
    }
    return null
  }

  return (
    <main>
      <header>
        <h1>StudyMatch</h1>
        {estado ? (
          <span className="ok">Backend ligado | {estado.totalEstudantes} estudantes</span>
        ) : (
          <span className="erro">Backend indisponível</span>
        )}
      </header>

      <div className="seletor">
        <label htmlFor="estudante">Estudante:</label>
        <select id="estudante" value={idSelecionado} onChange={(evento) => setIdSelecionado(evento.target.value)}>
          <option value="">-- escolher --</option>
          {estudantes.map((estudante) => (
            <option key={estudante.id} value={estudante.id}>
              {estudante.numeroAluno} - {estudante.nome}
            </option>
          ))}
        </select>
      </div>

      <nav>
        {SEPARADORES.map((item) => (
          <button
            key={item.id}
            className={item.id === separador ? 'ativo' : ''}
            onClick={() => setSeparador(item.id)}
          >
            {item.titulo}
          </button>
        ))}
      </nav>

      {erro && <p className="erro">{erro}</p>}

      <section>{renderizarSeparador()}</section>
    </main>
  )
}

export default App
