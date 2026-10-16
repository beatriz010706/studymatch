import { useEffect, useState } from 'react'
import { obterPerfil } from '../services/api.js'

const NOMES_ORIGEM = {
  ACADEMICA: 'Notas',
  AUTO_DECLARADA: 'Auto-declarada',
  MISTA: 'Notas + auto-declaração',
  DESCONHECIDA: 'Sem dados',
}

function descreverItem(item) {
  if (item.tipo === 'NOTA') {
    return item.fonte + ': nota ' + item.nota + ' (peso ' + item.peso + ')'
  }
  return 'Questionário: nível ' + item.nivel + ' (peso ' + item.peso.toFixed(2) + ')'
}

// Perfil do estudante, com a explicação de cada competência ("Porquê este valor?")
function PerfilPagina({ estudante }) {
  const [perfil, setPerfil] = useState(null)
  const [explicacoesAbertas, setExplicacoesAbertas] = useState({})
  const [erro, setErro] = useState('')

  async function carregar() {
    try {
      setErro('')
      setPerfil(await obterPerfil(estudante.id))
    } catch (excecao) {
      setPerfil(null)
      setErro(excecao.message)
    }
  }

  useEffect(() => {
    carregar()
  }, [estudante.id])

  function alternarExplicacao(codigo) {
    setExplicacoesAbertas({ ...explicacoesAbertas, [codigo]: !explicacoesAbertas[codigo] })
  }

  return (
    <div>
      <h2>Perfil de {estudante.nome}</h2>
      <button className="secundario" onClick={carregar}>
        Atualizar
      </button>
      {erro && <p className="erro">{erro}</p>}

      {perfil && (
        <div>
          <p>
            Maturidade do perfil: <strong>{Math.round(perfil.maturidade * 100)}%</strong>
            {perfil.maturidade === 0 && ' (ainda sem notas: perfil em cold start)'}
          </p>

          {perfil.competencias.map((competencia) => (
            <div key={competencia.codigo} className="competencia">
              <div className="cabecalho">
                <strong>{competencia.nome}</strong>
                <span className={'origem ' + competencia.origem}>{NOMES_ORIGEM[competencia.origem]}</span>
              </div>

              {competencia.nivel === null ? (
                <p className="sem-dados">Sem dados</p>
              ) : (
                <div>
                  <div className="barra">
                    <div className="enchimento" style={{ width: competencia.nivel + '%' }}></div>
                  </div>
                  <p>
                    Nível {competencia.nivel.toFixed(1)} / 100 | confiança{' '}
                    {Math.round(competencia.confianca * 100)}%
                  </p>
                </div>
              )}

              {competencia.explicacao.length > 0 && (
                <div>
                  <button className="secundario" onClick={() => alternarExplicacao(competencia.codigo)}>
                    {explicacoesAbertas[competencia.codigo] ? 'Esconder' : 'Porquê este valor?'}
                  </button>
                  {explicacoesAbertas[competencia.codigo] && (
                    <ul>
                      {competencia.explicacao.map((item, indice) => (
                        <li key={indice}>{descreverItem(item)}</li>
                      ))}
                    </ul>
                  )}
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

export default PerfilPagina
