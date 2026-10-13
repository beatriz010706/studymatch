import { useEffect, useState } from 'react'
import { criarEstudante, listarEstudantes, obterEstado } from './services/api.js'

const FORMULARIO_VAZIO = {
    numeroAluno: '',
    nome: '',
    curso: 'Engenharia Informática',
    anoCurricular: 1,
}

function App() {
    const [estado, setEstado] = useState(null)
    const [estudantes, setEstudantes] = useState([])
    const [formulario, setFormulario] = useState(FORMULARIO_VAZIO)
    const [erro, setErro] = useState('')

    // Carrega o estado do backend e a lista de estudantes ao abrir a página
    async function carregarDados() {
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
        carregarDados()
    }, [])

    function aoAlterarCampo(evento) {
        const { name, value } = evento.target
        setFormulario({ ...formulario, [name]: value })
    }

    async function aoSubmeter(evento) {
        evento.preventDefault()
        try {
            setErro('')
            await criarEstudante({
                ...formulario,
                anoCurricular: Number(formulario.anoCurricular),
            })
            setFormulario(FORMULARIO_VAZIO)
            await carregarDados()
        } catch (excecao) {
            setErro(excecao.message)
        }
    }

    return (
        <main>
            <h1>StudyMatch</h1>

            <section>
                <h2>Estado do sistema</h2>
                {estado ? (
                    <p className="ok">
                        {estado.aplicacao}: {estado.estado} | base de dados: {estado.baseDeDados} |
                        estudantes registados: {estado.totalEstudantes}
                    </p>
                ) : (
                    <p className="erro">Backend indisponível</p>
                )}
            </section>

            <section>
                <h2>Novo estudante</h2>
                <form onSubmit={aoSubmeter}>
                    <input
                        name="numeroAluno"
                        placeholder="Número de aluno"
                        value={formulario.numeroAluno}
                        onChange={aoAlterarCampo}
                    />
                    <input
                        name="nome"
                        placeholder="Nome"
                        value={formulario.nome}
                        onChange={aoAlterarCampo}
                    />
                    <input
                        name="curso"
                        placeholder="Curso"
                        value={formulario.curso}
                        onChange={aoAlterarCampo}
                    />
                    <input
                        name="anoCurricular"
                        type="number"
                        min="1"
                        max="5"
                        value={formulario.anoCurricular}
                        onChange={aoAlterarCampo}
                    />
                    <button type="submit">Registar</button>
                </form>
                {erro && <p className="erro">{erro}</p>}
            </section>

            <section>
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
                        </tr>
                        </thead>
                        <tbody>
                        {estudantes.map((estudante) => (
                            <tr key={estudante.id}>
                                <td>{estudante.numeroAluno}</td>
                                <td>{estudante.nome}</td>
                                <td>{estudante.curso}</td>
                                <td>{estudante.anoCurricular}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </section>
        </main>
    )
}

export default App

